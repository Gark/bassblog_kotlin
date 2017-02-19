package pixel.kotlin.bassblog.download

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.support.annotation.IntDef
import pixel.kotlin.bassblog.R

class MixDownloader(context: Context) {

    companion object {
        @IntDef(NOT_DOWNLOADED, IN_PROGRESS, DOWNLOADED)
        annotation class DownloadingState

        const val NOT_DOWNLOADED = 0L
        const val IN_PROGRESS = 1L
        const val DOWNLOADED = 2L
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private val mExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    private val mContext: Context = context
    private val mMapDownloads: HashMap<Long, ProgressListener> = HashMap()
    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder().build()

    init {
        // constructor body
    }

    // TODO Race condition
    @DownloadingState
    fun getState(mixId: Long): Long {
        val file = getFile(mixId)
        if (file.exists()) {
            return DOWNLOADED
        } else {
            return NOT_DOWNLOADED
        }

//        if (!file.exists()) {
//            return NOT_DOWNLOADED
//        } else {
//            if (mMapDownloads.containsKey(mixId)) {
//                return IN_PROGRESS
//            } else {
//                return DOWNLOADED
//            }
//        }
    }


    // TODO maybe should not be a part of that class, move to utility class.
    fun getFileSize(mixId: Long): String {
        val file = getFile(mixId)
        if (!file.exists()) {
            return ""
        }
        val sizeInMb = file.length() / (1024 * 1024)
        return mContext.getString(R.string.download_mb, sizeInMb)
    }

    fun scheduleDownload(mixId: Long, url: String?) {
        val file = getFile(mixId)
//        if (file.exists()) {
//            return
//        }


        val request = Request.Builder()
                .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
//                .url("http://www.colocenter.nl/speedtest/100mb.bin")
//                .url("https://upload.wikimedia.org/wikipedia/commons/7/72/%27Calypso%27_Panorama_of_Spirit%27s_View_from_%27Troy%27_%28Stereo%29.jpg")
                .build()

        mExecutor.submit {
            val response = mOkHttpClient.newCall(request).execute()
            val responseBody = response.body()
            val mixResponseBody = ProgressResponseBody(mixId, responseBody)

            StreamUtils.copy(mixResponseBody.byteStream(), file)
            responseBody.close()
            mixResponseBody.close()
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

    private fun notifyListenerIfExisted(mixId: Long, bytesRead: Long, contentLength: Long, done: Boolean) {
        val listener = mMapDownloads[mixId]
        listener?.let {
            mHandler.post {
                listener.update(mixId, bytesRead, contentLength, done)
            }
        }
    }

    private fun getFile(mixId: Long): File = File(mContext.cacheDir, mixId.toString())

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

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    notifyListenerIfExisted(mixId, totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }
}
