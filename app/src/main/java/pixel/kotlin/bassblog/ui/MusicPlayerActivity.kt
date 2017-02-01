package pixel.kotlin.bassblog.ui


import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.squareup.picasso.Picasso
import io.realm.Realm
import kotlinx.android.synthetic.main.play_music_activity.*
import pixel.kotlin.bassblog.BassBlogApplication
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.player.Player
import pixel.kotlin.bassblog.ui.playlist.TrackListActivity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS


class MusicPlayerActivity : BinderActivity(), SeekBar.OnSeekBarChangeListener {

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, MusicPlayerActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_play_toggle.setOnClickListener { handleToggleClick() }
        button_play_next.setOnClickListener { handleNextClick() }
        button_play_last.setOnClickListener { handlePlayLast() }
//        button_share.setOnClickListener { handleShareClick() }
        button_download.setOnClickListener { handleDownload() }
        button_favorite_toggle.setOnClickListener { handleFavouriteClick() }

        seek_bar.setOnSeekBarChangeListener(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean = initMenu(R.menu.tracklist, menu)

    private fun handleTrackListClick() {
        val mix = mPlaybackService?.getPlayingMix()
        mix?.content?.let {
            TrackListActivity.start(this, it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.track_list -> {
                handleTrackListClick()
                return true
            }

            else -> return super.onContextItemSelected(item)
        }
    }

    private fun handleFavouriteClick() {
        mPlaybackService?.let {
            val mix = it.getPlayingMix()
            mix?.let {

                val realm = Realm.getDefaultInstance()
                realm.beginTransaction()
                mix.favourite = !mix.favourite
                mix.favourite_time = System.currentTimeMillis()
                realm.copyToRealmOrUpdate(mix)
                realm.commitTransaction()
                updateFavouriteButton(mix.favourite)

                mix.title?.let {
                    if (mix.favourite) {
                        val app = application as BassBlogApplication
                        app.fireEventFavourite(it)
                    }
                }
            }
        }
    }

    private fun handleDownload() {
        val mix = mPlaybackService?.getPlayingMix()
        mix?.let {
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri = Uri.parse(it.url)
            val request = DownloadManager.Request(downloadUri)
            //Restrict the types of networks over which this download may proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            //Set whether this download may proceed over a roaming connection.
            request.setAllowedOverRoaming(false)
            //Set the title of this download, to be displayed in notifications (if enabled).
            request.setTitle(it.title)
            //Set a description of this download, to be displayed in notifications (if enabled)
            request.setDescription(it.track)
            //Set the local destination for the downloaded file to a path within the application's external files directory
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "BassBlog")

            //Enqueue a new download and same the referenceId
            var downloadReference = downloadManager.enqueue(request)
        }
    }

//    private fun handleShareClick() {
//        mPlaybackService?.getPlayingMix()?.url?.let {
//            val shareIntent = Intent(Intent.ACTION_SEND)
//            shareIntent.type = "text/plain"
//            shareIntent.putExtra(Intent.EXTRA_TEXT, it)
//            shareIntent.resolveActivity(this.packageManager)?.let {
//                startActivity(Intent.createChooser(shareIntent, getString(R.string.app_name)))
//            }
//        }
//    }

    private fun handlePlayLast() {
        if (mPlaybackService == null) return
        mPlaybackService!!.playLast()
    }

    private fun handleNextClick() {
        if (mPlaybackService == null) return
        mPlaybackService!!.playNext()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        super.onServiceConnected(name, service)
        mPlaybackService?.requestDataOnBind()
    }

    override fun onTick(progress: Int, duration: Int, secondaryProgress: Int) {
        text_view_progress.text = convertSecondsToHMmSs(progress)
        text_view_duration.text = convertSecondsToHMmSs(duration)
        seek_bar.progress = 100 * progress / duration
        seek_bar.secondaryProgress = secondaryProgress
    }

    private fun handleToggleClick() {
        mPlaybackService?.toggle()
    }

    override fun onPlayStatusChanged(state: Int) {
        updatePlayToggle(state)
        updateSongData()
    }

    private fun updateFavouriteButton(favourite: Boolean) {
        if (favourite) {
            button_favorite_toggle.setImageResource(R.drawable.ic_favorite)
        } else {
            button_favorite_toggle.setImageResource(R.drawable.ic_add_to_favorites)
        }
    }

    fun updateSongData() {
        if (mPlaybackService == null) return

        val mix = mPlaybackService?.getPlayingMix()
        text_view_name.text = mix?.title
        text_view_artist.text = mix?.label
        updateFavouriteButton(mix?.favourite ?: true)

        Picasso.with(applicationContext)
                .load(mix?.image)
//                .fit()
                .into(mix_image)
    }


    fun updatePlayToggle(state: Int) {
        // TODO loading
        button_play_toggle.setImageResource(
                when (state) {
                    Player.NOT_PLAYING -> R.drawable.ic_play
                    Player.PLAYING -> R.drawable.ic_pause
                    else -> R.drawable.ic_pause
                }
        )
    }

    fun convertSecondsToHMmSs(seconds: Int): String {
        val sec = seconds / 1000
        val s = sec % 60
        val m = sec / 60 % 60
        val h = sec / (60 * 60) % 24

        return if (h == 0) String.format("%02d:%02d", m, s) else String.format("%d:%02d:%02d", h, m, s)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            if (mPlaybackService == null) return
            mPlaybackService!!.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }
}


fun AppCompatActivity.initMenu(menuResource: Int, menu: Menu?): Boolean {
    menuInflater.inflate(menuResource, menu)
    return true
}