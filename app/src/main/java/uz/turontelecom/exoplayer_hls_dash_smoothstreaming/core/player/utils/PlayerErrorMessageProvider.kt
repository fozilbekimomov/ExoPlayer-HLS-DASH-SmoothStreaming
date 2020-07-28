package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils

import android.content.Context
import android.util.Pair
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil
import com.google.android.exoplayer2.util.ErrorMessageProvider
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.R


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */


class PlayerErrorMessageProvider(var context: Context) :
    ErrorMessageProvider<ExoPlaybackException> {
    override fun getErrorMessage(e: ExoPlaybackException): Pair<Int, String> {
        var errorString = context.getString(R.string.error_generic)
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            val cause = e.rendererException
            if (cause is MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                errorString = if (cause.codecInfo == null) {
                    if (cause.cause is MediaCodecUtil.DecoderQueryException) {
                        context.getString(R.string.error_querying_decoders)
                    } else if (cause.secureDecoderRequired) {
                        context.getString(
                            R.string.error_no_secure_decoder,
                            cause.mimeType
                        )
                    } else {
                        context.getString(
                            R.string.error_no_decoder,
                            cause.mimeType
                        )
                    }
                } else {
                    context.getString(
                        R.string.error_instantiating_decoder,
                        cause.codecInfo
                    )
                }
            }
        }
        return Pair.create(0, errorString)
    }

}