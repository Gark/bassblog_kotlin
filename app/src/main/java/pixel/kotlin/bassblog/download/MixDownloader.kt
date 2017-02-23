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
        entity?.let {
            return
        }

        entity = DownloadEntity(context, mOkHttpClient, url, mixId, mProgressListener)
        mMapDownloads.put(mixId, entity)
        mExecutor.submit(entity)
    }

    private inner class MixProgressListener : ProgressListener {
        override fun update(mixId: Long, progress: Int, readMb: Int, totalMb: Int, done: Boolean) {
            mHandler.post {
                //                System.out.println(" olololo -> $mixId $progress $readMb $totalMb $done")
                mMapListeners[mixId]?.update(mixId, progress, readMb, totalMb, done)
            }
        }
    }

    @UiThread
    fun addProgressListener(mixProgress: ProgressListener, mixId: Long?) {
        mMapListeners.put(mixId, mixProgress)
        //        System.out.println("added ${mMapListeners.size} ${mMapDownloads.size}")
    }

    @UiThread
    fun removeListener(mixId: Long?) {
        mMapListeners.remove(mixId)
        //        System.out.println("removed ${mMapListeners.size} ${mMapDownloads.size}")
    }
}
