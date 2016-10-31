package pixel.kotlin.bassblog.service

import android.database.Cursor
import pixel.kotlin.bassblog.storage.BlogPost

interface IPlayback {
    fun toggle()
    fun updatePlayList(cursor: Cursor?)
    fun play(post: BlogPost)
    fun playLast()
    fun playNext()
    fun isPlaying(): Boolean
    fun getProgress(): Int
    fun getDuration(): Int
    fun getBuffered(): Int
    fun getPlayingSong(): BlogPost?
    fun seekTo(progress: Int)
    fun nextPlayMode() : Int
    fun registerCallback(callback: Callback)
    fun unregisterCallback(callback: Callback)
    fun releasePlayer()

    interface Callback {
        fun onPlayStatusChanged(isPlaying: Boolean)
    }
}