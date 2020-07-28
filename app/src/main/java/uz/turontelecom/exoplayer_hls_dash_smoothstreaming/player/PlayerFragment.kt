package uz.turontelecom.exoplayer_hls_dash_smoothstreaming.player

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import kotlinx.android.synthetic.main.fragment_exo_player.*
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.R
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.base.BaseFragment
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.core.player.view.MyPlayer
import uz.turontelecom.tvmodule.ui.view.player.PlayerFragmentContract


/**
 * Created by <a href="mailto: fozilbekimomov@gmail.com" >Fozilbek Imomov</a>
 *
 * @author fozilbekimomov
 * @version 1.0
 * @date 7/27/20
 * @project Cinerama
 */


class PlayerFragment : BaseFragment(R.layout.fragment_exo_player), MyPlayer.PlayerListener,
    PlayerFragmentContract {

    private var playUrl: String = ""

    override fun afterCreate(view: View, bundle: Bundle?) {
        myPlayer.setPlayerListener(this)
    }

    override fun setPlayUrl(url: String, position: Long) {
        this.playUrl = url
        myPlayer.pause()
        myPlayer.releasePlayer()
        playMovie(url)
        myPlayer.initialize()
        myPlayer.play()
        myPlayer.getPlayer()?.seekTo(position)
    }

    override fun setPlayUrl(list: Array<String>, currentPosition: Int, startPosition: Long) {
        myPlayer.pause()
        myPlayer.releasePlayer()
        myPlayer.setVideosListUrl(list, currentPosition, startPosition)
        myPlayer.initialize()
        myPlayer.play()
    }


    private fun playMovie(movieUrl: String) {
        myPlayer.videoUrl = movieUrl
    }

    override fun onStreamButtonsUpdate(mappedTrackInfo: MappingTrackSelector.MappedTrackInfo?) {

    }

    override fun onVideoData(videoData: String?) {
        videoDataView.text = videoData
    }

    override fun onTrackChanged(positionIndex: Int) {

    }

    override fun onControllerVisibilityChange(visibility: Int) {

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
        myPlayer.releasePlayer()
        if (myPlayer.getPlayer() == null) {
            myPlayer.initialize()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        if (myPlayer.getPlayer() == null) {
            myPlayer.initialize()
        }

//        if (!ipPlayer.isPlaying) ipPlayer.play()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        myPlayer.releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        myPlayer.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        myPlayer.releasePlayer()
    }

    fun getPlayBackPosition(): Long {
        val pos = myPlayer.getPlayer()?.currentPosition
        pos?.let {
            return it
        }
        return 0
    }


}