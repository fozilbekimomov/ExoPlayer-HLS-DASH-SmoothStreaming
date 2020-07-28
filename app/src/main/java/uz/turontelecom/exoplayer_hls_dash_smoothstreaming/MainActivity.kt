package uz.turontelecom.exoplayer_hls_dash_smoothstreaming

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openPlayer(view: View) {
        val url = playUrl.text.toString()
        if (url.length > 5) {
            val bundle = bundleOf(PlayerActivity.KEY_CUSTOM_URL to url)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    fun openDemoPlayer(view: View) {
        val intent = Intent(this, PlayerActivity::class.java)
        startActivity(intent)
    }
}