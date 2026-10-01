package com.example.ui.components

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.sin

object AudioEjectorHelper {
  private var audioTrack: AudioTrack? = null
  private var isPlaying = false

  suspend fun startEjection(onProgress: (Float) -> Unit, durationMs: Long = 8000L) = withContext(Dispatchers.IO) {
    if (isPlaying) return@withContext
    isPlaying = true

    val sampleRate = 44100
    val minBufferSize = AudioTrack.getMinBufferSize(
      sampleRate,
      AudioFormat.CHANNEL_OUT_MONO,
      AudioFormat.ENCODING_PCM_16BIT
    )

    audioTrack = AudioTrack.Builder()
      .setAudioAttributes(
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_MEDIA)
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .build()
      )
      .setAudioFormat(
        AudioFormat.Builder()
          .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
          .setSampleRate(sampleRate)
          .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
          .build()
      )
      .setBufferSizeInBytes(minBufferSize)
      .setTransferMode(AudioTrack.MODE_STREAM)
      .build()

    audioTrack?.play()

    val buffer = ShortArray(minBufferSize / 2)
    val startTime = System.currentTimeMillis()
    var phase = 0.0

    while (isPlaying && (System.currentTimeMillis() - startTime < durationMs)) {
      val elapsed = System.currentTimeMillis() - startTime
      val progress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)
      onProgress(progress)

      // Frequency sweeps from 165Hz up to 440Hz with pulsing bursts to eject moisture
      val baseFreq = 165.0 + (progress * 275.0)
      val pulse = if ((elapsed / 250) % 2 == 0L) 1.0 else 0.4

      for (i in buffer.indices) {
        val angularFreq = 2.0 * Math.PI * baseFreq / sampleRate
        phase += angularFreq
        if (phase > 2 * Math.PI) phase -= 2 * Math.PI

        val sample = (sin(phase) * 32767.0 * pulse).toInt().toShort()
        buffer[i] = sample
      }

      audioTrack?.write(buffer, 0, buffer.size)
    }

    stopEjection()
    onProgress(1f)
  }

  fun stopEjection() {
    isPlaying = false
    try {
      audioTrack?.stop()
      audioTrack?.release()
    } catch (_: Exception) {}
    audioTrack = null
  }
}
