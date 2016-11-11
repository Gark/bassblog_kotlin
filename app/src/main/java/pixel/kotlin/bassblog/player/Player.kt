package pixel.kotlin.bassblog.player

import android.media.MediaPlayer
import android.util.Log
import pixel.kotlin.bassblog.BuildConfig
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.service.IPlayback
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class Player : IPlayback, MediaPlayer.OnCompletionListener {

    private val mCallbacks = ArrayList<IPlayback.Callback>()
    private val TAG = Player::class.java.name
    private val mPlayList = PlayList()
    private val mPlayer: MediaPlayer

    private var mBuffered = 0

    init {
        mPlayer = MediaPlayer()
        mPlayer.setOnCompletionListener { handleOnComplete() }
        mPlayer.setOnPreparedListener { mp -> handlePrepare() }
        mPlayer.setOnBufferingUpdateListener { mediaPlayer, i -> handleBuffering(i) }
    }

    private fun handleBuffering(i: Int) {
        mBuffered = i
    }

    private fun handleOnComplete() = playNext()

    override fun getBuffered(): Int = mBuffered

    override fun onCompletion(mp: MediaPlayer) = playNext()

    fun play() {
        mPlayer.start()
        notifyPlayStatusChanged(true)
    }

    fun pause() {
        mPlayer.pause()
        notifyPlayStatusChanged(false)
    }

    private fun handlePrepare() {
        play()
    }

    override fun play(mix: Mix, tab: Int) {
        val currentMix = mPlayList.getCurrentMix()
        if (mix == currentMix) {
            toggle()
        } else {
            stop()
            mPlayList.updateCurrent(mix, tab)
            prepareAdnPlay()
        }
    }

    override fun toggle() {
        if (mPlayer.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    private fun prepareAdnPlay() {
        val currentMix = mPlayList.getCurrentMix()
        currentMix?.let {
            try {
                mPlayer.reset()
                mPlayer.setDataSource(currentMix.track)
                mPlayer.prepareAsync()
                notifyPlayStatusChanged(false)
                // TODO notifyPlayStatusChanged(true) notify loading
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "play: ", e)
                notifyPlayStatusChanged(false)
            } catch (ex: IllegalStateException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "play: ", ex)
                notifyPlayStatusChanged(false)
            }
        }
    }

    private fun stop() {
        mPlayer.stop()
        notifyPlayStatusChanged(false)
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
        prepareAdnPlay()
    }

    override fun isPlaying(): Boolean = mPlayer.isPlaying

    override fun getProgress(): Int = mPlayer.currentPosition

    override fun getDuration(): Int = mPlayer.duration

    override fun getPlayingSong(): Mix? = mPlayList.getCurrentMix()

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
