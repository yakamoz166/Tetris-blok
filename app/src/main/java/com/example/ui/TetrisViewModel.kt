package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.HighScoreEntity
import com.example.data.HighScoreRepository
import com.example.game.BoardTheme
import com.example.game.GameState
import com.example.game.TetrisEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.pow

class TetrisViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = HighScoreRepository(database.highScoreDao())
    val soundManager = SoundManager(application)
    private val engine = TetrisEngine()

    val topScores: StateFlow<List<HighScoreEntity>> = repository.topScores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val highestScore: StateFlow<Int> = repository.highestScore
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null

    init {
        // Observe highest score from database and initialize game
        viewModelScope.launch {
            highestScore.collect { hs ->
                if (!_gameState.value.isStarted) {
                    _gameState.update { it.copy(highScore = hs) }
                }
            }
        }
        restartGame()
    }

    fun restartGame() {
        gameLoopJob?.cancel()
        val hs = highestScore.value
        _gameState.value = engine.initGame(hs)
        startLoop()
    }

    private fun startLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                val state = _gameState.value
                if (state.isStarted && !state.isPaused && !state.isGameOver) {
                    val delayMs = calculateDropDelay(state.level)
                    delay(delayMs)
                    val nextState = engine.tick(_gameState.value)
                    
                    if (nextState.isGameOver && !_gameState.value.isGameOver) {
                        soundManager.playGameOverSound()
                    }
                    _gameState.value = nextState
                } else {
                    delay(200)
                }
            }
        }
    }

    private fun calculateDropDelay(level: Int): Long {
        // Drops faster as level increases (e.g. 800ms -> 650ms -> 530ms -> ...)
        val delay = (800.0 * 0.88.pow(level - 1)).toLong()
        return delay.coerceAtLeast(80L)
    }

    fun moveLeft() {
        val state = _gameState.value
        val newState = engine.moveLeft(state)
        if (newState != state) {
            _gameState.value = newState
            soundManager.playMoveSound()
        }
    }

    fun moveRight() {
        val state = _gameState.value
        val newState = engine.moveRight(state)
        if (newState != state) {
            _gameState.value = newState
            soundManager.playMoveSound()
        }
    }

    fun rotate() {
        val state = _gameState.value
        val newState = engine.rotate(state)
        if (newState != state) {
            _gameState.value = newState
            soundManager.playRotateSound()
        }
    }

    fun softDrop() {
        val state = _gameState.value
        val newState = engine.softDrop(state)
        if (newState != state) {
            _gameState.value = newState
            soundManager.playSoftDropSound()
        }
    }

    fun hardDrop() {
        val state = _gameState.value
        val oldLines = state.linesCleared
        val newState = engine.hardDrop(state)
        _gameState.value = newState
        soundManager.playHardDropSound()

        val linesCleared = newState.linesCleared - oldLines
        if (linesCleared > 0) {
            soundManager.playLineClearSound(linesCleared)
        }
    }

    fun holdPiece() {
        val state = _gameState.value
        if (state.canHold) {
            val newState = engine.holdPiece(state)
            if (newState != state) {
                _gameState.value = newState
                soundManager.playHoldSound()
            }
        }
    }

    fun togglePause() {
        _gameState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun setPaused(paused: Boolean) {
        _gameState.update { it.copy(isPaused = paused) }
    }

    fun changeTheme(theme: BoardTheme) {
        _gameState.update { it.copy(activeTheme = theme) }
    }

    fun saveScore(playerName: String) {
        val state = _gameState.value
        viewModelScope.launch {
            repository.saveScore(
                playerName = playerName,
                score = state.score,
                linesCleared = state.linesCleared,
                levelReached = state.level
            )
        }
    }

    fun clearAllScores() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun toggleSound(enabled: Boolean) {
        soundManager.soundEnabled = enabled
    }

    fun toggleHaptics(enabled: Boolean) {
        soundManager.hapticsEnabled = enabled
    }
}
