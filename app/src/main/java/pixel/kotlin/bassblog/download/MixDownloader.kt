package pixel.kotlin.bassblog.download

import android.content.Context
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

    public fun scheduleDonwload(url: String) {
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val originalResponse = chain.proceed(chain.request())
                    originalResponse.newBuilder()
                            .body(ProgressResponseBody(originalResponse.body(), MyProgressListener()))
                            .build()
                }
                .build()

        val request = Request.Builder()
                .url(url)
                .build()

        mExecutor.submit({
            val response = client.newCall(request).execute()
            val responseBody = response.body()
            val stream = responseBody.byteStream()
            val file = File(mContext.cacheDir, "file")
            copyInputStreamToFile(stream, file)

//            StreamUtils.copy(responseBody.byteStream(), destination)


//            client.newCall(request).execute()({ response ->
//                if (!response.isSuccessful()) {
//                    throw ExceptionUtils.createIoException(response)
//                }
//                var responseBody = response.body()
//                try {
//                    if (progressListener != null) {
//                        responseBody = ProgressResponseBody(responseBody, progressListener)
//                    }
//                    StreamUtils.copy(responseBody.byteStream(), destination)
//                } finally {
//                    if (responseBody !== response.body()) {
//                        // We need to close our custom progress response manually, otherwise
//                        // progress handler thread will not be shut down
//                        responseBody.close()
//                    }
//                }
//            })


        }
        )
    }

    private fun copyInputStreamToFile(stream: InputStream, file: File) {
        try {
            val out = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            do {
                len = stream.read(buf)
                out.write(buf, 0, len)
            } while (len > 0)

//            while ((len = stream.read(buf)) > 0) {
//                out.write(buf, 0, len)
//            }
            out.close()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//    @Throws(IOException::class)
//    fun copy(stream: InputStream, destination: File) {
//        BufferedOutputStream(FileOutputStream(destination)).use { os -> IOUtils.copy(stream, os) }
//    }
//
//    @Throws(IOException::class)
//    fun copy(input: InputStream, output: OutputStream): Int {
//        val count = copyLarge(input, output)
//        if (count > Integer.MAX_VALUE) {
//            return -1
//        }
//        return count.toInt()
//    }

    private class MyProgressListener : ProgressListener {

        override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
            System.out.println("ololololo $bytesRead $contentLength $done")
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
