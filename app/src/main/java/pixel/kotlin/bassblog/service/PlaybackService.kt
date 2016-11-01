package pixel.kotlin.bassblog.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.database.Cursor
import android.os.Binder
import android.os.IBinder
import android.support.v7.app.NotificationCompat
import android.widget.RemoteViews
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.player.Player
import pixel.kotlin.bassblog.storage.BlogPost
import pixel.kotlin.bassblog.ui.MainActivity

class PlaybackService : Service(), IPlayback, IPlayback.Callback {

    private val ACTION_PLAY_TOGGLE = "pixel.kotlin.bassblog.ACTION.PLAY_TOGGLE"
    private val ACTION_PLAY_LAST = "pixel.kotlin.bassblog.ACTION.PLAY_LAST"
    private val ACTION_PLAY_NEXT = "pixel.kotlin.bassblog.ACTION.PLAY_NEXT"
    private val ACTION_STOP_SERVICE = "pixel.kotlin.bassblog.ACTION.STOP_SERVICE"

    private val NOTIFICATION_ID = 1

    private var mPlayer: Player? = null

    inner class LocalBinder : Binder() {
        val service: PlaybackService
            get() = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handleRemoteAction(intent?.action)
        return START_STICKY
    }

    private fun handleRemoteAction(action: String?) {
        when (action) {
            ACTION_PLAY_LAST -> playLast()
            ACTION_PLAY_NEXT -> playNext()
            ACTION_STOP_SERVICE -> stopSelf()
            ACTION_PLAY_TOGGLE -> toggle()
        }
    }

    override fun onDestroy() {
        mPlayer?.pause()
        mPlayer?.releasePlayer()
        unregisterCallback(this)
        super.onDestroy()
    }

    //------------------------------------------------------------------------------------------------------------//

    override fun onCreate() {
        super.onCreate()
        mPlayer = Player()
        registerCallback(this)
    }

    override fun updatePlayList(cursor: Cursor?) {
        mPlayer?.updatePlayList(cursor)
    }

    override fun toggle() = mPlayer!!.toggle()

    override fun play(post: BlogPost) = mPlayer!!.play(post)

    override fun playLast() {
        mPlayer?.playLast()
    }

    override fun playNext() = mPlayer!!.playNext()


    override fun isPlaying(): Boolean = mPlayer!!.isPlaying()

    override fun getProgress(): Int = mPlayer!!.getProgress()

    override fun getDuration(): Int = mPlayer!!.getDuration()

    override fun getBuffered(): Int = mPlayer!!.getBuffered()

    override fun getPlayingSong(): BlogPost? = mPlayer!!.getPlayingSong()

    override fun seekTo(progress: Int) = mPlayer!!.seekTo(progress)

    override fun nextPlayMode(): Int = mPlayer!!.nextPlayMode()

    override fun registerCallback(callback: IPlayback.Callback) {
        mPlayer?.registerCallback(callback)
    }

    override fun unregisterCallback(callback: IPlayback.Callback) {
        mPlayer?.unregisterCallback(callback)
    }

    override fun releasePlayer() {
        // do nothing
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) = showNotification()

    // Notification
    /**
     * Show a notification while this service is running.
     */
    private var mContentViewBig: RemoteViews? = null
    private var mContentViewSmall: RemoteViews? = null

    private fun showNotification() {
        // The PendingIntent to launch our activity if the user selects this notification
        val contentIntent = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), 0)
        // Set the info for the views that show in the notification panel.
        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .setCustomContentView(getSmallContentView())
                .setCustomBigContentView(getBigContentView())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .build()

        // Send the notification.
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getBigContentView(): RemoteViews? {
        if (mContentViewBig == null) {
            mContentViewBig = RemoteViews(packageName, R.layout.remote_view_music_player)
            setUpRemoteView(mContentViewBig)
        }
        updateRemoteViews(mContentViewBig as RemoteViews)
        return mContentViewBig
    }

    private fun getSmallContentView(): RemoteViews? {
        if (mContentViewSmall == null) {
            mContentViewSmall = RemoteViews(packageName, R.layout.remote_view_music_player_small)
            setUpRemoteView(mContentViewSmall)
        }
        updateRemoteViews(mContentViewSmall as RemoteViews)
        return mContentViewSmall
    }

    private fun setUpRemoteView(remoteView: RemoteViews?) {
        remoteView?.setImageViewResource(R.id.image_view_close, R.drawable.ic_remote_view_close)
        remoteView?.setImageViewResource(R.id.image_view_play_last, R.drawable.ic_remote_view_play_last)
        remoteView?.setImageViewResource(R.id.image_view_play_next, R.drawable.ic_remote_view_play_next)

        remoteView?.setOnClickPendingIntent(R.id.button_close, getPendingIntent(ACTION_STOP_SERVICE))
        remoteView?.setOnClickPendingIntent(R.id.button_play_last, getPendingIntent(ACTION_PLAY_LAST))
        remoteView?.setOnClickPendingIntent(R.id.button_play_next, getPendingIntent(ACTION_PLAY_NEXT))
        remoteView?.setOnClickPendingIntent(R.id.button_play_toggle, getPendingIntent(ACTION_PLAY_TOGGLE))
    }

    private fun updateRemoteViews(remoteView: RemoteViews) {
        val blogPost = mPlayer!!.getPlayingSong()
        remoteView.setTextViewText(R.id.text_view_name, blogPost?.title)
        remoteView.setTextViewText(R.id.text_view_artist, blogPost?.label)
        remoteView.setImageViewResource(R.id.image_view_play_toggle,
                if (mPlayer!!.isPlaying()) R.drawable.ic_remote_view_pause else R.drawable.ic_remote_view_play)
    }

    // PendingIntent
    private fun getPendingIntent(action: String): PendingIntent = PendingIntent.getService(this, 0, Intent(action), 0)
}
