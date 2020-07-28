package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.app

import android.app.Application
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base.PlayerApplicationPresenter


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */


class App : Application() {
    var playerApplication: PlayerApplicationPresenter? = null
        private set

    override fun onCreate() {
        super.onCreate()
        playerApplication = PlayerApplicationPresenter(this)
    }
}