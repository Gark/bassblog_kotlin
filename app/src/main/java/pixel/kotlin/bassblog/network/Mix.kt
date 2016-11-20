package pixel.kotlin.bassblog.network

import android.text.TextUtils
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

open class Mix : RealmObject() {
    @PrimaryKey
    var mixId: Long = 0L
    var title: String? = null
    var image: String? = null
    var label: String? = null
    var track: String? = null
    var content: String? = null
    var published = 0L
    var favourite_time = 0L
    var favourite: Boolean = false
    var url: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mix) return false

        if (mixId != other.mixId) return false

        return true
    }

    override fun hashCode(): Int {
        return mixId.hashCode()
    }
}

fun PostsResponse.RawPost.getImage(): String? = images?.get(0)?.url

private val pattern = Pattern.compile("http(.*?)mp3")

fun PostsResponse.RawPost.getTrack(): String? {
    var trackUrl: String? = null
    val matcher = pattern.matcher(content)
    if (matcher.find()) {
        trackUrl = "http" + (matcher.group(1) + "mp3")
    }
    return trackUrl
}

fun PostsResponse.RawPost.getLabel(): String? {
    return labels?.let { TextUtils.join(", ", labels) }
}


fun PostsResponse.RawPost.getTime(formatter: SimpleDateFormat): Long {
    try {
        return formatter.parse(published).time
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return 0L
}



