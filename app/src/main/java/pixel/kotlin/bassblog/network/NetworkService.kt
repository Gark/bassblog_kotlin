package pixel.kotlin.bassblog.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.support.v4.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import io.realm.Realm
import pixel.kotlin.bassblog.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

open class NetworkService : IntentService(NetworkService::class.java.name) {


    private val pattern = Pattern.compile("http(.*?)mp3")
    private var mApi: BassBlogApi? = null
    private var mReceiver: ResultReceiver? = null

    override fun onCreate() {
        super.onCreate()
        if (mApi == null) {
            initNetworkModule()
        }
    }

    private fun initNetworkModule() {
        val builder = Retrofit.Builder()
        builder.baseUrl(BuildConfig.BASE_URL)
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        mApi = retrofit.create(BassBlogApi::class.java)
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        try {

            mReceiver = intent.getParcelableExtra(RECEIVER)
            val count = intent.getIntExtra(COUNT, DEFAULT_COUNT)

            val endTimeLong = intent.getLongExtra(END_TIME, DEFAULT_TIME)
            val endTime = convertTime(endTimeLong)

            val startTimeLong = intent.getLongExtra(START_TIME, DEFAULT_TIME)
            val startTime = convertTime(startTimeLong)

            handleRequest(count, startTime, endTime)
            mReceiver?.send(IDLE, null)
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "network request error", e)
            mReceiver?.send(IDLE, null)
        }
    }

    private fun convertTime(time: Long): String? {
        var result: String? = null
        if (time != DEFAULT_TIME) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            result = FORMATTER.format(calendar.time)
        }
        return result
    }

    @Throws(IOException::class)
    private fun handleRequest(count: Int, startTime: String?, endTime: String?) {
        val call = mApi!!.posts(BuildConfig.BLOG_ID, true, true, null, BassBlogApi.ITEMS, BuildConfig.API_KEY, startTime, endTime, count)
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            val list = body.items
            list?.let { saveRealmList(it) }
        } else {
            val error = String(response.errorBody().bytes())
            throw IOException("Unsuccessful result -> " + error)
        }
    }

    private fun saveRealmList(list: List<PostsResponse.RawPost>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val mixList = ArrayList<Mix>()
        for (item in list) {
            val mix = Mix().apply {
                mixId = item.id
                title = item.title
                image = getImageUrl(item)
                track = getTrack(item)
                label = TextUtils.join(", ", item.labels)
                published = getTime(item.published)
            }
            mixList.add(mix)
        }
        realm.copyToRealmOrUpdate(mixList)
        realm.commitTransaction()
    }

    private fun getTrack(item: PostsResponse.RawPost): String? {
        var trackUrl: String? = null
        val matcher = pattern.matcher(item.content)
        if (matcher.find()) {
            trackUrl = "http" + (matcher.group(1) + "mp3")
        }
        return trackUrl
    }

    private fun getTime(published: String?): Long {
        try {
            return FORMATTER.parse(published).time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0L
    }

    private fun getImageUrl(item: PostsResponse.RawPost): String = item.images?.get(0)?.url ?: ""

    companion object {
        protected val DEFAULT_TIME = 0L
        protected val FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
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