package pixel.kotlin.bassblog.service

import pixel.kotlin.bassblog.network.Mix

interface IPlayback {
    fun toggle()
    fun play(mix: Mix, tab: Int)
    fun playLast()
    fun playNext()
    fun getPlayingState(): Int
    fun getPlayingMix(): Mix?
    fun seekTo(progress: Int)
    fun registerCallback(callback: PlayerCallback)
    fun unregisterCallback(callback: PlayerCallback)
    fun releasePlayer()
    fun requestDataOnBind()

    interface PlayerCallback {
        fun onPlayStatusChanged(state: Int)
        fun onTick(progress: Int, duration: Int, secondaryProgress: Int)
    }
}