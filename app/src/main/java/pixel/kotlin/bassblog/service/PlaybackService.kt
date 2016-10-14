package pixel.kotlin.bassblog.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import pixel.kotlin.bassblog.player.Player
import pixel.kotlin.bassblog.storage.BlogPost

class PlaybackService : Service(), IPlayback {


    inner class LocalBinder : Binder() {
        val service: PlaybackService
            get() = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    //------------------------------------------------------------------------------------------------------------//

    private var mPlayer: Player = null!!

    override fun onCreate() {
        super.onCreate()
        mPlayer = Player()
    }

    override fun play(): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlayList(array: Array<BlogPost>) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
