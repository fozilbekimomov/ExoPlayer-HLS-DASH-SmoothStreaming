package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */


interface PlayerListener {
    fun onBufferLoading(isLoading: Boolean, percent: Int)
    fun updateBufferData(
        duration: Int,
        networkSpeed: Float,
        type: String?
    )

    fun onMovieEnd(playWhenReady: Boolean)
}