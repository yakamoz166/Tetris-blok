package com.example.game

class TetrisEngine {
    private var bag = mutableListOf<TetrominoType>()

    fun initGame(currentHighScore: Int): GameState {
        bag.clear()
        val queue = refillBagAndGetNext(4)
        val firstPiece = Tetromino.create(queue.first())
        val remainingQueue = queue.drop(1)
        
        val emptyBoard = Array(20) { Array<TetrominoType?>(10) { null } }
        val ghostY = calculateGhostY(emptyBoard, firstPiece)

        return GameState(
            board = emptyBoard,
            activePiece = firstPiece,
            ghostY = ghostY,
            nextQueue = remainingQueue,
            score = 0,
            highScore = currentHighScore,
            level = 1,
            linesCleared = 0,
            comboCount = 0,
            isGameOver = false,
            isPaused = false,
            isStarted = true,
            canHold = true,
            holdPiece = null
        )
    }

    private fun refillBagAndGetNext(count: Int): List<TetrominoType> {
        val result = mutableListOf<TetrominoType>()
        while (result.size < count) {
            if (bag.isEmpty()) {
                bag.addAll(Tetromino.generate7Bag())
            }
            result.add(bag.removeAt(0))
        }
        return result
    }

    fun tick(state: GameState): GameState {
        if (!state.isStarted || state.isPaused || state.isGameOver || state.activePiece == null) {
            return state
        }

        // Try moving active piece down 1 cell
        val movedPiece = state.activePiece.copy(y = state.activePiece.y + 1)
        return if (isValidPosition(state.board, movedPiece)) {
            val newGhost = calculateGhostY(state.board, movedPiece)
            state.copy(activePiece = movedPiece, ghostY = newGhost)
        } else {
            // Piece cannot move down -> Lock piece into board
            lockPieceAndSpawnNext(state)
        }
    }

    fun moveLeft(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || state.activePiece == null) return state
        val moved = state.activePiece.copy(x = state.activePiece.x - 1)
        return if (isValidPosition(state.board, moved)) {
            val ghost = calculateGhostY(state.board, moved)
            state.copy(activePiece = moved, ghostY = ghost)
        } else state
    }

    fun moveRight(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || state.activePiece == null) return state
        val moved = state.activePiece.copy(x = state.activePiece.x + 1)
        return if (isValidPosition(state.board, moved)) {
            val ghost = calculateGhostY(state.board, moved)
            state.copy(activePiece = moved, ghostY = ghost)
        } else state
    }

    fun rotate(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || state.activePiece == null) return state
        val rotated = state.activePiece.rotatedClockwise()

        // Test wall kicks (0,0), (-1,0), (1,0), (0,-1), (-2,0), (2,0)
        val kickOffsets = listOf(
            0 to 0,
            -1 to 0,
            1 to 0,
            0 to -1,
            -2 to 0,
            2 to 0,
            0 to -2
        )

        for ((dx, dy) in kickOffsets) {
            val kickedPiece = rotated.copy(x = rotated.x + dx, y = rotated.y + dy)
            if (isValidPosition(state.board, kickedPiece)) {
                val ghost = calculateGhostY(state.board, kickedPiece)
                return state.copy(activePiece = kickedPiece, ghostY = ghost)
            }
        }
        return state
    }

    fun softDrop(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || state.activePiece == null) return state
        val moved = state.activePiece.copy(y = state.activePiece.y + 1)
        return if (isValidPosition(state.board, moved)) {
            val ghost = calculateGhostY(state.board, moved)
            state.copy(activePiece = moved, ghostY = ghost, score = state.score + 1)
        } else state
    }

    fun hardDrop(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || state.activePiece == null) return state
        val dropY = calculateGhostY(state.board, state.activePiece)
        val droppedDistance = dropY - state.activePiece.y
        val droppedPiece = state.activePiece.copy(y = dropY)
        val stateWithDropped = state.copy(
            activePiece = droppedPiece,
            score = state.score + (droppedDistance * 2),
            screenShakeTimestamp = System.currentTimeMillis(),
            isHardDropFlash = true
        )
        return lockPieceAndSpawnNext(stateWithDropped)
    }

    fun holdPiece(state: GameState): GameState {
        if (state.isPaused || state.isGameOver || !state.canHold || state.activePiece == null) return state

        val currentType = state.activePiece.type
        val nextQueue = state.nextQueue.toMutableList()

        val newActiveType: TetrominoType
        val newHoldType: TetrominoType = currentType

        if (state.holdPiece == null) {
            newActiveType = nextQueue.removeAt(0)
            if (nextQueue.size < 3) {
                nextQueue.addAll(refillBagAndGetNext(3 - nextQueue.size))
            }
        } else {
            newActiveType = state.holdPiece
        }

        val newActive = Tetromino.create(newActiveType)
        if (!isValidPosition(state.board, newActive)) {
            return state.copy(isGameOver = true)
        }

        val ghostY = calculateGhostY(state.board, newActive)
        return state.copy(
            activePiece = newActive,
            holdPiece = newHoldType,
            canHold = false,
            nextQueue = nextQueue,
            ghostY = ghostY
        )
    }

    private fun lockPieceAndSpawnNext(state: GameState): GameState {
        val piece = state.activePiece ?: return state

        // Copy current board
        val newBoard = Array(20) { r -> Array(10) { c -> state.board[r][c] } }

        // Place piece blocks onto board
        val matrix = piece.matrix
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] == 1) {
                    val boardX = piece.x + c
                    val boardY = piece.y + r
                    if (boardY in 0..19 && boardX in 0..9) {
                        newBoard[boardY][boardX] = piece.type
                    } else if (boardY < 0) {
                        // Locked above board -> Game Over
                        return state.copy(isGameOver = true)
                    }
                }
            }
        }

        // Check line clears
        val fullRows = mutableListOf<Int>()
        for (r in 0..19) {
            if (newBoard[r].all { it != null }) {
                fullRows.add(r)
            }
        }

        var newScore = state.score
        var newLines = state.linesCleared
        var newCombo = state.comboCount
        var newLevel = state.level
        val particles = mutableListOf<LineClearParticle>()
        val floatingTexts = state.floatingTexts.toMutableList()

        if (fullRows.isNotEmpty()) {
            newLines += fullRows.size
            newCombo += 1

            val basePoints = when (fullRows.size) {
                1 -> 100
                2 -> 300
                3 -> 500
                4 -> 800
                else -> 1000
            }

            val comboBonus = (newCombo - 1) * 50
            val linePoints = (basePoints + comboBonus) * state.level
            newScore += linePoints

            // Calculate level up (every 10 lines)
            newLevel = (newLines / 10) + 1

            // Clear full rows and shift upper rows down
            fullRows.sorted().forEach { clearedRow ->
                particles.add(LineClearParticle(row = clearedRow))
                for (r in clearedRow downTo 1) {
                    newBoard[r] = newBoard[r - 1].copyOf()
                }
                newBoard[0] = Array(10) { null }
            }

            // Floating text popup
            val textLabel = when (fullRows.size) {
                4 -> "TETRIS! +$linePoints"
                3 -> "TRIPLE! +$linePoints"
                2 -> "DOUBLE! +$linePoints"
                else -> "+$linePoints"
            }
            floatingTexts.add(FloatingText(text = textLabel, color = if (fullRows.size == 4) Tetromino.COLOR_MAP[TetrominoType.I]!! else Tetromino.COLOR_MAP[TetrominoType.L]!!))
        } else {
            newCombo = 0
        }

        // Spawn next piece
        val nextQueue = state.nextQueue.toMutableList()
        val nextType = nextQueue.removeAt(0)
        if (nextQueue.size < 3) {
            nextQueue.addAll(refillBagAndGetNext(4 - nextQueue.size))
        }

        val nextPiece = Tetromino.create(nextType)
        val isGameOver = !isValidPosition(newBoard, nextPiece)
        val ghostY = if (!isGameOver) calculateGhostY(newBoard, nextPiece) else 0

        val newHighScore = maxOf(state.highScore, newScore)

        return state.copy(
            board = newBoard,
            activePiece = if (isGameOver) null else nextPiece,
            ghostY = ghostY,
            nextQueue = nextQueue,
            score = newScore,
            highScore = newHighScore,
            level = newLevel,
            linesCleared = newLines,
            comboCount = newCombo,
            canHold = true,
            isGameOver = isGameOver,
            particles = particles,
            floatingTexts = floatingTexts
        )
    }

    private fun isValidPosition(board: Array<Array<TetrominoType?>>, piece: Tetromino): Boolean {
        val matrix = piece.matrix
        for (r in matrix.indices) {
            for (c in matrix[r].indices) {
                if (matrix[r][c] == 1) {
                    val boardX = piece.x + c
                    val boardY = piece.y + r

                    // Out of horizontal bounds
                    if (boardX < 0 || boardX >= 10) return false
                    // Below bottom
                    if (boardY >= 20) return false
                    // Overlaps filled cell
                    if (boardY >= 0 && board[boardY][boardX] != null) return false
                }
            }
        }
        return true
    }

    private fun calculateGhostY(board: Array<Array<TetrominoType?>>, piece: Tetromino): Int {
        var testY = piece.y
        while (isValidPosition(board, piece.copy(y = testY + 1))) {
            testY++
        }
        return testY
    }
}
