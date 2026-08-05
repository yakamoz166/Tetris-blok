package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gesture
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.*

@Composable
fun TetrisScreen(
    viewModel: TetrisViewModel
) {
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val topScores by viewModel.topScores.collectAsStateWithLifecycle()

    var showGestureGuide by remember { mutableStateOf(false) }
    var showHighScoresSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F))
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Score Bar
            HeaderScoreCard(
                gameState = gameState,
                onPauseToggle = { viewModel.togglePause() },
                onOpenScores = { showHighScoresSheet = true },
                onOpenSettings = { showSettingsSheet = true },
                onOpenGestureGuide = { showGestureGuide = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Main Play Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Panel: Hold Piece Preview Box
                Column(
                    modifier = Modifier.width(68.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PiecePreviewBox(
                        title = "TUTULAN",
                        pieceType = gameState.holdPiece,
                        canHold = gameState.canHold,
                        onClick = { viewModel.holdPiece() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Gesture Tip Chip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2B2930))
                            .border(1.dp, Color(0xFF49454F), RoundedCornerShape(14.dp))
                            .clickable { showGestureGuide = true }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Gesture,
                                contentDescription = "Rehber",
                                tint = Color(0xFFD0BCFF),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dokunmatik\nKontrol",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE6E1E5).copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                lineHeight = 11.sp
                            )
                        }
                    }
                }

                // Center: Tetris Board Canvas
                TetrisBoard(
                    gameState = gameState,
                    onMoveLeft = { viewModel.moveLeft() },
                    onMoveRight = { viewModel.moveRight() },
                    onRotate = { viewModel.rotate() },
                    onSoftDrop = { viewModel.softDrop() },
                    onHardDrop = { viewModel.hardDrop() },
                    onHold = { viewModel.holdPiece() },
                    modifier = Modifier.weight(1f)
                )

                // Right Panel: Next Pieces Queue Column
                Column(
                    modifier = Modifier.width(68.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NextQueueColumn(nextQueue = gameState.nextQueue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Immersive Gesture Hints Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2B2930))
                    .border(1.dp, Color(0xFF49454F), RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GestureFooterItem(
                    icon = "🔄",
                    title = "DOKUN",
                    subtitle = "Dönüştür"
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFF49454F))
                )
                GestureFooterItem(
                    icon = "↔️",
                    title = "KAYDIR",
                    subtitle = "Sağa/Sola"
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(Color(0xFF49454F))
                )
                GestureFooterItem(
                    icon = "⬇️",
                    title = "AŞAĞI",
                    subtitle = "Hızlı Düşür"
                )
            }
        }

        // Dialogs & Sheets
        if (showGestureGuide) {
            GestureGuideOverlay(onDismiss = { showGestureGuide = false })
        }

        if (gameState.isPaused) {
            PauseDialog(
                onResume = { viewModel.setPaused(false) },
                onRestart = { viewModel.restartGame() },
                onGestureGuide = { showGestureGuide = true },
                onSettings = { showSettingsSheet = true }
            )
        }

        if (gameState.isGameOver) {
            GameOverDialog(
                gameState = gameState,
                onSaveScore = { name -> viewModel.saveScore(name) },
                onRestart = { viewModel.restartGame() }
            )
        }

        if (showHighScoresSheet) {
            HighScoresBottomSheet(
                scores = topScores,
                onDismiss = { showHighScoresSheet = false },
                onClearAll = { viewModel.clearAllScores() }
            )
        }

        if (showSettingsSheet) {
            SettingsBottomSheet(
                currentTheme = gameState.activeTheme,
                soundEnabled = viewModel.soundManager.soundEnabled,
                hapticsEnabled = viewModel.soundManager.hapticsEnabled,
                onThemeSelected = { viewModel.changeTheme(it) },
                onSoundToggled = { viewModel.toggleSound(it) },
                onHapticsToggled = { viewModel.toggleHaptics(it) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}

@Composable
private fun GestureFooterItem(
    icon: String,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFFE6E1E5).copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE6E1E5).copy(alpha = 0.8f),
                letterSpacing = 0.5.sp
            )
            Text(
                text = subtitle,
                fontSize = 8.sp,
                color = Color(0xFFE6E1E5).copy(alpha = 0.4f)
            )
        }
    }
}
