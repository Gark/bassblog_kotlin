package pixel.kotlin.bassblog.service

import pixel.kotlin.bassblog.network.Mix

interface IPlayback {
    fun toggle()
    fun play(mix: Mix, tab: Int)
    fun playLast()
    fun playNext()
    fun isPlaying(): Boolean
    fun getProgress(): Int
    fun getDuration(): Int
    fun getBuffered(): Int
    fun getPlayingSong(): Mix?
    fun seekTo(progress: Int)
    fun registerCallback(callback: Callback)
    fun unregisterCallback(callback: Callback)
    fun releasePlayer()

    interface Callback {
        fun onPlayStatusChanged(isPlaying: Boolean)
    }
}