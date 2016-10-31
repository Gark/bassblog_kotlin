package pixel.kotlin.bassblog.ui

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.play_music_fragmnet.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.widget.CircleTransform
import java.util.concurrent.TimeUnit

class PlayerFragment : BinderFragment(), SeekBar.OnSeekBarChangeListener {

    private val INTERVAL = TimeUnit.SECONDS.toMillis(1)
    private val mHandler = Handler()
    private val runnable = Runnable { scheduleUpdater() }
    private val CIRCLE_TRANSFORMATION = CircleTransform()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.play_music_fragmnet, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        top_panel.setOnClickListener { handleClick() }
        button_play_toggle.setOnClickListener { handleToggleClick() }
        button_play_toggle_top.setOnClickListener { handleToggleClick() }
        button_play_next.setOnClickListener { handleNextClick() }
        button_play_last.setOnClickListener { handlePlayLast() }
        button_play_mode_toggle.setOnClickListener { handlePlayModeClick() }

        seek_bar.setOnSeekBarChangeListener(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun setPanelVisibility(visibility: Int) {
        top_panel.visibility = visibility
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

    private fun handleClick() {
        if (activity is MainActivity) {
            (activity as MainActivity).toggle()
        }
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        if (activity == null) return

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
        text_view_name_top.text = song?.title
        text_view_artist.text = song?.label

        Picasso.with(activity)
                .load(song?.image)
                .resizeDimen(R.dimen.circle_image_size, R.dimen.circle_image_size)
                .centerCrop()
                .placeholder(R.drawable.default_record_album)
                .error(R.drawable.default_record_album)
                .transform(CIRCLE_TRANSFORMATION)
                .into(image_view_album)

        Picasso.with(activity)
                .load(song?.image)
                .into(image_view_album_top)
    }

    fun updatePlayToggle(play: Boolean) {
        button_play_toggle.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
        button_play_toggle_top.setImageResource(if (play) R.drawable.ic_pause else R.drawable.ic_play)
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
