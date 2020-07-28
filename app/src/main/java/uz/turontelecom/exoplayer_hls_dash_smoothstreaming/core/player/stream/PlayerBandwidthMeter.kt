package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.stream

import android.os.Handler
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Clock
import com.google.android.exoplayer2.util.SlidingPercentile

/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */
class PlayerBandwidthMeter @JvmOverloads constructor(
    private val eventHandler: Handler? = null,
    private val eventListener: BandwidthMeter.EventListener? = null,
    initialBitrateEstimate: Long = DEFAULT_INITIAL_BITRATE_ESTIMATE,
    maxWeight: Int = DEFAULT_SLIDING_WINDOW_MAX_WEIGHT,
    clock: Clock = Clock.DEFAULT
) : BandwidthMeter, TransferListener {
    private val slidingPercentile: SlidingPercentile = SlidingPercentile(maxWeight)
    private val clock: Clock
    private var streamCount = 0
    private var sampleStartTimeMs: Long = 0
    private var sampleBytesTransferred: Long = 0
    private val totalElapsedTimeMs: Long = 0
    private val totalBytesTransferred: Long = 0
    private var bitrateEstimate: Long


    @Deprecated("Use {@link Builder} instead.")
    constructor(
        eventHandler: Handler?,
        eventListener: BandwidthMeter.EventListener?,
        maxWeight: Int
    ) : this(
        eventHandler,
        eventListener,
        DEFAULT_INITIAL_BITRATE_ESTIMATE,
        maxWeight,
        Clock.DEFAULT
    )

    @Synchronized
    override fun getBitrateEstimate(): Long {
        return bitrateEstimate
    }

    override fun getTransferListener(): TransferListener? {
        return this
    }

    override fun addEventListener(
        eventHandler: Handler,
        eventListener: BandwidthMeter.EventListener
    ) {
    }

    override fun removeEventListener(eventListener: BandwidthMeter.EventListener) {}
    private fun updateBandwidthSpeed() {
        val nowMs = clock.elapsedRealtime()
        val sampleElapsedTimeMs = (nowMs - sampleStartTimeMs).toInt()
        if (sampleElapsedTimeMs > 0) {
            val bitsPerSecond =
                sampleBytesTransferred * 8000 / sampleElapsedTimeMs.toFloat()
            bitrateEstimate = bitsPerSecond.toLong()
        }
    }

    private fun notifyBandwidthSample(
        elapsedMs: Int,
        bytes: Long,
        bitrate: Long
    ) {
        if (eventHandler != null && eventListener != null) {
            eventHandler.post(Runnable {
                eventListener.onBandwidthSample(
                    elapsedMs,
                    bytes,
                    bitrate
                )
            })
        }
    }

    override fun onTransferInitializing(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
    }

    override fun onTransferStart(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        if (streamCount == 0) {
            sampleStartTimeMs = clock.elapsedRealtime()
        }
        streamCount++
        updateBandwidthSpeed()
    }

    override fun onBytesTransferred(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean,
        bytesTransferred: Int
    ) {
        sampleBytesTransferred += bytesTransferred.toLong()
        updateBandwidthSpeed()
    }

    override fun onTransferEnd(
        source: DataSource,
        dataSpec: DataSpec,
        isNetwork: Boolean
    ) {
        if (streamCount < 0) return
        val nowMs = clock.elapsedRealtime()
        val sampleElapsedTimeMs = (nowMs - sampleStartTimeMs).toInt()
        updateBandwidthSpeed()
        notifyBandwidthSample(sampleElapsedTimeMs, sampleBytesTransferred, bitrateEstimate)
        if (--streamCount > 0) {
            sampleStartTimeMs = nowMs
        }
        sampleBytesTransferred = 0
    }

    /**
     * Builder for a bandwidth meter.
     */
    class Builder {
        private var eventHandler: Handler? = null
        private var eventListener: BandwidthMeter.EventListener? = null
        private var initialBitrateEstimate: Long
        private var slidingWindowMaxWeight: Int
        private var clock: Clock

        /**
         * Sets an event listener for new_stick bandwidth estimates.
         *
         * @param eventHandler  A handler for events.
         * @param eventListener A listener of events.
         * @return This builder.
         * @throws IllegalArgumentException If the event handler or listener are null.
         */
        fun setEventListener(
            eventHandler: Handler?,
            eventListener: BandwidthMeter.EventListener?
        ): Builder {
            Assertions.checkArgument(eventHandler != null && eventListener != null)
            this.eventHandler = eventHandler
            this.eventListener = eventListener
            return this
        }

        /**
         * Sets the maximum weight for the sliding window.
         *
         * @param slidingWindowMaxWeight The maximum weight for the sliding window.
         * @return This builder.
         */
        fun setSlidingWindowMaxWeight(slidingWindowMaxWeight: Int): Builder {
            this.slidingWindowMaxWeight = slidingWindowMaxWeight
            return this
        }

        /**
         * Sets the initial bitrate estimate in bits per second that should be assumed when a bandwidth
         * estimate is unavailable.
         *
         * @param initialBitrateEstimate The initial bitrate estimate in bits per second.
         * @return This builder.
         */
        fun setInitialBitrateEstimate(initialBitrateEstimate: Long): Builder {
            this.initialBitrateEstimate = initialBitrateEstimate
            return this
        }

        /**
         * Sets the clock used to estimate bandwidth from data transfers. Should only be set for testing
         * purposes.
         *
         * @param clock The clock used to estimate bandwidth from data transfers.
         * @return This builder.
         */
        fun setClock(clock: Clock): Builder {
            this.clock = clock
            return this
        }

        /**
         * Builds the bandwidth meter.
         *
         * @return A bandwidth meter with the configured properties.
         */
        fun build(): PlayerBandwidthMeter {
            return PlayerBandwidthMeter(
                eventHandler, eventListener, initialBitrateEstimate, slidingWindowMaxWeight, clock
            )
        }

        /**
         * Creates a builder with default parameters and without listener.
         */
        init {
            initialBitrateEstimate = DEFAULT_INITIAL_BITRATE_ESTIMATE
            slidingWindowMaxWeight =
                DEFAULT_SLIDING_WINDOW_MAX_WEIGHT
            clock = Clock.DEFAULT
        }
    }

    companion object {
        /**
         * Default initial bitrate estimate in bits per second.
         */
        const val DEFAULT_INITIAL_BITRATE_ESTIMATE: Long = 0

        /**
         * Default maximum weight for the sliding window.
         */
        const val DEFAULT_SLIDING_WINDOW_MAX_WEIGHT = 2000
        private const val ELAPSED_MILLIS_FOR_ESTIMATE = 2000
        private const val BYTES_TRANSFERRED_FOR_ESTIMATE = 512 * 1024
    }

    /**
     * Creates a bandwidth meter with default parameters.
     */
    init {
        this.clock = clock
        bitrateEstimate = initialBitrateEstimate
    }
}