package pixel.kotlin.bassblog

import android.content.Context
import android.database.Cursor
import android.support.v4.content.CursorLoader

import pixel.kotlin.bassblog.storage.BlogPost
import pixel.kotlin.bassblog.storage.IoContract
import pixel.kotlin.bassblog.storage.PostConstant

internal object PostUtils {

    private val POST_PROJECTION = arrayOf(
            IoContract.Post.COL_ID, // 0
            IoContract.Post.COL_TITLE, // 1
            IoContract.Post.COL_IMAGE, // 2
            IoContract.Post.COL_LABEL, // 3
            IoContract.Post.COL_FAVORITE, // 4
            IoContract.Post.COL_TRACK // 5
    )

    private val INDEX_ID = 0
    private val INDEX_TITLE = 1
    private val INDEX_IMAGE = 2
    private val INDEX_LABEL = 3
    private val INDEX_FAVORITE = 4
    private val INDEX_TRACK = 5


    fun createLoader(context: Context): CursorLoader {
        val loader = CursorLoader(context)
        loader.uri = IoContract.Post.CONTENT_URI
        loader.projection = POST_PROJECTION
        return loader
    }

    fun getBlogPost(cursor: Cursor): BlogPost {
        return BlogPost(
                getId(cursor),
                getPostTitle(cursor),
                getImageUrl(cursor),
                getLabel(cursor),
                getTrack(cursor),
                isFavorite(cursor))
    }

    fun getId(cursor: Cursor): String {
        return cursor.getString(INDEX_ID)
    }

    fun getTrack(cursor: Cursor): String {
        return cursor.getString(INDEX_TRACK)
    }

    fun getPostTitle(cursor: Cursor): String {
        return cursor.getString(INDEX_TITLE)
    }

    fun getImageUrl(cursor: Cursor): String {
        return cursor.getString(INDEX_IMAGE)
    }

    fun getLabel(cursor: Cursor): String {
        return cursor.getString(INDEX_LABEL)
    }

    fun isFavorite(cursor: Cursor): Boolean {
        return cursor.getInt(INDEX_FAVORITE) == PostConstant.FAVORITE
    }
}