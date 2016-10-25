package pixel.kotlin.bassblog.service

import android.database.Cursor
import pixel.kotlin.bassblog.player.PlayMode
import pixel.kotlin.bassblog.storage.BlogPost

interface IPlayback {
    fun play(): Boolean
    fun updatePlayList(cursor: Cursor?)
    fun play(post: BlogPost): Boolean
    fun playLast(): Boolean
    fun playNext(): Boolean
    fun pause(): Boolean
    fun isPlaying(): Boolean
    fun getProgress(): Int
    fun getDuration(): Int
    fun getBuffered(): Int
    fun getPlayingSong(): BlogPost?
    fun seekTo(progress: Int)
    fun setPlayMode(playMode: PlayMode)
    fun registerCallback(callback: Callback)
    fun unregisterCallback(callback: Callback)
    fun removeCallbacks()
    fun releasePlayer()

    interface Callback {
        fun onSwitchLast(last: BlogPost?)
        fun onSwitchNext(next: BlogPost)
        fun onComplete(next: BlogPost?)
        fun onPlayStatusChanged(isPlaying: Boolean)
    }
}