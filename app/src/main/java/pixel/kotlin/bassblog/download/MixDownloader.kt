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

    @DownloadingState
    fun getState(mixId: Long): Long {
        val file = getFile(mixId)
        if (!file.exists()) {
            return NOT_DOWNLOADED
        } else {
            if (mMapDownloads.containsKey(mixId)) {
                return IN_PROGRESS
            } else {
                return DOWNLOADED
            }
        }
    }

    fun scheduleDownload(mixId: Long, url: String?, mixProgress: ProgressListener) {
        val file = getFile(mixId)
        if (file.exists()) {
            return
        }

        var listener = mMapDownloads[mixId]
        if (listener == null) {
            mMapDownloads.put(mixId, mixProgress)
            listener = mixProgress

            val request = Request.Builder()
//                    .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
                    .url("http://www.colocenter.nl/speedtest/100mb.bin")
                    .build()

            mExecutor.submit {
                val response = mOkHttpClient.newCall(request).execute()
                val responseBody = response.body()
                val mixResponseBody = ProgressResponseBody(responseBody, listener!!, mHandler)

                StreamUtils.copy(mixResponseBody.byteStream(), file)
                responseBody.close()
                mixResponseBody.close()

                mHandler.post { removeListener(listener!!) }
            }
        }
    }

    private fun getFile(mixId: Long): File = File(mContext.cacheDir, mixId.toString())


    @UiThread
    private fun removeListener(mixProgressListener: ProgressListener) {
        mMapDownloads.values.remove(mixProgressListener)
    }

    // TODO move to separate class
    private class ProgressResponseBody(val responseBody: ResponseBody, val progressListener: ProgressListener, val handler: Handler) : ResponseBody() {

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
                    handler.post {
                        progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    }
                    return bytesRead
                }
            }
        }
    }
}
