package pixel.kotlin.bassblog.player

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import pixel.kotlin.bassblog.BuildConfig
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.service.IPlayback
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class Player : IPlayback, MediaPlayer.OnCompletionListener {

    companion object {
        val PLAYING = 0
        val NOT_PLAYING = 1
        val LOADING = 2
    }

    private val INTERVAL = TimeUnit.SECONDS.toMillis(1)
    private val mHandler = Handler()
    private val runnable = Runnable { tick() }


    private val mCallbacks = ArrayList<IPlayback.PlayerCallback>()
    private val TAG = Player::class.java.name
    private val mPlayList = PlayList()
    private val mPlayer: MediaPlayer

    private var mCurrentState = NOT_PLAYING
    private var mBuffered = 0

    init {
        mPlayer = MediaPlayer()
//        mPlayer.setOnCompletionListener { handleOnComplete() }
        mPlayer.setOnCompletionListener(this)
        mPlayer.setOnPreparedListener { mp -> handlePrepare() }
        mPlayer.setOnBufferingUpdateListener { mediaPlayer, i -> handleBuffering(i) }
    }

    private fun handleBuffering(i: Int) {
        mBuffered = i
    }

    private fun handleOnComplete() {
        playNext()
    }

    override fun onCompletion(mp: MediaPlayer) {
        playNext()
    }

    fun play() {
        mPlayer.start()
        notifyPlayStatusChanged(PLAYING)
    }

    fun pause() {
        mPlayer.pause()
        notifyPlayStatusChanged(NOT_PLAYING)
    }

    private fun handlePrepare() {
        play()
    }

    override fun play(mix: Mix, tab: Int) {
        val currentMix = mPlayList.getCurrentMix()
        if (mix == currentMix) {
            // do nothing
        } else {
            stop()
            mPlayList.updateCurrent(mix, tab)
            notifyPlayStatusChanged(NOT_PLAYING)
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
                notifyPlayStatusChanged(LOADING)
            } catch (e: IOException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "play: ", e)
                notifyPlayStatusChanged(NOT_PLAYING)
            } catch (ex: IllegalStateException) {
                if (BuildConfig.DEBUG) Log.e(TAG, "play: ", ex)
                notifyPlayStatusChanged(NOT_PLAYING)
            }
        }
    }

    private fun stop() {
        mPlayer.stop()
        notifyPlayStatusChanged(NOT_PLAYING)
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

    override fun getPlayingState(): Int = mCurrentState

    override fun getPlayingMix(): Mix? = mPlayList.getCurrentMix()

    override fun seekTo(progress: Int) {
        if (mPlayList.isEmpty()) return
        mPlayer.seekTo(progress * mPlayer.duration / 100)
    }

    override fun registerCallback(callback: IPlayback.PlayerCallback) {
        mCallbacks.add(callback)
    }

    override fun unregisterCallback(callback: IPlayback.PlayerCallback) {
        mCallbacks.remove(callback)
    }

    override fun releasePlayer() {
        mPlayList.clear()
        mPlayer.reset()
        mPlayer.release()
    }

    private fun notifyPlayStatusChanged(state: Int) {
        mCurrentState = state
        handlePlayingState()
        for (callback in mCallbacks) {
            callback.onPlayStatusChanged(state)
        }
    }

    private fun handlePlayingState() {
        when (mCurrentState) {
            PLAYING -> mHandler.postDelayed(runnable, INTERVAL)
            else -> mHandler.removeCallbacks(runnable)
        }
    }

    private fun tick() {
        mHandler.postDelayed(runnable, INTERVAL)
        requestData()
    }

    override fun requestDataOnBind() = requestData()

    fun requestData() {
        Log.e("", "")
//        mPlayList.getCurrentMix()?.let {
//            val duration = Math.max(1, mPlayer.duration)
//            notifyTick(mPlayer.currentPosition, duration, mBuffered)
//        }
    }

    private fun notifyTick(progress: Int, duration: Int, secondaryProgress: Int) {
        for (callback in mCallbacks) {
            callback.onTick(progress, duration, secondaryProgress)
        }
    }
}
