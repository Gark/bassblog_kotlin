package pixel.kotlin.bassblog.network

import io.realm.Realm
import pixel.kotlin.bassblog.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class NetworkHelper {

    private val mApi: BassBlogApi

    init {
        val builder = Retrofit.Builder()
        builder.baseUrl(BuildConfig.BASE_URL)
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        mApi = retrofit.create(BassBlogApi::class.java)
    }


    @Throws(IOException::class)
    fun requestMixes(count: Int, startTimeLong: Long, endTimeLong: Long): Int {
        var mixCount = 0

        val endTime = convertTime(endTimeLong)
        val startTime = convertTime(startTimeLong, 1)

        val call = mApi.posts(BuildConfig.BLOG_ID, true, true, null, BassBlogApi.ITEMS, BuildConfig.API_KEY, startTime, endTime, count)
        val response = call.execute()
        if (response.isSuccessful) {
            val body = response.body()
            val list = body?.items
            list?.let {
                saveRealmList(it)
                mixCount = it.size
            }
        } else {
            val error = String(response.errorBody()?.bytes() ?: ByteArray(0))
            throw IOException("Unsuccessful result -> $error")
        }
        return mixCount
    }

    private fun saveRealmList(list: List<PostsResponse.RawPost>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val mixList = list.map {
            Mix().apply {
                mixId = it.id
                title = it.title
                url = it.url
                image = it.getImage()
                track = it.getTrack()
                label = it.getLabel()
                content = it.content
                published = it.getTime(NetworkService.FORMATTER)
            }
        }
        realm.copyToRealmOrUpdate(mixList)
        realm.commitTransaction()
    }

    private fun convertTime(time: Long, trick: Int = 0): String? {
        var result: String? = null
        if (time != NetworkService.DEFAULT_TIME) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time
            if (trick > 0) {
                calendar.add(Calendar.MINUTE, trick)
            }
            result = NetworkService.FORMATTER.format(calendar.time)
        }
        return result
    }
}
