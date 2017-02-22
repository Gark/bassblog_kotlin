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
    private val mOkHttpClient = OkHttpClient.Builder().build()

    fun getState(mixId: Long): Long {
        val entity = mMapDownloads[mixId]
        entity?.let {
            return it.getState()
        }
        return DownloadEntity.NOT_DOWNLOADED
    }

    fun scheduleDownload(mixId: Long, url: String?, listener: ProgressListener) {
        var entity = mMapDownloads[mixId]
        entity?.let {
            return
        }

        entity = DownloadEntity(mHandler, context, mOkHttpClient, url, mixId, listener)
        mMapDownloads.put(mixId, entity)
        mExecutor.submit { entity }
    }

    @UiThread
    fun addProgressListener(mixProgress: ProgressListener, mixId: Long?) {
        mMapDownloads[mixId]?.addProgressListener(mixProgress)
    }

    @UiThread
    fun removeListener(mixId: Long?) {
        mMapDownloads[mixId]?.removeProgressListener()
    }

//
//    private fun updateToProgressState(mixId: Long) {
//        mHandler.post {
//            mMapState.put(mixId, IN_PROGRESS)
//        }
//    }
//
//    private fun updateToDownloadState(mixId: Long) {
//        mHandler.post {
//            mMapState.remove(mixId)
//        }
//    }
//

//
//    private fun notifyListenerIfExisted(mixId: Long, progress: Int, readMb: Int, totalMb: Int, done: Boolean) {
//        mHandler.post {
//            val listener = mMapDownloads[mixId]
//            listener?.let {
//                listener.update(mixId, progress, readMb, totalMb, done)
//            }
//        }
//    }
//

}
