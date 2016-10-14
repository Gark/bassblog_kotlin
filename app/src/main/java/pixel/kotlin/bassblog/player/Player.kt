package pixel.kotlin.bassblog.player

import android.media.MediaPlayer
import pixel.kotlin.bassblog.service.IPlayback
import pixel.kotlin.bassblog.storage.BlogPost

class Player : IPlayback, MediaPlayer.OnCompletionListener {
    private val mPlayer: MediaPlayer

    init {
        mPlayer = MediaPlayer()
    }

    override fun onCompletion(mp: MediaPlayer) {

    }

    override fun play(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlayList(array: Array<BlogPost>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
