package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.view;

import android.content.Context;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.R;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils.PlayerErrorMessageProvider;
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils.PlayerListener;

import static android.media.MediaCodecList.ALL_CODECS;


/**
 * Created by Kholmatov Siyavushkhon (TuronTelecom) on 03.07.2018.
 */

public class MyPlayer extends RelativeLayout implements PlayerContract.View, PlayerControlView.VisibilityListener, PlaybackPreparer, View.OnClickListener, View.OnLongClickListener, View.OnTouchListener, PlayerListener {

    private PlayerView playerView;
    private ImageView live;
    private PlayerPresenter playerPresenter;
    private PlayerListener playerListener;
    private int SCREEN = 0;
    private String videoData;
    private int deltaValue = 1000;
    private String bufferText;

    public MyPlayer(Context context) {
        super(context);
        setup(context, null);
    }

    public MyPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public MyPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }

    private static MediaCodecInfo selectCodec(String mimeType) {
        MediaCodecList codecList = new MediaCodecList(ALL_CODECS);
        int numCodecs = codecList.getCodecInfos().length;
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = codecList.getCodecInfos()[i];

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (type.equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private void setup(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        if (getContext() != null) {
            playerPresenter = new PlayerPresenter(this);
            initView();
        }
    }

    private void initView() {
        inflate(getContext(), R.layout.player_layout, this);
        playerView = findViewById(R.id.player_view_tv);
        initPlayer();
    }

    private void initPlayer() {
        playerView.setControllerVisibilityListener(this);
        playerView.setErrorMessageProvider(new PlayerErrorMessageProvider(getContext()));
        playerView.requestFocus();

        playerPresenter.setPlayerListener(this);
        playerPresenter.setup();
        playerView.setUseController(true);

    }

    @Override
    public PlayerView getPlayerView() {
        return playerView;
    }

    @Override
    public SimpleExoPlayer getPlayer() {
        return playerPresenter.getPlayer();
    }

    @Override
    public void setPlayer(SimpleExoPlayer player) {
        playerView.setPlayer(player);
        playerView.setPlaybackPreparer(this);
    }


    @Override
    public void onVisibilityChange(int visibility) {

        if (playerListener != null) {
            playerListener.onControllerVisibilityChange(visibility);
        }
    }

    @Override
    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void setVideosListUrl(String[] list, int currentPosition, long startPosition) {
        playerPresenter.setVideosListUrl(list, currentPosition, startPosition);
    }

    @Override
    public String getVideoUrl() {
        return playerPresenter.getVideoUrl();
    }

    @Override
    public void setVideoUrl(String url) {
        playerPresenter.setVideoUrl(url);
    }

    @Override
    public void setStartPosition(long startPosition) {
        playerPresenter.setStartPosition(startPosition);
    }

    @Override
    public int getCurrentPlayerIndex() {
        if (getPlayer() != null)
            return getPlayer().getCurrentWindowIndex();
        return 0;
    }

    @Override
    public DefaultTrackSelector getTrackSelector() {
        if (playerPresenter == null)
            return null;
        return playerPresenter.getTrackSelector();
    }

    @Override
    public void play() {
        try {
            playerPresenter.play();
        } catch (Exception e) {
        }
    }

    @Override
    public void pause() {
        playerPresenter.pause();
    }

    @Override
    public void stop() {
        playerPresenter.stop();
    }


    @Override
    public boolean isPlaying() {
        return playerPresenter.isPlaying();
    }

    @Override
    public void initialize() {
        try {
            playerPresenter.initialize(true);

        } catch (Exception r) {
        }
    }

    @Override
    public void releasePlayer() {
        playerPresenter.releasePlayer();
    }

    @Override
    public void updateTrackSelectorParameters() {
        playerPresenter.updateTrackSelectorParameters();
    }

    @Override
    public void updateStartPosition() {
        playerPresenter.updateStartPosition();
    }

    @Override
    public void clearStartPosition() {
        playerPresenter.clearStartPosition();
    }

    @Override
    public void updateButtonVisibilities() {
        if (playerPresenter.getPlayer() == null || playerListener == null) {
            return;
        }
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = playerPresenter.getTrackSelector().getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null)
            playerListener.onStreamButtonsUpdate(mappedTrackInfo);
    }

    @Override
    public void showToast(int messageId) {
        Toast.makeText(getContext(), messageId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onTrackChanged(int positionIndex) {
        if (playerListener != null) {
            playerListener.onTrackChanged(positionIndex);
        }
    }

    @Override
    public Context getApplicationContext() {
        return getContext().getApplicationContext();
    }

    @Override
    public void preparePlayback() {
        initialize();
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onBufferLoading(boolean isLoading, int percentage) {
//        showToast("Isloading:"+isLoading);
//        if (isLoading) {
//            progressBar.setVisibility(View.VISIBLE);
//        } else {
//            progressBar.setVisibility(View.GONE);
//        }
    }

    @Override
    public void updateBufferData(int duration, float networkSpeed, String videoContainer) {

        String videoDecoder = "";
        String audioDecoder = "";

        if (getPlayer().getVideoFormat() != null) {
            if (selectCodec(getPlayer().getVideoFormat().sampleMimeType).getName() != null) {
                videoDecoder = selectCodec(getPlayer().getVideoFormat().sampleMimeType).getName();
            }
        }
        if (getPlayer().getAudioFormat() != null) {
            if (selectCodec(getPlayer().getAudioFormat().sampleMimeType).getName() != null) {
                audioDecoder = selectCodec(getPlayer().getAudioFormat().sampleMimeType).getName();
            }
        }

        bufferText = ("\n" + getContext().getString(R.string.buffer) + duration + " s\n" + getContext().getString(R.string.download) + networkSpeed + getContext().getString(R.string.speed));//\n"+getPlayer().getVideoFormat().width+"x"+getPlayer().getVideoFormat().height);
        if (getPlayer() != null && getPlayer().getVideoFormat() != null) {
            bufferText = bufferText.concat("\n" + getContext().getString(R.string.videoFormat) + getPlayer().getVideoFormat().width + "x" + getPlayer().getVideoFormat().height +
                            "\n" + getContext().getString(R.string.audio) + ((getPlayer().getAudioFormat() != null) ? getPlayer().getAudioFormat().sampleRate + "hz, " + getPlayer().getAudioFormat().sampleMimeType : ""
                    ) +
                            "\n" + getContext().getString(R.string.codec) + getPlayer().getVideoFormat().codecs +
                            "\n" + getContext().getString(R.string.frameRate) + getPlayer().getVideoFormat().frameRate +
                            "\n" + getContext().getString(R.string.videoContainer) + videoContainer +
                            "\n" + getContext().getString(R.string.videoDecoder) + videoDecoder +
                            "\n" + getContext().getString(R.string.audioDecoder) + audioDecoder +
                            "\n" + getContext().getString(R.string.bitrate) + getPlayer().getVideoFormat().bitrate / 1048576 + getContext().getString(R.string.speed)
            );
        }


        videoData = bufferText;
        playerListener.onVideoData(videoData);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return false;
    }

    @Override
    public void onMovieEnd(boolean playWhenReady) {
        if (playWhenReady) {
//            playerListener.onMovieEnded();
        }
    }

    public interface PlayerListener {

        void onControllerVisibilityChange(int visibility);

        void onStreamButtonsUpdate(MappingTrackSelector.MappedTrackInfo mappedTrackInfo);

        void onTrackChanged(int positionIndex);

        void onVideoData(@Nullable String videoData);
//
//        void onMovieEnded();
    }
}
