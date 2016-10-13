package pixel.kotlin.bassblog.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import pixel.kotlin.bassblog.service.PlaybackService

open class PlaybackActivity : AppCompatActivity(), ServiceConnection {

    private var mPlaybackService: PlaybackService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(Intent(applicationContext, PlaybackService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mPlaybackService == null
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mPlaybackService = (service as PlaybackService.LocalBinder).service
    }
}
