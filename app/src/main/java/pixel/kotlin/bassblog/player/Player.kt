package pixel.kotlin.bassblog.player

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import pixel.kotlin.bassblog.BuildConfig
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.service.IPlayback
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class Player(wifi: WifiManager) : IPlayback, MediaPlayer.OnErrorListener {


    companion object {
        val PLAYING = 0
        val NOT_PLAYING = 1
        val LOADING = 2
    }

    private val INTERVAL = TimeUnit.SECONDS.toMillis(1)
    private val mHandler = Handler()
    private val runnable = Runnable { tick() }

    private val mWifiLock: WifiManager.WifiLock
    private val mCallbacks = ArrayList<IPlayback.PlayerCallback>()
    private val TAG = Player::class.java.name
    private val mPlayList = PlayList()
    private val mPlayer: MediaPlayer

    private var mCurrentState = NOT_PLAYING
    private var mBuffered = 0


    init {
        mWifiLock = wifi.createWifiLock("LOCK")
        mPlayer = MediaPlayer()
        mPlayer.setOnErrorListener(this)
        mPlayer.setOnCompletionListener { handleOnComplete() }
        mPlayer.setOnPreparedListener { mp -> handlePrepare() }
        mPlayer.setOnBufferingUpdateListener { mediaPlayer, i -> handleBuffering(i) }
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        //True if the method handled the error, false if it didn't. Returning false, or not having an OnErrorListener at all, will cause the OnCompletionListener to be called.
        return true
    }

    private fun handleBuffering(i: Int) {
        mBuffered = i
    }

    private fun handleOnComplete() {
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
        currentMix?.track?.let {
            try {
                mPlayer.reset()
                mPlayer.setDataSource(currentMix.track)
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
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
        val currentPosition = mPlayer.currentPosition
        if (currentPosition > TimeUnit.SECONDS.toMillis(20)) {
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
            PLAYING -> handlePlaying()
            NOT_PLAYING -> handleNotPlaying()
            else -> {
                // do nothing
            }
        }
    }

    private fun handleNotPlaying() {
        mHandler.removeCallbacks(runnable)
        if (mWifiLock.isHeld) {
            mWifiLock.release()
        }
    }

    private fun handlePlaying() {
        mHandler.postDelayed(runnable, INTERVAL)
        mWifiLock.acquire()
    }

    private fun tick() {
        mHandler.postDelayed(runnable, INTERVAL)
        requestData()
    }

    override fun requestDataOnBind() = requestData()

    fun requestData() {
        mPlayList.getCurrentMix()?.let {
            val duration = Math.max(1, mPlayer.duration)
            notifyTick(mPlayer.currentPosition, duration, mBuffered)
        }
    }

    private fun notifyTick(progress: Int, duration: Int, secondaryProgress: Int) {
        for (callback in mCallbacks) {
            callback.onTick(progress, duration, secondaryProgress)
        }
    }
}
