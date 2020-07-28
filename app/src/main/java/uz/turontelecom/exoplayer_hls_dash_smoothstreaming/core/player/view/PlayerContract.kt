package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.view

import android.content.Context
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */


interface PlayerContract {
    interface View {
        val playerView: PlayerView?

        fun getPlayer(): Player?
        fun setPlayer(player: SimpleExoPlayer?)
        val context: Context?

        fun setPlayerListener(playerListener: MyPlayer.PlayerListener?)

        fun setVideosListUrl(
            list: Array<String?>?,
            currentPosition: Int,
            startPosition: Long
        )

        var videoUrl: String?

        fun setStartPosition(startPosition: Long)
        val currentPlayerIndex: Int
        val trackSelector: DefaultTrackSelector?

        fun play()
        fun pause()
        fun stop()
        val isPlaying: Boolean

        fun initialize()
        fun releasePlayer()
        fun updateTrackSelectorParameters()
        fun updateStartPosition()
        fun clearStartPosition()
        fun updateButtonVisibilities()
        fun showToast(messageId: Int)
        fun showToast(message: String?)
        fun onTrackChanged(positionIndex: Int)
        val applicationContext: Context?
    }

    interface Presenter {
        fun setup()
        val player: Player?

        fun setVideosListUrl(
            list: Array<String?>?,
            currentEpisodePos: Int,
            startPosition: Long
        )

        var videoUrl: String?

        fun setStartPosition(startPosition: Long)
        fun play()
        fun pause()
        fun stop()
        val isPlaying: Boolean

        fun initialize(isTV: Boolean)
        fun releasePlayer()
        fun updateTrackSelectorParameters()
        fun updateStartPosition()
        fun clearStartPosition()
        val trackSelector: DefaultTrackSelector?

    }
}
