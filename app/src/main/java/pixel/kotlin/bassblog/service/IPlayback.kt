package pixel.kotlin.bassblog.service

import pixel.kotlin.bassblog.network.Mix

interface IPlayback {
    fun toggle()
//    fun updatePlayList(list: RealmResults<Mix>?)
    fun play(mix: Mix)
    fun playLast()
    fun playNext()
    fun isPlaying(): Boolean
    fun getProgress(): Int
    fun getDuration(): Int
    fun getBuffered(): Int
    fun getPlayingSong(): Mix?
    fun seekTo(progress: Int)
    fun nextPlayMode(): Int
    fun registerCallback(callback: Callback)
    fun unregisterCallback(callback: Callback)
    fun releasePlayer()

    interface Callback {
        fun onPlayStatusChanged(isPlaying: Boolean)
    }
}