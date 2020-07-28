package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.util.AttributeSet
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckedTextView
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.RendererCapabilities
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.SelectionOverride
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider
import com.google.android.exoplayer2.ui.TrackNameProvider
import com.google.android.exoplayer2.util.Assertions
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.R
import java.util.*

/**
 * Created by [Fozilbek Imomov](mailto: fozilbekimomov@gmail.com)
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/28/20
 * @project ExoPlayer_HLS_DASH_SmoothStreaming
 */
class CustomTrackSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val selectableItemBackgroundResourceId: Int
    private val inflater: LayoutInflater
    private val disableView: CheckedTextView
    private val defaultView: CheckedTextView
    private val componentListener: ComponentListener
    var states = arrayOf(
        intArrayOf(-android.R.attr.state_checked),
        intArrayOf(-android.R.attr.state_pressed)
    )

    //    val colors = intArrayOf(Color.parseColor("#000000"), Color.parseColor("#0072D6"))
    //    int[] colors =
    //            {getContext().getColor(R.color.colorAccent), getContext().getColor(R.color.colorAccent)};
    private var allowAdaptiveSelections = false
    private var trackNameProvider: TrackNameProvider
    private var trackViews: Array<Array<CheckedTextView?>?>? = null
    private var trackSelector: DefaultTrackSelector? = null
    private var rendererIndex = 0
    private var trackGroups: TrackGroupArray? = null
    private var isDisabled = false
    private var override: SelectionOverride? = null
    private val colorsText: IntArray
        private get() = if (Build.VERSION.SDK_INT > 23) {
            intArrayOf(
                context.resources.getColor(R.color.colorAccent, context.theme),
                context.resources.getColor(R.color.colorAccent, context.theme)
            )
        } else {
            intArrayOf(
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorAccent)
            )
        }
    // Private methods.
    /**
     * Sets whether adaptive selections (consisting of more than one track) can be made using this
     * selection view.
     *
     *
     * For the view to enable adaptive selection it is necessary both for this feature to be
     * enabled, and for the target renderer to support adaptation between the available tracks.
     *
     * @param allowAdaptiveSelections Whether adaptive selection is enabled.
     */
    fun setAllowAdaptiveSelections(allowAdaptiveSelections: Boolean) {
        if (this.allowAdaptiveSelections != allowAdaptiveSelections) {
            this.allowAdaptiveSelections = allowAdaptiveSelections
            updateViews()
        }
    }

    /**
     * Sets whether an option is available for disabling the renderer.
     *
     * @param showDisableOption Whether the disable option is shown.
     */
    fun setShowDisableOption(showDisableOption: Boolean) {
        disableView.visibility = if (showDisableOption) View.VISIBLE else View.GONE
    }

    /**
     * Sets the [TrackNameProvider] used to generate the user visible name of each track and
     * updates the view with track names queried from the specified provider.
     *
     * @param trackNameProvider The [TrackNameProvider] to use.
     */
    fun setTrackNameProvider(trackNameProvider: TrackNameProvider?) {
        this.trackNameProvider = Assertions.checkNotNull(trackNameProvider)
        updateViews()
    }

    /**
     * Initialize the view to select tracks for a specified renderer using a [ ].
     *
     * @param trackSelector The [DefaultTrackSelector].
     * @param rendererIndex The index of the renderer.
     */
    fun init(trackSelector: DefaultTrackSelector?, rendererIndex: Int) {
        this.trackSelector = trackSelector
        this.rendererIndex = rendererIndex
        updateViews()
    }

    private fun updateViews() {
        // Remove previous per-track views.
        for (i in childCount - 1 downTo 3) {
            removeViewAt(i)
        }
        val trackInfo =
            if (trackSelector == null) null else trackSelector!!.currentMappedTrackInfo
        if (trackSelector == null || trackInfo == null) {
            // The view is not initialized.
            disableView.isEnabled = false
            defaultView.isEnabled = false
            return
        }
        disableView.isEnabled = true
        defaultView.isEnabled = true
        trackGroups = trackInfo.getTrackGroups(rendererIndex)
        val parameters = trackSelector!!.parameters
        isDisabled = parameters.getRendererDisabled(rendererIndex)
        override = parameters.getSelectionOverride(rendererIndex, trackGroups!!)

        // Add per-track views.
        trackViews = arrayOfNulls(trackGroups!!.length)
        for (groupIndex in 0 until trackGroups!!.length) {
            val group = trackGroups!![groupIndex]
            val enableAdaptiveSelections = (allowAdaptiveSelections
                    && trackGroups!![groupIndex].length > 1 && (trackInfo.getAdaptiveSupport(
                rendererIndex,
                groupIndex,
                false
            )
                    != RendererCapabilities.ADAPTIVE_NOT_SUPPORTED))
            trackViews!![groupIndex] = arrayOfNulls(group.length)
            for (trackIndex in 0 until group.length) {
                if (trackIndex == 0) {
                    addView(inflater.inflate(R.layout.exo_list_divider, this, false))
                }
                val trackViewLayoutId =
                    if (enableAdaptiveSelections) R.layout.custom_list_item_multy_choice else R.layout.custom_list_item_single_choice
                val trackView =
                    inflater.inflate(trackViewLayoutId, this, false) as CheckedTextView
                trackView.setBackgroundResource(selectableItemBackgroundResourceId)
                //                trackView.setCheckMarkTintList(colorsStateList);
                trackView.text = trackNameProvider.getTrackName(group.getFormat(trackIndex))
                if (trackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex)
                    == RendererCapabilities.FORMAT_HANDLED
                ) {
                    trackView.isFocusable = true
                    //                    trackView.settin
                    trackView.tag = Pair.create(groupIndex, trackIndex)
                    trackView.setOnClickListener(componentListener)
                } else {
                    trackView.isFocusable = false
                    trackView.isEnabled = false
                }
                trackViews!![groupIndex]?.set(trackIndex, trackView)
                addView(trackView)
            }
        }
        updateViewStates()
    }

    private fun updateViewStates() {
        disableView.isChecked = isDisabled
        defaultView.isChecked = !isDisabled && override == null
        for (i in trackViews!!.indices) {
            for (j in trackViews!![i]!!.indices) {
                trackViews!![i]!![j]!!.isChecked =
                    override != null && override!!.groupIndex == i && override!!.containsTrack(j)
            }
        }
    }

    private fun applySelection() {
        val parametersBuilder = trackSelector!!.buildUponParameters()
        parametersBuilder.setRendererDisabled(rendererIndex, isDisabled)
        if (override != null) {
            parametersBuilder.setSelectionOverride(rendererIndex, trackGroups!!, override)
        } else {
            parametersBuilder.clearSelectionOverrides(rendererIndex)
        }
        trackSelector!!.setParameters(parametersBuilder)
    }

    private fun onClick(view: View) {
        if (view === disableView) {
            onDisableViewClicked()
        } else if (view === defaultView) {
            onDefaultViewClicked()
        } else {
            onTrackViewClicked(view)
        }
        updateViewStates()
    }

    private fun onDisableViewClicked() {
        isDisabled = true
        override = null
    }

    private fun onDefaultViewClicked() {
        isDisabled = false
        override = null
    }

    private fun onTrackViewClicked(view: View) {
        isDisabled = false
        val tag = view.tag as Pair<Int, Int>
        val groupIndex = tag.first
        val trackIndex = tag.second
        if (override == null || override!!.groupIndex != groupIndex || !allowAdaptiveSelections) {
            // A new override is being started.
            override = SelectionOverride(groupIndex, trackIndex)
        } else {
            // An existing override is being modified.
            val overrideLength = override!!.length
            val overrideTracks = override!!.tracks
            if ((view as CheckedTextView).isChecked) {
                // Remove the track from the override.
                if (overrideLength == 1) {
                    // The last track is being removed, so the override becomes empty.
                    override = null
                    isDisabled = true
                } else {
                    val tracks = getTracksRemoving(
                        overrideTracks,
                        trackIndex
                    )
                    override = SelectionOverride(groupIndex, *tracks)
                }
            } else {
                val tracks =
                    getTracksAdding(overrideTracks, trackIndex)
                override = SelectionOverride(groupIndex, *tracks)
            }
        }
    }

    // Internal classes.
    private inner class ComponentListener : OnClickListener {
        override fun onClick(view: View) {
            this@CustomTrackSelectionView.onClick(view)
        }
    }

    companion object {
        /**
         * Gets a pair consisting of a dialog and the [TrackSelectionView] that will be shown by it.
         *
         * @param activity      The parent activity.
         * @param title         The dialog's title.
         * @param trackSelector The track selector.
         * @param rendererIndex The index of the renderer.
         * @return The dialog and the [TrackSelectionView] that will be shown by it.
         */
        fun getDialog(
            activity: Activity?,
            title: CharSequence?,
            trackSelector: DefaultTrackSelector?,
            rendererIndex: Int
        ): Pair<AlertDialog, CustomTrackSelectionView> {
            val builder =
                AlertDialog.Builder(activity, R.style.PlayerActivityDialog)

            // Inflate with the builder's context to ensure the correct style is used.
            val dialogInflater = LayoutInflater.from(builder.context)
            val dialogView =
                dialogInflater.inflate(R.layout.custom_exo_track_selection_dialog, null)
            val selectionView: CustomTrackSelectionView =
                dialogView.findViewById(R.id.exo_track_selection_view)
            selectionView.init(trackSelector, rendererIndex)
            val okClickListener =
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> selectionView.applySelection() }
            val dialog = builder
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, okClickListener)
                .setNegativeButton(android.R.string.cancel, null)
                .create()
            return Pair.create(dialog, selectionView)
        }

        private fun getTracksAdding(tracks: IntArray, addedTrack: Int): IntArray {
            var tracks = tracks
            tracks = Arrays.copyOf(tracks, tracks.size + 1)
            tracks[tracks.size - 1] = addedTrack
            return tracks
        }

        private fun getTracksRemoving(tracks: IntArray, removedTrack: Int): IntArray {
            val newTracks = IntArray(tracks.size - 1)
            var trackCount = 0
            for (track in tracks) {
                if (track != removedTrack) {
                    newTracks[trackCount++] = track
                }
            }
            return newTracks
        }
    }

    init {
        val attributeArray = context
            .theme
            .obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))
        selectableItemBackgroundResourceId = attributeArray.getResourceId(0, 0)
        attributeArray.recycle()
        inflater = LayoutInflater.from(context)
        componentListener =
            ComponentListener()
        trackNameProvider = DefaultTrackNameProvider(resources)

        // View for disabling the renderer.
        disableView = inflater.inflate(
            R.layout.custom_list_item_single_choice,
            this,
            false
        ) as CheckedTextView
        disableView.setBackgroundResource(selectableItemBackgroundResourceId)
        disableView.setText(R.string.exo_track_selection_none)
        disableView.isEnabled = false
        disableView.isFocusable = true
        disableView.setOnClickListener(componentListener)
        disableView.visibility = View.GONE
        addView(disableView)
        // Divider view.
        addView(inflater.inflate(R.layout.exo_list_divider, this, false))
        // View for clearing the override to allow the selector to use its default selection logic.
        defaultView = inflater.inflate(
            R.layout.custom_list_item_single_choice,
            this,
            false
        ) as CheckedTextView
        defaultView.setBackgroundResource(selectableItemBackgroundResourceId)
        defaultView.setText(R.string.exo_track_selection_auto)
        defaultView.isEnabled = false
        defaultView.isFocusable = true
        defaultView.setOnClickListener(componentListener)
        addView(defaultView)
    }
}