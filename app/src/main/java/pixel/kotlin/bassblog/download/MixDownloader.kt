package pixel.kotlin.bassblog.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MixDownloader(val context: Context) {

    private val mHandler = Handler(Looper.getMainLooper())
    private val mExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    private val mMapDownloads: HashMap<Long, DownloadEntity> = HashMap()
    private val mMapListeners: HashMap<Long?, ProgressListener> = HashMap()
    private val mOkHttpClient = OkHttpClient.Builder().build()
    private val mProgressListener = MixProgressListener()

    fun getDownloadingEntity(mixId: Long): DownloadEntity? = mMapDownloads[mixId]

    fun scheduleDownload(mixId: Long, url: String?) {
        var entity = mMapDownloads[mixId]
        // TODO                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
        entity?.let {
            if (it.getState() != DownloadingState.NOT_DOWNLOADED) {
                return
            }
        }

        entity = DownloadEntity(context, mOkHttpClient, url, mixId, mProgressListener)
        mMapDownloads.put(mixId, entity)
        mExecutor.submit(entity)
    }

    private inner class MixProgressListener : ProgressListener {
        override fun update(mixId: Long, progress: Int, readMb: Int, totalMb: Int, state: Long) {
            mHandler.post {
                mMapListeners[mixId]?.update(mixId, progress, readMb, totalMb, state)
            }
        }
    }

    @UiThread
    fun addProgressListener(mixProgress: ProgressListener, mixId: Long?) {
        mMapListeners.put(mixId, mixProgress)
    }

    @UiThread
    fun removeListener(mixId: Long?) {
        mMapListeners.remove(mixId)
    }
}
