package pixel.kotlin.bassblog.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import pixel.kotlin.bassblog.service.IPlayback
import pixel.kotlin.bassblog.service.PlaybackService

open class BinderFragment : Fragment(), ServiceConnection, IPlayback.Callback {

    protected var mPlaybackService: IPlayback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.bindService(Intent(activity, PlaybackService::class.java), this, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity.unbindService(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mPlaybackService == null
        mPlaybackService?.unregisterCallback(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        mPlaybackService = (service as PlaybackService.LocalBinder).service
        mPlaybackService?.registerCallback(this)
        onPlayStatusChanged(mPlaybackService!!.isPlaying())
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {

    }
}
