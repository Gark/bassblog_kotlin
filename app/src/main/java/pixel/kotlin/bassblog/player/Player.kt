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
    private var isPaused = false
    // Default size 2: for service and UI

    private val mCallbacks = ArrayList<IPlayback.Callback>(2)

    init {
        mPlayer = MediaPlayer()
        mPlayer.setOnCompletionListener { handleOnComplete() }
        mPlayer.setOnPreparedListener { mp -> handlePrepare(mp) }
    }


    private fun handleOnComplete() {
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
        notifyComplete(mPlayList.getCurrentPost())

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
                mPlayer.setDataSource(blogPost.track)
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

    override fun play(array: Array<BlogPost>): Boolean {
//        if (array.isEmpty()) return false
//        isPaused = false
//        updatePlayList(array)
//        return play()
        return false
    }


    override fun play(array: Array<BlogPost>, startIndex: Int): Boolean {
//        if (array.isEmpty() || startIndex < 0 || startIndex >= array.size) return false
//        isPaused = false
//        mIndex = startIndex // TODO optimise it
//        mPlayList.clear()
//        mPlayList.addAll(array)
//        return play()
        return false
    }

    override fun play(post: BlogPost): Boolean {
        return play() // TODO
    }

    override fun playLast(): Boolean {
//        isPaused = false
//        if (!mPlayList.isEmpty()) {
//            val last = mPlayList.last()
//            play()
//            notifyPlayLast(last)
//            return true
//        }
        return false
    }

    // TODO
    override fun playNext(): Boolean {
//        isPaused = false
////        val hasNext = mPlayList.hasNext(false)
////        if (hasNext) {
////            val next = mPlayList.next()
//        mIndex = 0
//        play()
//        notifyPlayNext(mPlayList[mIndex])
        return true
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

    override fun getPlayingSong(): BlogPost {
        return mPlayList.getCurrentPost()
    }

    override fun seekTo(progress: Int): Boolean {
        if (mPlayList.isEmpty()) return false
        // TODO
//        val currentSong = mPlayList[mIndex]
//            if (currentSong!!.getDuration() <= progress) {
//                onCompletion(mPlayer)
//            } else {
//                mPlayer.seekTo(progress)
//            return true
//        }
        return false
    }

    override fun setPlayMode(playMode: PlayMode) {
//        mPlayList.setPlayMode(playMode)
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

    private fun notifyPlayLast(blogPost: BlogPost) {
        for (callback in mCallbacks) {
            callback.onSwitchLast(blogPost)
        }
    }

    private fun notifyPlayNext(song: BlogPost) {
        for (callback in mCallbacks) {
            callback.onSwitchNext(song)
        }
    }

    private fun notifyComplete(blogPost: BlogPost) {
        for (callback in mCallbacks) {
            callback.onComplete(blogPost)
        }
    }
}
