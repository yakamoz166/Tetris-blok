package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.Tetromino
import com.example.game.TetrominoType

@Composable
fun PiecePreviewBox(
    title: String,
    pieceType: TetrominoType?,
    modifier: Modifier = Modifier,
    canHold: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2B2930))
            .border(
                width = 1.dp,
                color = if (!canHold) Color(0xFFFFB4AB).copy(alpha = 0.5f) else Color(0xFF49454F),
                shape = RoundedCornerShape(16.dp)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6E1E5).copy(alpha = 0.5f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1B1F))
            ) {
                if (pieceType != null) {
                    MiniPieceCanvas(
                        pieceType = pieceType,
                        alpha = if (canHold) 1f else 0.4f
                    )
                } else {
                    Text(
                        text = "—",
                        color = Color(0xFFE6E1E5).copy(alpha = 0.3f),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NextQueueColumn(
    nextQueue: List<TetrominoType>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2B2930))
            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(16.dp))
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "GELECEK",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6E1E5).copy(alpha = 0.5f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            val displayQueue = nextQueue.take(3)
            displayQueue.forEachIndexed { index, type ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(if (index == 0) 50.dp else 40.dp)
                        .padding(vertical = 2.dp)
                ) {
                    MiniPieceCanvas(
                        pieceType = type,
                        scale = if (index == 0) 1f else 0.75f
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPieceCanvas(
    pieceType: TetrominoType,
    alpha: Float = 1f,
    scale: Float = 1f
) {
    val matrix = Tetromino.SHAPES[pieceType]!![0]
    val color = Tetromino.COLOR_MAP[pieceType] ?: Color.Cyan

    Canvas(modifier = Modifier.fillMaxSize()) {
        val rows = matrix.size
        val cols = matrix[0].size

        val cellSize = (size.minDimension / 4f) * scale
        val startX = (size.width - (cols * cellSize)) / 2f
        val startY = (size.height - (rows * cellSize)) / 2f

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (matrix[r][c] == 1) {
                    val x = startX + (c * cellSize)
                    val y = startY + (r * cellSize)
                    val pad = cellSize * 0.08f

                    drawRoundRect(
                        color = color.copy(alpha = alpha),
                        topLeft = Offset(x + pad, y + pad),
                        size = Size(cellSize - (pad * 2), cellSize - (pad * 2)),
                        cornerRadius = CornerRadius(cellSize * 0.2f)
                    )

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.4f * alpha), Color.Transparent)
                        ),
                        topLeft = Offset(x + pad, y + pad),
                        size = Size(cellSize - (pad * 2), (cellSize - (pad * 2)) * 0.4f),
                        cornerRadius = CornerRadius(cellSize * 0.2f)
                    )
                }
            }
        }
    }
}
