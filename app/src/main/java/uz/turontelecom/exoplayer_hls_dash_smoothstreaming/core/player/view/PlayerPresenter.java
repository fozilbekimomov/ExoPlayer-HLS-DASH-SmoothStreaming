package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.view;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.R;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base.PlayerApplication;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.base.PlayerApplicationPresenter;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.stream.PlayerBandwidthMeter;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.stream.PlayerBufferListener;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils.PlayerEventListener;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils.PlayerExtras;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils.PlayerListener;


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */

public class PlayerPresenter implements PlayerContract.Presenter, PlayerEventListener.EventListener, PlaybackPreparer, PlayerBufferListener.Listener, PlayerExtras {

    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    private static PlayerBandwidthMeter BANDWIDTH_METER = new PlayerBandwidthMeter(null, null,
            0, DefaultBandwidthMeter.DEFAULT_SLIDING_WINDOW_MAX_WEIGHT, Clock.DEFAULT);

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    boolean isTV = false;
    private PlayerApplication.Presenter playerApplication;
    private Context context;
    private PlayerContract.View cineramaPlayer;
    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;
    private PlayerListener playerListener;
    private TrackGroupArray lastSeenTrackGroupArray;
    private boolean startAutoPlay;
    private int startWindow;
    private long startPosition;
    private String videoUrl;
    private String[] videosListUrl;
    private Handler updateBufferHandler;
    private Runnable updateBufferRunnable;
    private String videoContainer;

    public PlayerPresenter(PlayerContract.View cineramaPlayer) {
        this.cineramaPlayer = cineramaPlayer;
        playerApplication = new PlayerApplicationPresenter(cineramaPlayer.getApplicationContext());
        this.context = cineramaPlayer.getApplicationContext();
    }

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false;
        }
        Throwable cause = e.getSourceException();
        while (cause != null) {
            if (cause instanceof BehindLiveWindowException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    @Override
    public void setup() {
        mediaDataSourceFactory = buildDataSourceFactory(true);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
        clearStartPosition();
        initUpdateBufferTimer();
    }

    private void initUpdateBufferTimer() {
        updateBufferHandler = new Handler();
        updateBufferRunnable = () -> {
            updateBufferData();
            updateBufferHandler.postDelayed(updateBufferRunnable, UPDATE_BUFFER_INFO);
        };
        updateBufferHandler.postDelayed(updateBufferRunnable, UPDATE_BUFFER_INFO);
    }

    /**
     * @return A new_stick DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidth) {
        if (playerApplication != null)
            return playerApplication.buildDataSourceFactory(useBandwidth ? BANDWIDTH_METER : null);
        else
            return null;
        //return new_stick DefaultDataSourceFactory(cineramaPlayer.getContext(), Util.getUserAgent(cineramaPlayer.getContext(), "CineramaPlayer"), BANDWIDTH_METER);
    }

    @Override
    public void setVideosListUrl(String[] list, int currentEpisodePos, long startPosition) {
        this.videosListUrl = list;
        this.startWindow = currentEpisodePos;
        this.startPosition = startPosition;
    }

    @Override
    public String getVideoUrl() {
        return this.videoUrl;
    }

    @Override
    public void setVideoUrl(String url) {
        this.videoUrl = url;
    }

    @Override
    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
        //this.startWindow = 0;
    }

    @Override
    public void play() {
        startAutoPlay = true;
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    public void pause() {
        startAutoPlay = false;
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    @Override
    public void stop() {
        startAutoPlay = false;
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop(true);
        }
    }

    @Override
    public boolean isPlaying() {
        return startAutoPlay;
    }

    @Override
    public void initialize(boolean isTV) {
        this.isTV = isTV;
        if (player == null) {
            Uri[] uris;
            if (videoUrl != null) {
                uris = new Uri[]{Uri.parse(videoUrl)};
            } else if (videosListUrl != null) {
                uris = new Uri[videosListUrl.length];
                for (int i = 0; i < videosListUrl.length; i++) {
                    uris[i] = Uri.parse(videosListUrl[i]);
                }
            } else {
                return;
            }
            if (Util.maybeRequestReadExternalStoragePermission((Activity) cineramaPlayer.getContext(), uris))
                // The player will be reinitialized if the permission is granted.
                return;

            Handler mainHandler = new Handler();
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                    playerApplication.useExtensionRenders()
                            ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory =
                    new DefaultRenderersFactory(cineramaPlayer.getContext());
            renderersFactory.setExtensionRendererMode(extensionRendererMode);

            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);
            lastSeenTrackGroupArray = null;

            DefaultAllocator allocator = new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE);

//            Log.d("XXXX", C.DEFAULT_BUFFER_SEGMENT_SIZE+"");
//            Log.d("XXXX", 32*1024+"");

//            DefaultLoadControl defaultLoadControl = new DefaultLoadControl();
            DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(allocator)
                    .setBufferDurationsMs(
                            PlayerExtras.MIN_BUFFER_MS_0,
                            PlayerExtras.MAX_BUFFER_MS,
                            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                    )
                    .createDefaultLoadControl();

//            DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
//                    .setAllocator(allocator)
//                    .setBufferDurationsMs(
//                            preferencesUtil.getBufferTime(),
//                            MAX_BUFFER_MS,
//                            BUFFER_FOR_PLAYBACK_MS,
//                            BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
//                    )
//                    .createDefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(context, renderersFactory, trackSelector, loadControl);
            player.addListener(new PlayerEventListener(this));
            player.setPlayWhenReady(startAutoPlay);
            player.addAnalyticsListener(new EventLogger(trackSelector));
            //player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            cineramaPlayer.setPlayer(player);

            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
//                mediaSources[i] = buildMediaSource(uris[i], adUriTag);
                mediaSources[i] = buildMediaSource(uris[i]);
            }
            mediaSource =
                    mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);
            mediaSource.addEventListener(mainHandler, new PlayerBufferListener(this));
        }

        boolean haveStartPosition = startWindow != C.INDEX_UNSET;
        if (haveStartPosition) {
            try {
                player.seekTo(startWindow, startPosition);
            } catch (Exception e) {
//                Log.i("TTTT", getClass().toString()+"->initialize: "+e);
            }
        }
        player.prepare(mediaSource, !haveStartPosition, false);
        if (!isTV) {
            player.seekTo(0);
        }
    }

    @Override
    public void releasePlayer() {
        if (player != null) {
            updateTrackSelectorParameters();
            updateStartPosition();
            player.stop();
            player.release();
            player = null;
            updateBufferHandler.removeCallbacks(updateBufferRunnable);
            updateBufferRunnable = null;
            mediaSource = null;
            trackSelector = null;
        }
    }

    @Override
    public void updateTrackSelectorParameters() {
        if (trackSelector != null) {
            trackSelectorParameters = trackSelector.getParameters();
        }
    }

    @Override
    public void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    @Override
    public void clearStartPosition() {
        startAutoPlay = true;
        startWindow = C.INDEX_UNSET;
        startPosition = C.TIME_UNSET;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                playerListener.onBufferLoading(true, 0);
                break;
            case Player.STATE_ENDED:
                playerListener.onMovieEnd(playWhenReady);
                break;
            default:
                playerListener.onBufferLoading(false, 0);
        }
        cineramaPlayer.updateButtonVisibilities();
    }

    @Override
    public void onPositionDiscontinuity(@Player.DiscontinuityReason int reason) {
//        if (player.getPlaybackError() != null) {
        // The user has performed a seek whilst in the error state. Update the resume position so
        // that if the user then retries, playback resumes from the position to which they seeked.
        //updateStartPosition();
//        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        if (isBehindLiveWindow(e)) {
            clearStartPosition();
            initialize(isTV);
        } else {
            updateStartPosition();
            cineramaPlayer.updateButtonVisibilities();
            //showControls();
        }
    }

    @Override
    @SuppressWarnings("ReferenceEquality")
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        cineramaPlayer.updateButtonVisibilities();
        if (cineramaPlayer != null) {
            cineramaPlayer.onTrackChanged(player.getCurrentWindowIndex());
        }

        if (trackGroups != lastSeenTrackGroupArray) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_VIDEO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    if (cineramaPlayer != null) {
                        cineramaPlayer.showToast(R.string.error_unsupported_video);
                    }
                }
                if (mappedTrackInfo.getTypeSupport(C.TRACK_TYPE_AUDIO)
                        == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                    if (cineramaPlayer != null) {
                        cineramaPlayer.showToast(R.string.error_unsupported_audio);
                    }
                }
            }
            lastSeenTrackGroupArray = trackGroups;
        }
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
//        playerListener.onBufferLoading(isLoading,);
    }

    private MediaSource buildMediaSource(Uri uri) {
        @C.ContentType int type = Util.inferContentType(uri);
        switch (type) {
            case C.TYPE_DASH:
                videoContainer = "DASH";
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(true))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                videoContainer = "SmoothStreaming";
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                videoContainer = "HLS";
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER: {

                return new ExtractorMediaSource.Factory(mediaDataSourceFactory).setExtractorsFactory(new DefaultExtractorsFactory()).createMediaSource(uri);
            }
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }


    @Override
    public SimpleExoPlayer getPlayer() {
        return player;
    }

    @Override
    public DefaultTrackSelector getTrackSelector() {
        return this.trackSelector;
    }

    @Override
    public void preparePlayback() {
        initialize(isTV);
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
    }

    @Override
    public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {
/*        Log.d("playerTest", "\n~~~~onLoadCompleted~~~~\n" +
                //"mediaPeriodID: " + mediaPeriodId.periodIndex +
                "\nloadEventInfo.bytesLoaded: " + loadEventInfo.bytesLoaded +
                "\nbuffer position: " + player.getBufferedPosition() +
                "\nbuffer percent: " + player.getBufferedPercentage() +
                "\nloadEventInfo.loadDurationMs: " + loadEventInfo.loadDurationMs);*/
        //updateBufferLoading();
    }

    private void updateBufferData() {
        if (player != null) {
            long currentPos = player.getCurrentPosition();
            long bufferPos = player.getBufferedPosition() - currentPos;
            int bufferDuration = (int) (bufferPos / 1000);
            if (bufferDuration < 0)
                bufferDuration = 0;
            playerListener.updateBufferData(bufferDuration, getNetworkBandwidth(), videoContainer);

            if (player.getPlaybackState() == Player.STATE_BUFFERING) {
                int percentage = (int) ((bufferPos * 100) / BUFFER_FOR_PLAYBACK_MS);
                percentage = (percentage > 100) ? 100 : (percentage < 0) ? 0 : percentage;
                //Log.d("playerTest", "PERCENT: " + percentage);
                playerListener.onBufferLoading(true, percentage);
            } else
                playerListener.onBufferLoading(false, 0);
        }
    }

    public float getNetworkBandwidth() {
        float speed = ((float) BANDWIDTH_METER.getBitrateEstimate() / 1048576); //mbit/s
        return (float) Math.round(speed * 100) / 100;
    }

    @Override
    public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaSourceEventListener.LoadEventInfo loadEventInfo, MediaSourceEventListener.MediaLoadData mediaLoadData) {

    }

}