package pixel.kotlin.bassblog.download

import android.content.Context
import android.support.annotation.WorkerThread
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okio.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MixDownloader(context: Context) {

    private val mExecutor: ExecutorService = Executors.newFixedThreadPool(2)
    private val mContext: Context = context

    fun scheduleDownload(url: String) {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
                .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
                .build()

        mExecutor.submit({
            val response = client.newCall(request).execute()
            var responseBody = response.body()
            responseBody = ProgressResponseBody(responseBody, MyProgressListener())

            val file = File(mContext.cacheDir, "file")
            StreamUtils.copy(responseBody.byteStream(), file)
            responseBody.close()
        })
    }

    private class MyProgressListener : ProgressListener {

        override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
            System.out.println("ololololo ${100 * bytesRead / contentLength} $done")
        }
    }

    private class ProgressResponseBody(val responseBody: ResponseBody, val progressListener: ProgressListener) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

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
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }
}
