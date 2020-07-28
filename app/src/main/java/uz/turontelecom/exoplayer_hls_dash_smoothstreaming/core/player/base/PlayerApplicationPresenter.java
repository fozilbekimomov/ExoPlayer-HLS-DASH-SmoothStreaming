package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base;

import android.content.Context;

import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.cache.Cache;


/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */

public class PlayerApplicationPresenter implements PlayerApplication.Presenter {

    private static Cache downloadCache;
    private Context context;
    private String userAgent = "UserAgent ExoPlayer_HLS_DASH_SmoothStreaming";


    public PlayerApplicationPresenter(Context context) {
        this.context = context;
    }

    @Override
    public DataSource.Factory buildDataSourceFactory(TransferListener listener) {
        return new DefaultDataSourceFactory(context, listener, buildHttpDataSourceFactory(listener));
    }

    @Override
    public HttpDataSource.Factory buildHttpDataSourceFactory(TransferListener listener) {
        return new DefaultHttpDataSourceFactory(userAgent, listener);
    }

    @Override
    public boolean useExtensionRenders() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }


}
