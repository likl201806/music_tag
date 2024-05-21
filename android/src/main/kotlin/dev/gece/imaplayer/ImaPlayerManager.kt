package dev.gece.imaplayer

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodChannel

///特别提醒，com.google.android.exoplayer支持hls播放，可以直接通过url播放，无需经过HlsMediaSource。
// 而原来的androidx.media3:media3-exoplayer则不支持hls播放

import com.google.android.exoplayer2.source.hls.HlsMediaSource

@RequiresApi(Build.VERSION_CODES.N)
class ImaPlayerManager private constructor(
    private val context: Context,
    private val messenger: BinaryMessenger
) : Player.Listener {
    // 定义 ExoPlayer 和其他相关属性
    public var eventSink: EventSink? = null

    // Video Player
    public lateinit var player: ExoPlayer
    private lateinit var imaPlayerEqualizer: ImaPlayerEqualizer

    // Passed arguments
    private var videoUrl: Uri? = null
    private var imaTag: Uri? = null
    private var isMuted: Boolean = true
    private var isMixed: Boolean = true
    private var autoPlay: Boolean = true

    private var curState: String = ""

    companion object {
        private var instance: ImaPlayerManager? = null

        @RequiresApi(Build.VERSION_CODES.N)
        fun getInstance(
            context: Context,
            messenger: BinaryMessenger
        ): ImaPlayerManager {
            if (instance == null) {
                instance = ImaPlayerManager(context, messenger)
            }
            return instance!!
        }
    }

    fun initialize(args: Map<String, Any>?, result: MethodChannel.Result) {
        if (args != null) {
            videoUrl = Uri.parse(args["video_url"] as String?)
            imaTag = Uri.parse(args["ima_tag"] as String?)
            isMuted = args["is_muted"] as Boolean? == true
            isMixed = args["is_mixed"] as Boolean? ?: true
            autoPlay = args["auto_play"] as Boolean? ?: true
        } else {
            isMuted = true
            isMixed = true
            autoPlay = true
        }

        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        // Create an ExoPlayer and set it as the player for content and ads.
        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .setAudioAttributes(AudioAttributes.DEFAULT, !isMixed)
            .build()

        // 创建 ImaPlayerEqualizer 实例
        imaPlayerEqualizer = ImaPlayerEqualizer(player)

        player.playWhenReady = false
        if (isMuted) {
            player.volume = 0.0F
        }

        player.addListener(this)

        preparePlayer()

        result.success(true)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            ExoPlayer.STATE_IDLE -> {
                sendEvent("IDLE")
                curState = "IDLE"
            }
            ExoPlayer.STATE_READY -> {
                sendEvent("READY")
                curState = "READY"
            }
            ExoPlayer.STATE_BUFFERING -> {
                if (curState == "" || curState == "IDLE" || curState == "READY") {
                    sendEvent("LOADING")
                    curState = "LOADING"
                } else if (curState == "LOADING") {
                    sendEvent("BUFFERING")
                    curState = "BUFFERING"
                }
            }
            ExoPlayer.STATE_ENDED -> {
                sendEvent("ENDED")
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        sendEvent(if (isPlaying) "PLAYING" else "PAUSED")
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        sendEvent("ERROR")
        curState = "ERROR"
    }

    private fun preparePlayer() {
        if (videoUrl != null && imaTag != null){
            println("---android url: $videoUrl")
            val mediaItem = MediaItem.Builder().setUri(videoUrl)
                .setAdsConfiguration(imaTag?.let {
                    MediaItem.AdsConfiguration.Builder(it).build()
                }).build()

            player.setMediaItem(mediaItem)
            player.prepare()
        }
    }

//    public fun play(videoUrl: String?, result: MethodChannel.Result) {
//        if (videoUrl != null) {
//            this.videoUrl = Uri.parse(videoUrl)
//            player.stop()
//            player.clearMediaItems()
//            preparePlayer()
//        }
//        player.playWhenReady = true
//        if (player.isPlaying == false){
//            player.play()
//        }
//        result.success(true)
//    }
    public fun play(videoUrl: String?, result: MethodChannel.Result) {
        try {
            if (videoUrl != null) {
                this.videoUrl = Uri.parse(videoUrl)
                player.stop()
                player.clearMediaItems()
                preparePlayer()
            }
            player.playWhenReady = true
            if (!player.isPlaying) {
                player.play()
            }
            result.success(true)
        } catch (e: Exception) {
            result.error("PLAY_ERROR", "Error playing video: ${e.message}", null)
        }
    }

//    public fun setMediaUrl(videoUrl: String?, result: MethodChannel.Result) {
//        if (videoUrl != null) {
//            this.videoUrl = Uri.parse(videoUrl)
//            player.stop()
//            player.clearMediaItems()
//            preparePlayer()
//        }
//        result.success(true)
//    }
    public fun setMediaUrl(videoUrl: String?, result: MethodChannel.Result) {
        try {
            if (videoUrl != null) {
                this.videoUrl = Uri.parse(videoUrl)
                player.stop()
                player.clearMediaItems()
                preparePlayer()
            }
            result.success(true)
        } catch (e: Exception) {
            result.error("SET_MEDIA_URL_ERROR", "Error setting media URL: ${e.message}", null)
        }
    }

//    public fun pause(result: MethodChannel.Result) {
//        player.pause()
//        result.success(true)
//    }
    public fun pause(result: MethodChannel.Result) {
        try {
            player.pause()
            result.success(true)
        } catch (e: Exception) {
            result.error("PAUSE_ERROR", "Error pausing player: ${e.message}", null)
        }
    }

//    public fun isPlaying(result: MethodChannel.Result) {
//        if (player.isPlaying == true){
//            result.success(true)
//        }else{
//            result.success(false)
//        }
//    }
    public fun isPlaying(result: MethodChannel.Result) {
        try {
            result.success(player.isPlaying)
        } catch (e: Exception) {
            result.error("IS_PLAYING_ERROR", "Error checking if player is playing: ${e.message}", null)
        }
    }

//    public fun stop(result: MethodChannel.Result) {
//        player.stop()
//        player.clearMediaItems()
//        result.success(true)
//    }
    public fun stop(result: MethodChannel.Result) {
        try {
            player.stop()
            player.clearMediaItems()
            result.success(true)
        } catch (e: Exception) {
            result.error("STOP_ERROR", "Error stopping player: ${e.message}", null)
        }
    }

//    public fun seekTo(duration: Int?, result: MethodChannel.Result) {
//        if (duration != null) {
//            player.seekTo(duration.toLong())
//        }
//
//        result.success(duration != null)
//    }
    public fun seekTo(duration: Int?, result: MethodChannel.Result) {
        try {
            if (duration != null) {
                player.seekTo(duration.toLong())
                result.success(true)
            } else {
                result.success(false)
            }
        } catch (e: Exception) {
            result.error("SEEK_ERROR", "Error seeking to position: ${e.message}", null)
        }
    }

//    public fun setVolume(value: Double?, result: MethodChannel.Result) {
//        if (value != null) {
//            player.volume = 0.0.coerceAtLeast(1.0.coerceAtMost(value)).toFloat()
//        }
//
//        result.success(value != null)
//    }
    public fun setVolume(value: Double?, result: MethodChannel.Result) {
        try {
            if (value != null) {
                player.volume = 0.0.coerceAtLeast(1.0.coerceAtMost(value)).toFloat()
                result.success(true)
            } else {
                result.success(false)
            }
        } catch (e: Exception) {
            result.error("SET_VOLUME_ERROR", "Error setting volume: ${e.message}", null)
        }
    }

//    public fun getVolume(result: MethodChannel.Result) {
//        val speed = player.volume.toDouble()
//        result.success(speed)
//    }
    public fun getVolume(result: MethodChannel.Result) {
        try {
            result.success(player.volume.toDouble())
        } catch (e: Exception) {
            result.error("GET_VOLUME_ERROR", "Error getting volume: ${e.message}", null)
        }
    }

//    public fun setSpeed(value: Double?, result: MethodChannel.Result) {
//        if (value != null && value > 0.0) {
//            // 确保速度值在有效范围内，大于 0
//            val speed = 0.0.coerceAtLeast(value).toFloat()
//            player.setPlaybackSpeed(speed)
//            result.success(true)
//        } else {
//            result.success(false)
//        }
//    }
    public fun setSpeed(value: Double?, result: MethodChannel.Result) {
        try {
            if (value != null && value > 0.0) {
                val speed = 0.0.coerceAtLeast(value).toFloat()
                player.setPlaybackSpeed(speed)
                result.success(true)
            } else {
                result.success(false)
            }
        } catch (e: Exception) {
            result.error("SET_SPEED_ERROR", "Error setting speed: ${e.message}", null)
        }
    }

//    public fun getSpeed(result: MethodChannel.Result) {
//        val speed = player.playbackParameters.speed.toDouble()
//        result.success(speed)
//    }

    public fun getSpeed(result: MethodChannel.Result) {
        try {
            result.success(player.playbackParameters.speed.toDouble())
        } catch (e: Exception) {
            result.error("GET_SPEED_ERROR", "Error getting speed: ${e.message}", null)
        }
    }

//    public fun disposePlayer(result: MethodChannel.Result) {
//        player.removeListener(this)
//        player.release()
//        eventSink = null
//        result.success(true)
//    }
    public fun disposePlayer(result: MethodChannel.Result) {
        try {
            player.removeListener(this)
            player.release()
            eventSink = null
            result.success(true)
        } catch (e: Exception) {
            result.error("DISPOSE_ERROR", "Error disposing player: ${e.message}", null)
        }
    }

//    public fun getEqualizerSettings(result: MethodChannel.Result) {
//        val settings = imaPlayerEqualizer.getEqualizerSettings()
//        result.success(settings)
//    }
    public fun getEqualizerSettings(result: MethodChannel.Result) {
        try {
            val settings = imaPlayerEqualizer.getEqualizerSettings()
            result.success(settings)
        } catch (e: Exception) {
            result.error("GET_EQUALIZER_SETTINGS_ERROR", "Error getting equalizer settings: ${e.message}", null)
        }
    }

//    public fun setEqualizerSingleBand(args: Map<String, Any>?, result: MethodChannel.Result) {
//        if (args != null){
//            var index = args["index"] as Int
//            var bandLevel = args["bandLevel"] as Int
//            imaPlayerEqualizer.setBandLevel(index.toShort(), bandLevel.toShort())
//            result.success(true)
//        }else{
//            result.success(false);
//        }
//    }
    public fun setEqualizerSingleBand(args: Map<String, Any>?, result: MethodChannel.Result) {
        try {
            if (args != null) {
                val index = args["index"] as Int
                val bandLevel = args["bandLevel"] as Int
                imaPlayerEqualizer.setBandLevel(index.toShort(), bandLevel.toShort())
                result.success(true)
            } else {
                result.success(false)
            }
        } catch (e: Exception) {
            result.error("SET_EQUALIZER_BAND_ERROR", "Error setting equalizer band: ${e.message}", null)
        }
    }

//    public fun getVideoInfo(result: MethodChannel.Result) {
//        result.success(
//            hashMapOf(
//                "total_duration" to roundForTwo(player.contentDuration.toDouble()),
//                "buffered_position" to roundForTwo(player.bufferedPosition.toDouble()),
//                "current_position" to if (player.isPlayingAd) 0.0 else roundForTwo(player.currentPosition.toDouble()),
//                "height" to player.videoSize.height,
//                "width" to player.videoSize.width
//            )
//        )
//    }
    public fun getVideoInfo(result: MethodChannel.Result) {
        try {
            result.success(
                hashMapOf(
                    "total_duration" to roundForTwo(player.contentDuration.toDouble()),
                    "buffered_position" to roundForTwo(player.bufferedPosition.toDouble()),
                    "current_position" to if (player.isPlayingAd) 0.0 else roundForTwo(player.currentPosition.toDouble()),
                    "height" to player.videoSize.height,
                    "width" to player.videoSize.width
                )
            )
        } catch (e: Exception) {
            result.error("GET_VIDEO_INFO_ERROR", "Error getting video info: ${e.message}", null)
        }
    }

    public fun roundForTwo(value: Double?): Double {
        return "%.1f".format((value ?: 0.0) / 1000).toDouble()
    }

    public fun sendEvent(value: Any?) {
        eventSink?.success(
            value
        )
    }
}