package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.TransferListener

/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */
interface PlayerApplication {
    interface Presenter {
        fun buildDataSourceFactory(listener: TransferListener?): DataSource.Factory?
        fun buildHttpDataSourceFactory(listener: TransferListener?): HttpDataSource.Factory?
        fun useExtensionRenders(): Boolean
    }
}