package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HighScoreDao {
    @Query("SELECT * FROM high_scores ORDER BY score DESC LIMIT :limit")
    fun getTopScores(limit: Int = 10): Flow<List<HighScoreEntity>>

    @Query("SELECT MAX(score) FROM high_scores")
    fun getHighestScore(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(highScore: HighScoreEntity): Long

    @Query("DELETE FROM high_scores")
    suspend fun clearAllScores()
}
