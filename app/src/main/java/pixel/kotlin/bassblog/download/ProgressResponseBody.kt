package pixel.kotlin.bassblog.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class ProgressResponseBody(
        val mixId: Long,
        val responseBody: ResponseBody,
        val listener: ProgressListener?) : ResponseBody() {

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
            internal var progressReadMb = 0
            internal var temp = 0
            internal val totalMb: Int = (responseBody.contentLength() / (1024 * 1024)).toInt()

            @Throws(IOException::class)
            override fun read(sink: Buffer, byteCount: Long): Long {
                val bytesRead = super.read(sink, byteCount)
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                temp = (totalBytesRead / (1024 * 1024)).toInt()
                if (progressReadMb < temp) {
                    progressReadMb = temp
                    listener?.update(mixId, 100 * progressReadMb / totalMb, progressReadMb, totalMb, bytesRead == -1L)
//                    System.out.println("xxxxxxxxx -> $mixId $bytesRead $totalMb")
                }
                return bytesRead
            }
        }
    }
}
