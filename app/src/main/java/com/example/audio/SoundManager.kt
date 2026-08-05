package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(context: Context) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEnabled: Boolean = true
    var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun playMoveSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequencyHz = 440.0, durationMs = 35, volume = 0.2f)
        }
    }

    fun playRotateSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequencyHz = 660.0, durationMs = 45, volume = 0.3f)
        }
        triggerVibration(20, 100)
    }

    fun playSoftDropSound() {
        if (!soundEnabled) return
        scope.launch {
            playTone(frequencyHz = 220.0, durationMs = 30, volume = 0.25f)
        }
    }

    fun playHardDropSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSequence(
                listOf(
                    Tone(300.0, 40, 0.4f),
                    Tone(150.0, 60, 0.5f)
                )
            )
        }
        triggerVibration(50, 200)
    }

    fun playHoldSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSequence(
                listOf(
                    Tone(523.25, 40, 0.3f), // C5
                    Tone(659.25, 60, 0.3f)  // E5
                )
            )
        }
        triggerVibration(30, 150)
    }

    fun playLineClearSound(count: Int) {
        if (!soundEnabled) return
        scope.launch {
            val tones = when (count) {
                1 -> listOf(Tone(523.25, 80, 0.4f), Tone(659.25, 100, 0.4f)) // C5, E5
                2 -> listOf(Tone(523.25, 70, 0.4f), Tone(659.25, 70, 0.4f), Tone(783.99, 100, 0.4f)) // C5, E5, G5
                3 -> listOf(Tone(523.25, 60, 0.4f), Tone(659.25, 60, 0.4f), Tone(783.99, 60, 0.4f), Tone(1046.50, 120, 0.5f)) // C5, E5, G5, C6
                else -> listOf( // TETRIS!
                    Tone(523.25, 80, 0.5f),
                    Tone(659.25, 80, 0.5f),
                    Tone(783.99, 80, 0.5f),
                    Tone(1046.50, 80, 0.5f),
                    Tone(1318.51, 180, 0.6f)
                )
            }
            playToneSequence(tones)
        }
        val vibeDuration = when(count) {
            4 -> 120L
            else -> 60L
        }
        triggerVibration(vibeDuration, 220)
    }

    fun playGameOverSound() {
        if (!soundEnabled) return
        scope.launch {
            playToneSequence(
                listOf(
                    Tone(400.0, 100, 0.4f),
                    Tone(350.0, 100, 0.4f),
                    Tone(300.0, 100, 0.4f),
                    Tone(220.0, 250, 0.5f)
                )
            )
        }
        triggerVibration(200, 250)
    }

    private fun triggerVibration(durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Safe fallback if vibration permission or device lacks support
        }
    }

    private data class Tone(val freqHz: Double, val durationMs: Int, val volume: Float)

    private fun playToneSequence(tones: List<Tone>) {
        tones.forEach { tone ->
            playTone(tone.freqHz, tone.durationMs, tone.volume)
        }
    }

    private fun playTone(frequencyHz: Double, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (durationMs * sampleRate / 1000)
        val sample = DoubleArray(numSamples)
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val angle = 2.0 * Math.PI * i / (sampleRate / frequencyHz)
            sample[i] = sin(angle)
        }

        var idx = 0
        for (dVal in sample) {
            // Fade out envelope at end
            val fade = if (idx > numSamples - 200 && numSamples > 200) {
                (numSamples - idx).toFloat() / 200f
            } else 1.0f
            val valShort = (dVal * 32767 * volume * fade).toInt()
            buffer[idx++] = valShort.toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong())
            audioTrack.release()
        } catch (_: Exception) {
            // AudioTrack fallback safety
        }
    }
}
