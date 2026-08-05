package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameState
import com.example.game.Tetromino
import com.example.game.TetrominoType
import kotlin.math.abs

@Composable
fun TetrisBoard(
    gameState: GameState,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onRotate: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onHold: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dragXAccumulator by remember { mutableFloatStateOf(0f) }
    var dragYAccumulator by remember { mutableFloatStateOf(0f) }
    var touchStartX by remember { mutableFloatStateOf(0f) }
    var touchStartY by remember { mutableFloatStateOf(0f) }
    var touchStartTime by remember { mutableLongStateOf(0L) }

    val theme = gameState.activeTheme

    // Screen flash animation on hard drop
    val flashAlpha by animateFloatAsState(
        targetValue = if (gameState.isHardDropFlash) 0.35f else 0f,
        animationSpec = tween(durationMillis = 150),
        label = "flashAlpha"
    )

    Box(
        modifier = modifier
            .aspectRatio(10f / 20f)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(theme.boardBg)
            .border(
                width = 3.dp,
                color = theme.borderColor,
                shape = RoundedCornerShape(24.dp)
            )
            .pointerInput(gameState.isPaused, gameState.isGameOver) {
                if (gameState.isPaused || gameState.isGameOver) return@pointerInput

                detectTapGestures(
                    onTap = {
                        onRotate()
                    },
                    onDoubleTap = {
                        onHold()
                    },
                    onLongPress = {
                        onHold()
                    }
                )
            }
            .pointerInput(gameState.isPaused, gameState.isGameOver) {
                if (gameState.isPaused || gameState.isGameOver) return@pointerInput

                detectDragGestures(
                    onDragStart = { offset ->
                        dragXAccumulator = 0f
                        dragYAccumulator = 0f
                        touchStartX = offset.x
                        touchStartY = offset.y
                        touchStartTime = System.currentTimeMillis()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragXAccumulator += dragAmount.x
                        dragYAccumulator += dragAmount.y

                        val sizeWidth = size.width.toFloat()
                        val cellWidth = sizeWidth / 10f

                        // Horizontal drag threshold
                        if (abs(dragXAccumulator) >= cellWidth * 0.75f) {
                            if (dragXAccumulator > 0) {
                                onMoveRight()
                            } else {
                                onMoveLeft()
                            }
                            dragXAccumulator = 0f
                        }

                        // Vertical soft drop threshold
                        if (dragYAccumulator >= cellWidth * 0.85f) {
                            onSoftDrop()
                            dragYAccumulator = 0f
                        }
                    },
                    onDragEnd = {
                        val totalDragY = dragYAccumulator + (touchStartY - touchStartY) // relative y
                        val dragDuration = System.currentTimeMillis() - touchStartTime

                        // Upward swipe -> Hold piece
                        if (dragYAccumulator < -cellWidthPx(size.width) * 2f) {
                            onHold()
                        }
                        // Quick downward flick -> Hard drop
                        else if (dragYAccumulator > cellWidthPx(size.width) * 1.5f && dragDuration < 300) {
                            onHardDrop()
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boardW = 10
            val boardH = 20
            val cellW = size.width / boardW
            val cellH = size.height / boardH

            // 1. Draw Grid Lines
            for (col in 1 until boardW) {
                drawLine(
                    color = theme.gridLine,
                    start = Offset(col * cellW, 0f),
                    end = Offset(col * cellW, size.height),
                    strokeWidth = 1f
                )
            }
            for (row in 1 until boardH) {
                drawLine(
                    color = theme.gridLine,
                    start = Offset(0f, row * cellH),
                    end = Offset(size.width, row * cellH),
                    strokeWidth = 1f
                )
            }

            // 2. Draw Locked Board Blocks
            for (r in 0 until boardH) {
                for (c in 0 until boardW) {
                    val blockType = gameState.board[r][c]
                    if (blockType != null) {
                        drawBlock(
                            x = c * cellW,
                            y = r * cellH,
                            cellW = cellW,
                            cellH = cellH,
                            color = Tetromino.COLOR_MAP[blockType] ?: Color.Cyan,
                            glowColor = Tetromino.GLOW_MAP[blockType] ?: Color.Cyan,
                            isGhost = false
                        )
                    }
                }
            }

            // 3. Draw Ghost Piece Projection
            val piece = gameState.activePiece
            if (piece != null && !gameState.isGameOver) {
                val matrix = piece.matrix
                val ghostY = gameState.ghostY

                for (r in matrix.indices) {
                    for (c in matrix[r].indices) {
                        if (matrix[r][c] == 1) {
                            val boardX = piece.x + c
                            val boardY = ghostY + r
                            if (boardY in 0 until boardH && boardX in 0 until boardW) {
                                drawBlock(
                                    x = boardX * cellW,
                                    y = boardY * cellH,
                                    cellW = cellW,
                                    cellH = cellH,
                                    color = piece.color.copy(alpha = 0.25f),
                                    glowColor = Color.Transparent,
                                    isGhost = true
                                )
                            }
                        }
                    }
                }

                // 4. Draw Active Piece
                for (r in matrix.indices) {
                    for (c in matrix[r].indices) {
                        if (matrix[r][c] == 1) {
                            val boardX = piece.x + c
                            val boardY = piece.y + r
                            if (boardY in 0 until boardH && boardX in 0 until boardW) {
                                drawBlock(
                                    x = boardX * cellW,
                                    y = boardY * cellH,
                                    cellW = cellW,
                                    cellH = cellH,
                                    color = piece.color,
                                    glowColor = piece.glowColor,
                                    isGhost = false
                                )
                            }
                        }
                    }
                }
            }

            // 5. Draw Line Clear Sweep Animation
            gameState.particles.forEach { particle ->
                val y = particle.row * cellH
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White,
                            Color(0xFF00E5FF),
                            Color.White,
                            Color.Transparent
                        )
                    ),
                    topLeft = Offset(0f, y),
                    size = Size(size.width, cellH)
                )
            }
        }

        // Active Gesture Badge Pill Overlay
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x99000000))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF4ADE80))
                )
                Text(
                    text = "DOKUNMATİK KONTROLLER AKTİF",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.85f),
                    letterSpacing = 1.sp
                )
            }
        }

        // Hard drop impact flash overlay
        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = flashAlpha))
            )
        }
    }
}

private fun cellWidthPx(totalWidth: Int): Float = totalWidth / 10f

private fun DrawScope.drawBlock(
    x: Float,
    y: Float,
    cellW: Float,
    cellH: Float,
    color: Color,
    glowColor: Color,
    isGhost: Boolean
) {
    val padding = cellW * 0.06f
    val blockW = cellW - (padding * 2)
    val blockH = cellH - (padding * 2)
    val cornerRadius = CornerRadius(cellW * 0.18f, cellH * 0.18f)

    if (isGhost) {
        drawRoundRect(
            color = color,
            topLeft = Offset(x + padding, y + padding),
            size = Size(blockW, blockH),
            cornerRadius = cornerRadius,
            style = Stroke(
                width = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )
        )
    } else {
        // Base block fill
        drawRoundRect(
            color = color,
            topLeft = Offset(x + padding, y + padding),
            size = Size(blockW, blockH),
            cornerRadius = cornerRadius
        )

        // Inner bevel top-left highlight
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.45f), Color.Transparent)
            ),
            topLeft = Offset(x + padding + 1f, y + padding + 1f),
            size = Size(blockW - 2f, blockH * 0.4f),
            cornerRadius = cornerRadius
        )

        // Outer border stroke
        drawRoundRect(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(x + padding, y + padding),
            size = Size(blockW, blockH),
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.5f)
        )
    }
}
