package com.example.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.GameState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HeaderScoreCard(
    gameState: GameState,
    onPauseToggle: () -> Unit,
    onOpenScores: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenGestureGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateIntAsState(targetValue = gameState.score, label = "score")
    val formattedScore = NumberFormat.getNumberInstance(Locale.US).format(animatedScore)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1C1B1F))
            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            // Top Header: Level tag & Score vs Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LEVEL ${String.format("%02d", gameState.level)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD0BCFF),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = formattedScore,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFFE6E1E5),
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "MEVCUT SKOR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE6E1E5).copy(alpha = 0.4f),
                        letterSpacing = 1.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenGestureGuide,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B2930))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gesture,
                            contentDescription = "Dokunmatik Rehber",
                            tint = Color(0xFFD0BCFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenScores,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B2930))
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Skor Tablosu",
                            tint = Color(0xFFFFD180),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B2930))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ayarlar",
                            tint = Color(0xFFE6E1E5).copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onPauseToggle,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B2930))
                    ) {
                        Icon(
                            imageVector = if (gameState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (gameState.isPaused) "Devam Et" else "Duraklat",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sub-metrics Row (High Score & Lines)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    label = "EN YÜKSEK",
                    value = gameState.highScore.toString(),
                    highlightColor = Color(0xFFFFD180),
                    modifier = Modifier.weight(1f)
                )

                MetricChip(
                    label = "TEMİZLENEN SATIR",
                    value = gameState.linesCleared.toString(),
                    highlightColor = Color(0xFFC0E8AD),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2B2930))
            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(12.dp))
            .padding(vertical = 6.dp, horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6E1E5).copy(alpha = 0.4f),
                letterSpacing = 0.8.sp
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
        }
    }
}

