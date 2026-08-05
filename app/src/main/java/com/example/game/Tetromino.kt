package com.example.game

import androidx.compose.ui.graphics.Color

enum class TetrominoType {
    I, J, L, O, S, T, Z
}

data class Tetromino(
    val type: TetrominoType,
    val rotationState: Int = 0, // 0, 1, 2, 3
    val x: Int = 3, // Column position on 10-wide board
    val y: Int = 0  // Row position on 20-high board
) {
    val matrix: Array<IntArray>
        get() = SHAPES[type]!![rotationState % 4]

    val color: Color
        get() = COLOR_MAP[type] ?: Color.Cyan

    val glowColor: Color
        get() = GLOW_MAP[type] ?: Color.Cyan

    fun rotatedClockwise(): Tetromino {
        return copy(rotationState = (rotationState + 1) % 4)
    }

    fun rotatedCounterClockwise(): Tetromino {
        return copy(rotationState = (rotationState + 3) % 4)
    }

    companion object {
        val COLOR_MAP = mapOf(
            TetrominoType.I to Color(0xFF80D8FF), // Pastel Cyan
            TetrominoType.J to Color(0xFFD0BCFF), // Pastel Lavender
            TetrominoType.L to Color(0xFFFFD180), // Soft Peach
            TetrominoType.O to Color(0xFFFFE082), // Warm Yellow
            TetrominoType.S to Color(0xFFC0E8AD), // Soft Lime
            TetrominoType.T to Color(0xFFE1BEE7), // Soft Lilac
            TetrominoType.Z to Color(0xFFFFB4AB)  // Soft Coral
        )

        val GLOW_MAP = mapOf(
            TetrominoType.I to Color(0x6080D8FF),
            TetrominoType.J to Color(0x60D0BCFF),
            TetrominoType.L to Color(0x60FFD180),
            TetrominoType.O to Color(0x60FFE082),
            TetrominoType.S to Color(0x60C0E8AD),
            TetrominoType.T to Color(0x60E1BEE7),
            TetrominoType.Z to Color(0x60FFB4AB)
        )

        // 4 Rotation states for each tetromino type (1 = filled block, 0 = empty)
        val SHAPES: Map<TetrominoType, List<Array<IntArray>>> = mapOf(
            TetrominoType.I to listOf(
                arrayOf(
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(1, 1, 1, 1),
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(0, 0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0),
                    intArrayOf(0, 0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(0, 0, 0, 0),
                    intArrayOf(1, 1, 1, 1),
                    intArrayOf(0, 0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0),
                    intArrayOf(0, 1, 0, 0)
                )
            ),
            TetrominoType.J to listOf(
                arrayOf(
                    intArrayOf(1, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 1)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0)
                )
            ),
            TetrominoType.L to listOf(
                arrayOf(
                    intArrayOf(0, 0, 1),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(1, 0, 0)
                ),
                arrayOf(
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.O to listOf(
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                ),
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(1, 1)
                )
            ),
            TetrominoType.S to listOf(
                arrayOf(
                    intArrayOf(0, 1, 1),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 0, 1)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(1, 1, 0)
                ),
                arrayOf(
                    intArrayOf(1, 0, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.T to listOf(
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 0)
                )
            ),
            TetrominoType.Z to listOf(
                arrayOf(
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 0, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 1),
                    intArrayOf(0, 1, 1),
                    intArrayOf(0, 1, 0)
                ),
                arrayOf(
                    intArrayOf(0, 0, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(0, 1, 1)
                ),
                arrayOf(
                    intArrayOf(0, 1, 0),
                    intArrayOf(1, 1, 0),
                    intArrayOf(1, 0, 0)
                )
            )
        )

        fun create(type: TetrominoType): Tetromino {
            val initialX = if (type == TetrominoType.O) 4 else 3
            return Tetromino(type = type, x = initialX, y = 0)
        }

        fun generate7Bag(): List<TetrominoType> {
            return TetrominoType.values().toList().shuffled()
        }
    }
}
