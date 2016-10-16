package pixel.kotlin.bassblog.ui


import android.os.Bundle
import kotlinx.android.synthetic.main.play_music.*
import pixel.kotlin.bassblog.R

class MusicPlayerActivity : CommunicationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music)
        button_play_toggle.setOnClickListener { handleToggleClick() }


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

}
