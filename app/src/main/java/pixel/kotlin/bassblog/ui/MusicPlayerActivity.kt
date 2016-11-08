package pixel.kotlin.bassblog.ui


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.play_music_activity.*
import pixel.kotlin.bassblog.R
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : CommunicationActivity(), SeekBar.OnSeekBarChangeListener {

    private val INTERVAL = TimeUnit.SECONDS.toMillis(1)
    private val mHandler = Handler()
    private val runnable = Runnable { scheduleUpdater() }

    companion object {
        fun start(activity: Activity, view: View, text: String) {
            val intent = Intent(activity, MusicPlayerActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, text)
            activity.startActivity(intent, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_play_toggle.setOnClickListener { handleToggleClick() }
        button_play_next.setOnClickListener { handleNextClick() }
        button_play_last.setOnClickListener { handlePlayLast() }
        button_play_mode_toggle.setOnClickListener { handlePlayModeClick() }
        button_favorite_toggle.setOnClickListener { handleFavouriteClick() }

        seek_bar.setOnSeekBarChangeListener(this)
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


    private fun handleFavouriteClick() {
        mPlaybackService?.let {
            val mix = it.getPlayingSong()
            mix?.let {

                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                mix.favourite = !mix.favourite;
                realm.copyToRealmOrUpdate(mix)
                realm.commitTransaction()
                updateFavouriteButton(mix.favourite)
            }
        }
    }

    private fun handlePlayModeClick() {
        if (mPlaybackService == null) return
        button_play_mode_toggle.setImageResource(mPlaybackService!!.nextPlayMode())
    }

    private fun handlePlayLast() {
        if (mPlaybackService == null) return
        mPlaybackService!!.playLast()
    }

    private fun handleNextClick() {
        if (mPlaybackService == null) return
        mPlaybackService!!.playNext()
    }

    override fun onResume() {
        super.onResume()
        if (mPlaybackService == null) return
        if (mPlaybackService!!.isPlaying()) {
            mHandler.post(runnable)
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(runnable)
    }

    private fun scheduleUpdater() {
        mHandler.postDelayed(runnable, INTERVAL)
        if (mPlaybackService == null) return

        val progress = mPlaybackService?.getProgress() ?: 0
        val duration = mPlaybackService?.getDuration() ?: 1

        text_view_duration.text = convertSecondsToHMmSs(duration)
        if (mPlaybackService!!.isPlaying()) {
            text_view_progress.text = convertSecondsToHMmSs(progress)
            seek_bar.progress = 100 * progress / duration
            seek_bar.secondaryProgress = mPlaybackService!!.getBuffered()
        }
    }

    private fun handleToggleClick() {
        if (mPlaybackService == null) return
        mPlaybackService!!.toggle()
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        updatePlayToggle(isPlaying)
        updateSongData()
        if (isPlaying) {
            mHandler.post(runnable)
        } else {
            mHandler.removeCallbacks(runnable)
        }
    }

    private fun updateFavouriteButton(favourite: Boolean) {
        if (favourite) {
            button_favorite_toggle.setImageResource(R.drawable.ic_favorite_yes)
        } else {
            button_favorite_toggle.setImageResource(R.drawable.ic_favorite_no)
        }
    }

    fun updateSongData() {
        if (mPlaybackService == null) return

        button_play_mode_toggle.setImageResource(mPlaybackService!!.getPlayMode())

        val mix = mPlaybackService!!.getPlayingSong()
        text_view_name.text = mix?.title
        text_view_artist.text = mix?.label
        updateFavouriteButton(mix?.favourite ?: true)

        Picasso.with(applicationContext)
                .load(mix?.image)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(mix_image)
    }


    fun updatePlayToggle(play: Boolean) {
        button_play_toggle.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
    }

    fun convertSecondsToHMmSs(seconds: Int): String {
        val sec = seconds / 1000
        val s = sec % 60
        val m = sec / 60 % 60
        val h = sec / (60 * 60) % 24

        return if (h == 0) String.format("%02d:%02d", m, s) else String.format("%d:%02d:%02d", h, m, s)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            if (mPlaybackService == null) return
            mPlaybackService!!.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}