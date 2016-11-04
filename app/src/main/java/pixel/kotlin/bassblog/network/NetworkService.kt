package pixel.kotlin.bassblog.network

import android.app.IntentService
import android.content.Context
import android.content.Intent
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
            var startTime: String? = null
            val startTimeLong = intent.getLongExtra(START_TIME, DEFAULT_TIME)
            val count = intent.getIntExtra(COUNT, DEFAULT_COUNT)

            if (startTimeLong != DEFAULT_TIME) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = startTimeLong
                startTime = FORMATTER.format(calendar.time)
            }

            handleRequest(count, startTime)
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "network request error", e)
        }
    }

    @Throws(IOException::class)
    private fun handleRequest(count: Int, startTime: String?) {
        val call = mApi!!.posts(BuildConfig.BLOG_ID, true, true, null, BassBlogApi.ITEMS, BuildConfig.API_KEY, startTime, count)
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            val list = body.items
            list?.let { saveRealmList(it) }
        } else {
            throw IOException("Unsuccessful result")
        }
    }

    private fun saveRealmList(list: List<PostsResponse.RawPost>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        for (item in list) {
            val mix = Mix().apply {
                mixId = item.id
                title = item.title
                image = getImageUrl(item)
                track = getTrack(item)
                label = TextUtils.join(", ", item.labels)
                published = getTime(item.published)
            }
            realm.copyToRealmOrUpdate(mix)
        }
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

    private fun getImageUrl(item: PostsResponse.RawPost): String {
        var url = ""
        if (item.images != null && item.images!!.size > 0) {
            url = item.images!![0].url.toString()
        }
        return url
    }

    companion object {
        protected val DEFAULT_TIME = 0L
        protected val FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
        protected val START_TIME = "START_TIME"
        protected val COUNT = "COUNT"

        init {
            FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        }

        private val DEFAULT_COUNT = 20
        private val TAG = NetworkService::class.java.name

        fun start(context: Context, startTime: Long? = null, count: Int = DEFAULT_COUNT) {
            val intent = Intent(context, NetworkService::class.java)
            intent.putExtra(START_TIME, startTime)
            intent.putExtra(COUNT, count)
            context.startService(intent)
        }
    }
}