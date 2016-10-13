package pixel.kotlin.bassblog.service

interface IPlayback {

    //    void setPlayList(PlayList list);

    fun play(): Boolean

//    boolean play(PlayList list);
//
//    boolean play(PlayList list, int startIndex);
//
//    boolean play(Song song);
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