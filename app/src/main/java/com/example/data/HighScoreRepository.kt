package com.example.data

import kotlinx.coroutines.flow.Flow

class HighScoreRepository(private val highScoreDao: HighScoreDao) {
    val topScores: Flow<List<HighScoreEntity>> = highScoreDao.getTopScores(10)
    val highestScore: Flow<Int?> = highScoreDao.getHighestScore()

    suspend fun saveScore(playerName: String, score: Int, linesCleared: Int, levelReached: Int) {
        if (score <= 0) return
        val entity = HighScoreEntity(
            playerName = playerName.ifBlank { "Oyuncu" },
            score = score,
            linesCleared = linesCleared,
            levelReached = levelReached
        )
        highScoreDao.insertScore(entity)
    }

    suspend fun clearAll() {
        highScoreDao.clearAllScores()
    }
}
