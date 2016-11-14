package pixel.kotlin.bassblog.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.os.ResultReceiver
import android.util.Log
import pixel.kotlin.bassblog.BuildConfig
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

open class NetworkService : IntentService(NetworkService::class.java.name) {

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return

        val helper = NetworkHelper()
        val receiver: ResultReceiver? = intent.getParcelableExtra(RECEIVER)
        val count = intent.getIntExtra(COUNT, DEFAULT_COUNT)
        val endTimeLong = intent.getLongExtra(END_TIME, DEFAULT_TIME)
        val startTimeLong = intent.getLongExtra(START_TIME, DEFAULT_TIME)

        try {
            helper.requestMixes(count, startTimeLong, endTimeLong)
            receiver?.send(IDLE, null)
        } catch (exp: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "network request error", exp)
            receiver?.send(IDLE, null)
        }
    }

    companion object {
        val DEFAULT_TIME = 0L
        val FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
        protected val START_TIME = "START_TIME"
        protected val END_TIME = "END_TIME"
        protected val COUNT = "COUNT"
        protected val RECEIVER = "RECEIVER"

        val LOADING = 10
        val IDLE = 11

        init {
            FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        }

        private val DEFAULT_COUNT = 20
        private val TAG = NetworkService::class.java.name

        fun start(context: Context, receiver: ResultReceiver, startTime: Long? = null, endTime: Long? = null, count: Int = DEFAULT_COUNT) {
            val intent = Intent(context, NetworkService::class.java)
            intent.putExtra(START_TIME, startTime)
            intent.putExtra(END_TIME, endTime)
            intent.putExtra(COUNT, count)
            intent.putExtra(RECEIVER, receiver)
            context.startService(intent)
        }
    }
}