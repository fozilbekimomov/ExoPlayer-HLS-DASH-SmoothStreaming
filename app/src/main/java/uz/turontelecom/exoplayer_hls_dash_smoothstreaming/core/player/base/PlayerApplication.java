package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;


/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */

public interface PlayerApplication {

    interface Presenter {

        DataSource.Factory buildDataSourceFactory(TransferListener listener);

        HttpDataSource.Factory buildHttpDataSourceFactory(TransferListener listener);

        boolean useExtensionRenders();


    }

}
