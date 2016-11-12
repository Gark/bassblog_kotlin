package pixel.kotlin.bassblog.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import pixel.kotlin.bassblog.service.IPlayback
import pixel.kotlin.bassblog.service.PlaybackService

abstract class CommunicationActivity : AppCompatActivity(), ServiceConnection, IPlayback.PlayerCallback {

    protected var mPlaybackService: IPlayback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(applicationContext, PlaybackService::class.java))
        bindService(Intent(applicationContext, PlaybackService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mPlaybackService == null
        mPlaybackService?.unregisterCallback(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mPlaybackService = (service as PlaybackService.LocalBinder).service
        mPlaybackService?.registerCallback(this)
        mPlaybackService?.requestDataOnBind()
        onPlayStatusChanged(mPlaybackService!!.getPlayingState())
    }
}
