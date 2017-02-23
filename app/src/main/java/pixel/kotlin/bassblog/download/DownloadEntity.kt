package pixel.kotlin.bassblog.download

import android.content.Context
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class DownloadEntity(
        val context: Context,
        val httpOk: OkHttpClient,
        val url: String?,
        val mixId: Long,
        val listener: ProgressListener) : Runnable {

    private val MB = 1024 * 1024
    private val mInterrupt = AtomicBoolean(false)

    private var file: File = FileUtils.getFile(mixId, context)
    private var mTotalSize: Int? = null
    private var mState = DownloadingState.NOT_DOWNLOADED

    init {
        mState = DownloadingState.PENDING
        listener.update(mixId, 0, 0, 0, DownloadingState.PENDING)
    }

    fun cancelDownloading() = mInterrupt.set(true)

    fun getTotalSize(): Int = mTotalSize ?: 1

    fun getReadSize(): Int = (file.length() / MB).toInt()

    fun getState(): Long = mState

    override fun run() {
        mState = DownloadingState.IN_PROGRESS
        listener.update(mixId, 0, 0, 0, DownloadingState.IN_PROGRESS)
        val request = Request.Builder()
//                .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
//                .url("http://www.colocenter.nl/speedtest/100mb.bin")
                .url("https://upload.wikimedia.org/wikipedia/commons/7/72/%27Calypso%27_Panorama_of_Spirit%27s_View_from_%27Troy%27_%28Stereo%29.jpg")
                .build()

        var responseBody: ResponseBody? = null
        var mixResponseBody: ResponseBody? = null

        try {
            val response = httpOk.newCall(request).execute()
            responseBody = response.body()
            mTotalSize = (responseBody.contentLength() / MB).toInt()
            mixResponseBody = ProgressResponseBody(mixId, responseBody, listener, mInterrupt)
            StreamUtils.copy(mixResponseBody.byteStream(), file)

            // TODO
            val size: Int = (file.length() / MB).toInt()
            listener.update(mixId, 100, size, size, DownloadingState.DOWNLOADED)
            mState = DownloadingState.DOWNLOADED
        } catch (ex: IOException) {
            Log.e("DownloadEntity", "exception", ex)
            mState = DownloadingState.NOT_DOWNLOADED
            file.delete()
            listener.update(mixId, 0, 0, 0, DownloadingState.NOT_DOWNLOADED)
        } finally {
            responseBody?.close()
            mixResponseBody?.close()
        }
    }
}