package pixel.kotlin.bassblog.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.IntDef
import android.support.annotation.UiThread
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.*
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MixDownloader(context: Context) {

    companion object {
        @IntDef(NOT_DOWNLOADED, IN_PROGRESS, DOWNLOADED, PENDING)
        annotation class DownloadingState

        const val NOT_DOWNLOADED = 0L
        const val IN_PROGRESS = 1L
        const val DOWNLOADED = 2L
        const val PENDING = 3L
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    private val mContext: Context = context
    private val mMapDownloads: HashMap<Long, ProgressListener> = HashMap()
    private val mMapState: HashMap<Long, Long> = HashMap()
    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    init {
        // constructor body
    }

    @DownloadingState
    fun getState(mixId: Long): Long {
        val file = FileUtils.getFile(mixId, mContext)
        val state = mMapState[mixId]

        if (state == PENDING) {
            return PENDING
        }

        if (state == IN_PROGRESS) {
            return IN_PROGRESS
        }

        if (file.exists()) {
            return DOWNLOADED
        } else {
            return NOT_DOWNLOADED
        }
    }

    fun getFileSize(mixId: Long): CharSequence? = FileUtils.getFileSize(mixId, mContext)

    fun deleteFileMix(mixId: Long) = FileUtils.deleteFileMix(mixId, mContext)

    fun scheduleDownload(mixId: Long, url: String?) {
        // interrupt downloading if given mix is already scheduled
        if (mMapState.contains(mixId)) {
            return
        }

        // interrupt downloading if given mix is already downloaded
        val file = FileUtils.getFile(mixId, mContext)
        if (file.exists()) {
            return
        }

        val request = Request.Builder()
//                .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
//                .url("http://www.colocenter.nl/speedtest/100mb.bin")
                .url("https://upload.wikimedia.org/wikipedia/commons/7/72/%27Calypso%27_Panorama_of_Spirit%27s_View_from_%27Troy%27_%28Stereo%29.jpg")
                .build()
        mMapState.put(mixId, PENDING)
        notifyListenerIfExisted(mixId, 0, 0, 0, false)

        mExecutor.submit {
            updateToProgressState(mixId)

            var responseBody: ResponseBody? = null
            var mixResponseBody: ResponseBody? = null

            try {
                val response = mOkHttpClient.newCall(request).execute()
                responseBody = response.body()
                mixResponseBody = ProgressResponseBody(mixId, responseBody)
                StreamUtils.copy(mixResponseBody.byteStream(), file)
            } catch (ex: IOException) {
                ex.printStackTrace()
                file.delete()
            } finally {
                responseBody?.close()
                mixResponseBody?.close()
            }

            updateToDownloadState(mixId)

            val size: Int = (file.length() / (1024 * 1024)).toInt()
            notifyListenerIfExisted(mixId, 100, size, size, true)
        }
    }

    private fun updateToProgressState(mixId: Long) {
        mHandler.post {
            mMapState.put(mixId, IN_PROGRESS)
        }
    }

    private fun updateToDownloadState(mixId: Long) {
        mHandler.post {
            mMapState.remove(mixId)
        }
    }

    @UiThread
    fun addProgressListener(mixProgress: ProgressListener, mixId: Long) {
        mMapDownloads.put(mixId, mixProgress)
    }

    @UiThread
    fun removeListener(mixProgressListener: ProgressListener) {
        mMapDownloads.values.remove(mixProgressListener)
    }

    private fun notifyListenerIfExisted(mixId: Long, progress: Int, readMb: Int, totalMb: Int, done: Boolean) {
        mHandler.post {
            val listener = mMapDownloads[mixId]
            listener?.let {
                listener.update(mixId, progress, readMb, totalMb, done)
            }
        }
    }

    // TODO move to separate class, probably
    private inner class ProgressResponseBody(
            val mixId: Long,
            val responseBody: ResponseBody) : ResponseBody() {

        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType = responseBody.contentType()

        override fun contentLength(): Long = responseBody.contentLength()

        override fun source(): BufferedSource? {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()))
            }
            return bufferedSource
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                internal var totalBytesRead = 0L
                internal var progress = 0
                internal var temp = 0
                internal val total: Int = (responseBody.contentLength() / (1024 * 1024)).toInt()

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                    System.out.println("olololol $totalBytesRead ${responseBody.contentLength()} ${bytesRead == -1L}) ")

                    temp = (totalBytesRead / (1024 * 1024)).toInt()
                    if (progress < temp) {
                        progress = temp
                        notifyListenerIfExisted(mixId, progress, temp, total, bytesRead == -1L)
                    }
                    return bytesRead
                }
            }
        }
    }


}
