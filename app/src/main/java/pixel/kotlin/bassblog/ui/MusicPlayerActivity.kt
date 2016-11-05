package pixel.kotlin.bassblog.ui


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityOptionsCompat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.play_music_fragmnet.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.widget.CircleTransform
import java.util.concurrent.TimeUnit

class MusicPlayerActivity : CommunicationActivity(), SeekBar.OnSeekBarChangeListener {

    private val INTERVAL = TimeUnit.SECONDS.toMillis(1)
    private val mHandler = Handler()
    private val runnable = Runnable { scheduleUpdater() }
    private val CIRCLE_TRANSFORMATION = CircleTransform()

    companion object {
        fun start(activity: Activity, view: View, text: String) {
            val intent = Intent(activity, MusicPlayerActivity::class.java)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, text)
//            activity.startActivity(intent, options.toBundle())
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music_fragmnet)

        button_play_toggle.setOnClickListener { handleToggleClick() }
        button_play_next.setOnClickListener { handleNextClick() }
        button_play_last.setOnClickListener { handlePlayLast() }
        button_play_mode_toggle.setOnClickListener { handlePlayModeClick() }
        button_favorite_toggle.setOnClickListener { handleFavouriteClick() }

        seek_bar.setOnSeekBarChangeListener(this)
    }

    private fun handleFavouriteClick() {
        Toast.makeText(applicationContext, "Favourite", Toast.LENGTH_SHORT).show()
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
            image_view_album.resumeRotateAnimation()
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(runnable)
        image_view_album.pauseRotateAnimation()
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
            image_view_album.resumeRotateAnimation()
        } else {
            mHandler.removeCallbacks(runnable)
            image_view_album.pauseRotateAnimation()
        }
    }

    fun updateSongData() {
        if (mPlaybackService == null) return

        val song = mPlaybackService!!.getPlayingSong()
        text_view_name.text = song?.title
        text_view_artist.text = song?.label

        Picasso.with(applicationContext)
                .load(song?.image)
                .resizeDimen(R.dimen.circle_image_size, R.dimen.circle_image_size)
                .centerCrop()
                .placeholder(R.drawable.default_record_album)
                .error(R.drawable.default_record_album)
                .transform(CIRCLE_TRANSFORMATION)
                .into(image_view_album)
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