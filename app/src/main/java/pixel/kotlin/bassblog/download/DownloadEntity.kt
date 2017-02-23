package pixel.kotlin.bassblog.download

import android.content.Context
import android.os.Handler
import android.support.annotation.IntDef
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.IOException

class DownloadEntity(
        val context: Context,
        val httpOk: OkHttpClient,
        val url: String?,
        val mixId: Long,
        val listener: ProgressListener) : Runnable {

    companion object {
        @IntDef(NOT_DOWNLOADED, IN_PROGRESS, DOWNLOADED, PENDING)
        annotation class DownloadingState

        const val NOT_DOWNLOADED = 0L
        const val IN_PROGRESS = 1L
        const val DOWNLOADED = 2L
        const val PENDING = 3L
    }

    private val MB = 1024 * 1024
    private val file = FileUtils.getFile(mixId, context)
    private var mTotalSize: Int? = null

    private var mState = NOT_DOWNLOADED

    init {
        mState = PENDING
        listener.update(mixId, 0, 0, 0, false)
    }

    fun getTotalSize(): Int? = mTotalSize

    fun getReadSize(): Int? = (file.length() / MB).toInt()

    @DownloadingState
    fun getState(): Long = mState

    override fun run() {
        mState = IN_PROGRESS
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
            mixResponseBody = ProgressResponseBody(mixId, responseBody, listener)
            StreamUtils.copy(mixResponseBody.byteStream(), file)
        } catch (ex: IOException) {
            ex.printStackTrace()
            mState = NOT_DOWNLOADED
            file.delete()
        } finally {
            responseBody?.close()
            mixResponseBody?.close()
        }

        val size: Int = (file.length() / MB).toInt()
        listener.update(mixId, 100, size, size, true)
        mState = DOWNLOADED
    }
}