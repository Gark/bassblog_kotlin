package pixel.kotlin.bassblog.player

import android.database.Cursor
import android.media.MediaPlayer
import android.util.Log
import pixel.kotlin.bassblog.service.IPlayback
import pixel.kotlin.bassblog.storage.BlogPost
import java.io.IOException
import java.util.*

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

    private fun handleOnComplete() {
        playNext()
    }

    override fun getBuffered(): Int {
        return mBuffered
    }

    override fun onCompletion(mp: MediaPlayer) {
//        var next: Song? = null
//        // There is only one limited play mode which is list, player should be stopped when hitting the list end
//        if (mPlayList.getPlayMode() === PlayMode.LIST && mPlayList.getPlayingIndex() === mPlayList.getNumOfSongs() - 1) {
//            // In the end of the list
//            // Do nothing, just deliver the callback
//        } else
//            if (mPlayList.getPlayMode() === PlayMode.SINGLE) {
//            next = mPlayList.getCurrentSong()
//            play()
//        } else {
//            val hasNext = mPlayList.hasNext(true)
//            if (hasNext) {
//                next = mPlayList.next()
//        play()
//            }
//        }
//        notifyComplete(mPlayList.getCurrentPost())

        playNext()
    }

    //    override fun playLast(): Boolean {
    override fun play(): Boolean {
        if (isPaused) {
            mPlayer.start()
            notifyPlayStatusChanged(true)
            return true
        }
        if (!mPlayList.isEmpty()) {
            val blogPost = mPlayList.getCurrentPost()
            try {
                mPlayer.reset()
                mPlayer.setDataSource(blogPost?.track)
                mPlayer.prepareAsync()
            } catch (e: IOException) {
                Log.e(TAG, "play: ", e)
                notifyPlayStatusChanged(false)
                return false
            }

            return true
        }
        return false
    }

    private fun handlePrepare(mp: MediaPlayer?) {
        mp?.start()
        if (mp != null) notifyPlayStatusChanged(true)

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

    private fun stop() {
        pause()
        isPaused = false
    }

    override fun playLast() {
        val duration = mPlayer.currentPosition
        if (duration > 20 * 1000) {
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

    override fun pause(): Boolean {
        if (mPlayer.isPlaying) {
            mPlayer.pause()
            isPaused = true
            notifyPlayStatusChanged(false)
            return true
        }
        return false
    }

    override fun isPlaying(): Boolean {
        return mPlayer.isPlaying
    }

    override fun getProgress(): Int {
        return mPlayer.currentPosition
    }

    override fun getDuration(): Int {
        return mPlayer.duration
    }

    override fun getPlayingSong(): BlogPost? {
        return mPlayList.getCurrentPost()
    }

    override fun seekTo(progress: Int) {
        if (mPlayList.isEmpty()) return
        mPlayer.seekTo(progress * mPlayer.duration / 100)
    }

    override fun nextPlayMode() : Int {
        return mPlayList.nextPlayMode()
    }

    override fun registerCallback(callback: IPlayback.Callback) {
        mCallbacks.add(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mCallbacks.remove(callback)
    }

    override fun removeCallbacks() {
        mCallbacks.clear()
    }

    override fun releasePlayer() {
        mPlayList.clear()
        mPlayer.reset()
        mPlayer.release()
    }

    override fun updatePlayList(cursor: Cursor?) {
        mPlayList.updatePlayList(cursor)
    }

    private fun notifyPlayStatusChanged(isPlaying: Boolean) {
        for (callback in mCallbacks) {
            callback.onPlayStatusChanged(isPlaying)
        }
    }

//    private fun notifyPlayLast(blogPost: BlogPost) {
//        for (callback in mCallbacks) {
//            callback.onSwitchLast(blogPost)
//        }
//    }
//
//    private fun notifyPlayNext(song: BlogPost) {
//        for (callback in mCallbacks) {
//            callback.onSwitchNext(song)
//        }
//    }
//
//    private fun notifyComplete(blogPost: BlogPost?) {
//        for (callback in mCallbacks) {
//            callback.onComplete(blogPost)
//        }
//    }
}
