package uz.turontelecom.exoplayer_hls_dash_smoothstreaming

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_player.*
import uz.turontelecom.exoplayer_hls_dash_smoothstreaming.player.PlayerFragment

class PlayerActivity : AppCompatActivity() {

    private lateinit var playUrl: String
    private val playerFragment: PlayerFragment
        get() = myPlayerFragment as PlayerFragment

    val urlList = arrayOf("https://bae.sgp1.digitaloceanspaces.com/videos/121/dash/590b546f0d4e3896f611cd51d718e32f.mpd",
            "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8",
            "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8",
            "https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8",
            "https://multiplatform-f.akamaihd.net/i/multi/april11/sintel/sintel-hd_,512x288_450_b,640x360_700_b,768x432_1000_b,1024x576_1400_m,.mp4.csmil/master.m3u8",
            "https://cph-p2p-msl.akamaized.net/hls/live/2000341/test/master.m3u8",
            "http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_stereo_subs.m3u8"
    )

    companion object {
        val KEY_CUSTOM_URL = "video_url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        if (intent.extras != null) {
            if (intent.extras!!.containsKey(KEY_CUSTOM_URL)) {
                setPlayUrl(intent.extras!!.getString(KEY_CUSTOM_URL)!!)
            }
        } else {

//        setPlayUrl(urlList[0])
            setPlayUrl(urlList, 0, 0)
        }

    }

    fun setPlayUrl(url: String) {
        this.playUrl = url
        playerFragment.setPlayUrl(url, 0)
    }

    fun setPlayUrl(list: Array<String>, currentPosition: Int, startPosition: Long) {
        playerFragment.setPlayUrl(list, currentPosition, startPosition)
    }

}