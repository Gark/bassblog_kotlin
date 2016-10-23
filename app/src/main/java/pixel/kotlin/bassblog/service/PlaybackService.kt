package pixel.kotlin.bassblog.service

import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.os.IBinder
import pixel.kotlin.bassblog.player.PlayMode
import pixel.kotlin.bassblog.player.Player
import pixel.kotlin.bassblog.storage.BlogPost

class PlaybackService : Service(), IPlayback, IPlayback.Callback {


    inner class LocalBinder : Binder() {
        val service: PlaybackService
            get() = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        mPlayer?.releasePlayer()
        super.onDestroy()
    }

    //------------------------------------------------------------------------------------------------------------//

    private var mPlayer: Player? = null

    override fun onCreate() {
        super.onCreate()
        mPlayer = Player()
    }

    override fun updatePlayList(cursor: Cursor?) {
        mPlayer?.updatePlayList(cursor)
    }

    override fun play(): Boolean {
        return mPlayer!!.play()
    }

    override fun play(array: Array<BlogPost>): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun play(array: Array<BlogPost>, startIndex: Int): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun play(post: BlogPost): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playLast(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun playNext(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun pause(): Boolean {
        return mPlayer!!.pause()
    }

    override fun isPlaying(): Boolean {
        return mPlayer!!.isPlaying()
    }

    override fun getProgress(): Int {
        return mPlayer!!.getProgress()
    }

    override fun getDuration(): Int {
        return mPlayer!!.getDuration()
    }

    override fun getBuffered(): Int {
        return mPlayer!!.getBuffered()
    }

    override fun getPlayingSong(): BlogPost {
        return mPlayer!!.getPlayingSong()
    }

    override fun seekTo(progress: Int) {
        mPlayer!!.seekTo(progress)
    }

    override fun setPlayMode(playMode: PlayMode) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerCallback(callback: IPlayback.Callback) {
        mPlayer?.registerCallback(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mPlayer?.unregisterCallback(callback)
    }

    override fun removeCallbacks() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun releasePlayer() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSwitchLast(last: BlogPost?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSwitchNext(next: BlogPost) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onComplete(next: BlogPost) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {

    }


}
