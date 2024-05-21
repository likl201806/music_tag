package dev.gece.imaplayer

import com.google.android.exoplayer2.ExoPlayer
import android.media.audiofx.Equalizer
import com.google.android.exoplayer2.util.Log

class ImaPlayerEqualizer(private val player: ExoPlayer) {
    private var equalizer: Equalizer? = null

    init {
        initializeEqualizer()
    }

    private fun initializeEqualizer() {
        val audioSessionId = player.audioSessionId
        Log.d("ImaPlayerEqualizer", "---ima Initializing Equalizer with session ID: $audioSessionId")
        if (audioSessionId > 0) {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            Log.d("ImaPlayerEqualizer", "---ima Equalizer initialized")
        } else {
            Log.d("ImaPlayerEqualizer", "---ima Invalid audio session ID")
        }
    }

    fun getEqualizerSettings(): Map<String, Any>? {
        equalizer?.let { eq ->
            val settings = mutableMapOf<String, Any>()
            val numberOfBands = eq.numberOfBands
            settings["numberOfBands"] = numberOfBands
            val bandLevelRange = eq.bandLevelRange
            val minLevel = bandLevelRange[0]
            val maxLevel = bandLevelRange[1]
            settings["min_band_level"] = minLevel
            settings["max_band_level"] = maxLevel
            for (i in 0 until numberOfBands) {
                val bandFreqRange = eq.getBandFreqRange(i.toShort())
                val bandLevel = eq.getBandLevel(i.toShort())
                settings["band_frequency_range_$i"] = bandFreqRange
                settings["band_level_$i"] = bandLevel
            }
            return settings
        }
        return null
    }

    fun setBandLevel(bandIndex: Short, level: Short) {
        equalizer?.let { eq ->
            val bandLevelRange = eq.bandLevelRange
            val minLevel = bandLevelRange[0]
            val maxLevel = bandLevelRange[1]

            if (level in minLevel..maxLevel) {
                eq.setBandLevel(bandIndex, level)
            } else {
                throw IllegalArgumentException("Level must be within $minLevel to $maxLevel")
            }
        } ?: throw IllegalStateException("Equalizer is not initialized or released")
    }

    fun release() {
        equalizer?.release()
    }
}