package pixel.kotlin.bassblog.player

import android.database.Cursor
import android.media.MediaPlayer
import android.util.Log
import pixel.kotlin.bassblog.service.IPlayback
import pixel.kotlin.bassblog.storage.BlogPost
import pixel.kotlin.bassblog.storage.BuildConfig
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class Player : IPlayback, MediaPlayer.OnCompletionListener {

    private val TAG = Player::class.java.name
    private val mPlayList = PlayList()
    private val mPlayer: MediaPlayer

    private var isPaused = false // TODO make states
    private var mBuffered = 0
    // Default size 2: for service and UI
    private val mCallbacks = ArrayList<IPlayback.Callback>(2)

    init {
        mPlayer = MediaPlayer()
        mPlayer.setOnCompletionListener { handleOnComplete() }
        mPlayer.setOnPreparedListener { mp -> handlePrepare(mp) }
        mPlayer.setOnBufferingUpdateListener { mediaPlayer, i -> handleBuffering(i) }
    }

    private fun handleBuffering(i: Int) {
        mBuffered = i
    }

    private fun handleOnComplete() = playNext()

    override fun getBuffered(): Int = mBuffered

    override fun onCompletion(mp: MediaPlayer) = playNext()

    fun play() {
        if (isPaused) {
            mPlayer.start()
            notifyPlayStatusChanged(true)
            return
        }
        if (!mPlayList.isEmpty()) {
            val blogPost = mPlayList.getCurrentPost()
            try {
                mPlayer.reset()
                mPlayer.setDataSource(blogPost?.track)
                mPlayer.prepareAsync()
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "play: ", e)
                notifyPlayStatusChanged(false)
            }
        }
    }

    fun pause() {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            isPaused = true
            notifyPlayStatusChanged(false)
        }
    }

    private fun handlePrepare(mp: MediaPlayer?) {
        mp?.start()
        if (mp != null) {
            notifyPlayStatusChanged(true)
        }
    }

    override fun play(post: BlogPost) {
        val currentPost = mPlayList.getCurrentPost()
        if (post == currentPost) {
            if (!isPaused) {
                play()
            }
        } else {
            stop()
            mPlayList.updateCurrent(post)
            notifyPlayStatusChanged(false)
            play()
        }
    }

    override fun toggle() {
        if (mPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    private fun stop() {
        pause()
        isPaused = false
    }

    override fun playLast() {
        val duration = mPlayer.currentPosition
        if (duration > TimeUnit.SECONDS.toMillis(20)) {
            seekTo(0)
        } else {
            stop()
            mPlayList.moveToPrevious()
            play()
        }
    }

    override fun playNext() {
        stop()
        mPlayList.moveToNext()
        play()
    }

    override fun isPlaying(): Boolean = mPlayer.isPlaying

    override fun getProgress(): Int = mPlayer.currentPosition

    override fun getDuration(): Int = mPlayer.duration

    override fun getPlayingSong(): BlogPost? = mPlayList.getCurrentPost()

    override fun nextPlayMode(): Int = mPlayList.nextPlayMode()

    override fun updatePlayList(cursor: Cursor?) = mPlayList.updatePlayList(cursor)


    override fun seekTo(progress: Int) {
        if (mPlayList.isEmpty()) return
        mPlayer.seekTo(progress * mPlayer.duration / 100)
    }

    override fun registerCallback(callback: IPlayback.Callback) {
        mCallbacks.add(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mCallbacks.remove(callback)
    }

    override fun releasePlayer() {
        mPlayList.clear()
        mPlayer.reset()
        mPlayer.release()
    }

    private fun notifyPlayStatusChanged(isPlaying: Boolean) {
        for (callback in mCallbacks) {
            callback.onPlayStatusChanged(isPlaying)
        }
    }
}
