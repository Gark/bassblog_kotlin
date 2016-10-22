package pixel.kotlin.bassblog.ui


import android.os.Bundle
import android.os.Handler
import android.view.DragEvent
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.play_music.*
import pixel.kotlin.bassblog.R

class MusicPlayerActivity : CommunicationActivity(), SeekBar.OnSeekBarChangeListener {


    private val INTERVAL = 1000L
    private val mHandler = Handler()
    private val runnable = Runnable { scheduleUpdater() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music)
        button_play_toggle.setOnClickListener { handleToggleClick() }
        seek_bar.setOnSeekBarChangeListener(this)
    }


    override fun onResume() {
        super.onResume()
        mHandler.post(runnable)
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
            text_view_artist.text = "" + (100 * progress / duration)
            seek_bar.progress = 100 * progress / duration
            seek_bar.secondaryProgress = mPlaybackService!!.getBuffered()
        }
    }

    private fun handleToggleClick() {
        if (mPlaybackService == null) return

        if (mPlaybackService!!.isPlaying()) {
            mPlaybackService?.pause()
        } else {
            mPlaybackService?.play()
        }
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        super.onPlayStatusChanged(isPlaying)
        updatePlayToggle(isPlaying)
        if (isPlaying) {
            image_view_album.startRotateAnimation()
            image_view_album.setImageResource(R.mipmap.ic_launcher)
        } else {
            image_view_album.cancelRotateAnimation()
        }
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
