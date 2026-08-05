package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.GameState

@Composable
fun GameOverDialog(
    gameState: GameState,
    onSaveScore: (playerName: String) -> Unit,
    onRestart: () -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var isScoreSaved by remember { mutableStateOf(false) }

    val isNewRecord = gameState.score > 0 && gameState.score >= gameState.highScore

    Dialog(onDismissRequest = { /* Cannot dismiss without action */ }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF2E1065))
                    )
                )
                .border(
                    2.dp,
                    if (isNewRecord) Color(0xFFFBBF24) else Color(0xFFFF1744),
                    RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isNewRecord) {
                    Text(
                        text = "🏆 YENİ REKOR! 🏆",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFBBF24)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "OYUN BİTTİ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF1744),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatSummaryItem(label = "SKOR", value = gameState.score.toString(), color = Color(0xFF00E5FF))
                    StatSummaryItem(label = "SATIR", value = gameState.linesCleared.toString(), color = Color(0xFF22C55E))
                    StatSummaryItem(label = "SEVİYE", value = gameState.level.toString(), color = Color(0xFFA855F7))
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (!isScoreSaved && gameState.score > 0) {
                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { if (it.length <= 12) playerName = it },
                        label = { Text("Oyuncu Adı", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("İsminizi girin...", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            onSaveScore(playerName)
                            isScoreSaved = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("SKORU KAYDET", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                } else if (isScoreSaved) {
                    Text(
                        text = "✓ Skorunuz kaydedildi!",
                        color = Color(0xFF22C55E),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("TEKRAR OYNA", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatSummaryItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
    }
}
