package pixel.kotlin.bassblog.ui.playlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_play_list.*
import pixel.kotlin.bassblog.R

class TrackListActivity : AppCompatActivity() {

    companion object {
        val CONTENT = "CONTENT"
        fun start(activity: Activity, content: String) {
            val intent = Intent(activity, TrackListActivity::class.java)
            intent.putExtra(CONTENT, content)
            activity.startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onContextItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val content = intent.getStringExtra(CONTENT)

        playlist_detail.settings.loadWithOverviewMode = true
        playlist_detail.settings.useWideViewPort = true
        playlist_detail.setInitialScale(200)
        playlist_detail.isVerticalScrollBarEnabled = false
        playlist_detail.isHorizontalScrollBarEnabled = false
        playlist_detail.loadData(content, "text/html; charset=utf-8", "UTF-8")
    }
}