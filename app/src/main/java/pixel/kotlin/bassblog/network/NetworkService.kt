package pixel.kotlin.bassblog.network

import android.app.IntentService
import android.content.ContentProviderOperation
import android.content.Context
import android.content.Intent
import android.content.OperationApplicationException
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import io.realm.Realm
import pixel.kotlin.bassblog.BuildConfig
import pixel.kotlin.bassblog.storage.IoContract
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
        try {
            handleRequest()
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) Log.e(TAG, "network request error", e)
        }
    }

    @Throws(IOException::class)
    private fun handleRequest() {
        val call = mApi!!.posts(BuildConfig.BLOG_ID, true, true, null, BassBlogApi.ITEMS, BuildConfig.API_KEY, MAX_RESULT)
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            val list = body.items
            list?.let { applyBatch(it) }
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
                label = TextUtils.join(", ", item.labels)
                published = getTime(item.published)
            }
            realm.copyToRealmOrUpdate(mix)
        }
        realm.commitTransaction()
    }

    private fun applyBatch(list: List<PostsResponse.RawPost>) {
        val ops = ArrayList<ContentProviderOperation>()
        for (item in list) {
            ops.add(ContentProviderOperation.newInsert(IoContract.Post.CONTENT_URI)
                    .withValue(IoContract.Post.COL_POST_ID, item.id)
                    .withValue(IoContract.Post.COL_TITLE, item.title)
                    .withValue(IoContract.Post.COL_PUBLISHED, getTime(item.published))
                    .withValue(IoContract.Post.COL_LABEL, TextUtils.join(", ", item.labels))
                    .withValue(IoContract.Post.COL_IMAGE, getImageUrl(item))
                    .withValue(IoContract.Post.COL_TRACK, getTrack(item))
                    .build())
        }
        try {
            val result = contentResolver.applyBatch(IoContract.AUTHORITY, ops)
            Log.e("NetworkService", " result " + result.size)
        } catch (e: RemoteException) {
            Log.e("NetworkService", "apply batch error", e)
        } catch (e: OperationApplicationException) {
            Log.e("NetworkService", "apply batch error", e)
        }

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
        protected val FORMATTER = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

        init {
            FORMATTER.timeZone = TimeZone.getTimeZone("UTC")
        }

        private val TAG = NetworkService::class.java.name
        private val MAX_RESULT = 5
        fun start(context: Context) {
            val intent = Intent(context, NetworkService::class.java)
            context.startService(intent)
        }
    }
}
/**
 * Creates an IntentService.  Invoked by your subclass's constructor.
 */
