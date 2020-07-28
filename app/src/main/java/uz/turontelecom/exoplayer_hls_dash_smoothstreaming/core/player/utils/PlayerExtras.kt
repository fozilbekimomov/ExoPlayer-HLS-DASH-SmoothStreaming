package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.utils

/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */

interface PlayerExtras {
    companion object {
        /**
         * The default minimum duration of media that the player will attempt to ensure is buffered at all
         * times, in milliseconds.
         */
        const val DEFAULT_MIN_BUFFER_MS = 15000

        /**
         * The default maximum duration of media that the player will attempt to buffer, in milliseconds.
         */
        const val DEFAULT_MAX_BUFFER_MS = 30000

        /**
         * The default duration of media that must be buffered for playback to start or resume following a
         * user action such as a seek, in milliseconds.
         */
        const val DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500

        /**
         * The default duration of media that must be buffered for playback to resume after a rebuffer,
         * in milliseconds. A rebuffer is defined to be caused by buffer depletion rather than a user
         * action.
         */
        const val DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000
        const val UPDATE_BUFFER_INFO = 1000 //millis

        /**
         * The default minimum duration of media that the player will attempt to ensure is buffered at all
         * times, in milliseconds.
         *
         */
        const val MIN_BUFFER_MS_0 = 15000 //15 second
        const val MIN_BUFFER_MS_0_5 = 30000 // 30 second
        const val MIN_BUFFER_MS_1_5 = 90000 //90 second

        //    int MIN_BUFFER_MS_5 = 300000; //5 minute
        const val MIN_BUFFER_MS_15 = 600000 //10 minute
        //    int MIN_BUFFER_MS_30 = 1800000; //30 minute
        /**
         * The default maximum duration of media that the player will attempt to buffer, in milliseconds.
         */
        const val MAX_BUFFER_MS = 600000//10 minute

        /**
         * The default duration of media that must be buffered for playback to start or resume following a
         * user action such as a seek, in milliseconds.
         */
        const val BUFFER_FOR_PLAYBACK_MS = 1000 //original -> 10000

        /**
         * The default duration of media that must be buffered for playback to resume after a rebuffer,
         * in milliseconds. A rebuffer is defined to be caused by buffer depletion rather than a user
         * action.
         */
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 1000 // original -> 5000

    }
}