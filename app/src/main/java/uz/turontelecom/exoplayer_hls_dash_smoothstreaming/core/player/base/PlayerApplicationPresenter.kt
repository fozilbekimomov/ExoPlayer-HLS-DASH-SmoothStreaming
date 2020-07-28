package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base

import android.content.Context
import com.google.android.exoplayer2.BuildConfig
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.upstream.cache.Cache

/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */
class PlayerApplicationPresenter(private val context: Context) :
    PlayerApplication.Presenter {
    private val userAgent = "UserAgent ExoPlayer_HLS_DASH_SmoothStreaming"
    override fun buildDataSourceFactory(listener: TransferListener?): DataSource.Factory? {
        return DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(listener))
    }

    override fun buildHttpDataSourceFactory(listener: TransferListener?): HttpDataSource.Factory? {
        return DefaultHttpDataSourceFactory(userAgent, listener)
    }

    override fun useExtensionRenders(): Boolean {
        return "withExtensions" == BuildConfig.FLAVOR
    }

    companion object {
        private val downloadCache: Cache? = null
    }

}