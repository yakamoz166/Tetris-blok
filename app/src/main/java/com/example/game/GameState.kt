package com.example.game

import androidx.compose.ui.graphics.Color

enum class BoardTheme(
    val displayName: String,
    val bgStart: Color,
    val bgEnd: Color,
    val gridLine: Color,
    val boardBg: Color,
    val borderColor: Color,
    val accentColor: Color
) {
    IMMERSIVE_UI("Immersive Dark", Color(0xFF1C1B1F), Color(0xFF0F0E11), Color(0x2049454F), Color(0xFF0F0E11), Color(0xFF49454F), Color(0xFFD0BCFF)),
    NEON_CYBER("Siber Neon", Color(0xFF0D1117), Color(0xFF161B22), Color(0x2200E5FF), Color(0xFF0D1117), Color(0xFF00E5FF), Color(0xFF00E5FF)),
    MIDNIGHT_OLED("Gece Gezegeni", Color(0xFF000000), Color(0xFF0F0F1A), Color(0x228B5CF6), Color(0xFF05050A), Color(0xFF8B5CF6), Color(0xFFD0BCFF)),
    SYNTHWAVE("Synthwave", Color(0xFF1A0B2E), Color(0xFF2B0938), Color(0x33FF007F), Color(0xFF1A0B2E), Color(0xFFFF007F), Color(0xFFFF007F))
}

data class LineClearParticle(
    val row: Int,
    val progress: Float = 0f, // 0f to 1f
    val color: Color = Color.White
)

data class FloatingText(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val xRatio: Float = 0.5f,
    val yRatio: Float = 0.4f,
    val alpha: Float = 1f,
    val color: Color = Color.Yellow
)

data class GameState(
    val boardWidth: Int = 10,
    val boardHeight: Int = 20,
    val board: Array<Array<TetrominoType?>> = Array(20) { Array(10) { null } },
    val activePiece: Tetromino? = null,
    val ghostY: Int = 0,
    val holdPiece: TetrominoType? = null,
    val canHold: Boolean = true,
    val nextQueue: List<TetrominoType> = emptyList(),
    val score: Int = 0,
    val highScore: Int = 0,
    val level: Int = 1,
    val linesCleared: Int = 0,
    val comboCount: Int = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
    val isStarted: Boolean = false,
    val activeTheme: BoardTheme = BoardTheme.NEON_CYBER,
    val particles: List<LineClearParticle> = emptyList(),
    val floatingTexts: List<FloatingText> = emptyList(),
    val screenShakeTimestamp: Long = 0L,
    val isHardDropFlash: Boolean = false,
    val lastActionText: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GameState

        if (score != other.score) return false
        if (highScore != other.highScore) return false
        if (level != other.level) return false
        if (linesCleared != other.linesCleared) return false
        if (comboCount != other.comboCount) return false
        if (isGameOver != other.isGameOver) return false
        if (isPaused != other.isPaused) return false
        if (isStarted != other.isStarted) return false
        if (ghostY != other.ghostY) return false
        if (activePiece != other.activePiece) return false
        if (holdPiece != other.holdPiece) return false
        if (canHold != other.canHold) return false
        if (nextQueue != other.nextQueue) return false
        if (activeTheme != other.activeTheme) return false
        if (particles != other.particles) return false
        if (floatingTexts != other.floatingTexts) return false
        if (screenShakeTimestamp != other.screenShakeTimestamp) return false
        if (isHardDropFlash != other.isHardDropFlash) return false

        return true
    }

    override fun hashCode(): Int {
        var result = score
        result = 31 * result + highScore
        result = 31 * result + level
        result = 31 * result + linesCleared
        result = 31 * result + comboCount
        result = 31 * result + isGameOver.hashCode()
        result = 31 * result + isPaused.hashCode()
        result = 31 * result + isStarted.hashCode()
        result = 31 * result + ghostY
        result = 31 * result + (activePiece?.hashCode() ?: 0)
        result = 31 * result + (holdPiece?.hashCode() ?: 0)
        result = 31 * result + canHold.hashCode()
        result = 31 * result + nextQueue.hashCode()
        result = 31 * result + activeTheme.hashCode()
        return result
    }
}
