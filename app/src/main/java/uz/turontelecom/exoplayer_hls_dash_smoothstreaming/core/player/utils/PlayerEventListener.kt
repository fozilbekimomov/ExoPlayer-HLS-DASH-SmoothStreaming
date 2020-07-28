package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils

import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */


open class PlayerEventListener(var eventListener: EventListener) :
    Player.EventListener {
    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
        eventListener.onTracksChanged(trackGroups, trackSelections)
    }

    override fun onPlayerStateChanged(
        playWhenReady: Boolean,
        playbackState: Int
    ) {
        eventListener.onPlayerStateChanged(playWhenReady, playbackState)
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        eventListener.onPlayerError(error)
    }

    override fun onPositionDiscontinuity(reason: Int) {
        eventListener.onPositionDiscontinuity(reason)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        eventListener.onLoadingChanged(isLoading)
    }

    interface EventListener {
        fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        )

        fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int)
        fun onPlayerError(e: ExoPlaybackException?)
        fun onTracksChanged(
            trackGroups: TrackGroupArray?,
            trackSelections: TrackSelectionArray?
        )

        fun onLoadingChanged(isLoading: Boolean)
    }

}