package pixel.kotlin.bassblog.service

import pixel.kotlin.bassblog.storage.BlogPost

interface IPlayback {

    fun play(): Boolean

    fun setPlayList(array: Array<BlogPost>)


//    boolean play(PlayList list);
//
//    void setPlayList( list);
//
//    boolean play(PlayList list, int startIndex);
//
//    boolean play(Song song);
//    boolean play(BlogPost[] songs);
//
//    abstract fun playLast(): Boolean
//
//    abstract fun playNext(): Boolean
//
//    abstract fun pause(): Boolean
//
//    abstract fun isPlaying(): Boolean
//
//    abstract fun getProgress(): Int
//
//    Song getPlayingSong();
//
//    abstract fun seekTo(progress: Int): Boolean
//
//    abstract fun setPlayMode(playMode: PlayMode)
//
//    abstract fun registerCallback(callback: Callback)
//
//    abstract fun unregisterCallback(callback: Callback)
//
//    abstract fun removeCallbacks()
//
//    abstract fun releasePlayer()
}