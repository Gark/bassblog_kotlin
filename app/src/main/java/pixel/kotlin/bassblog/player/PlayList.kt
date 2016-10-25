package pixel.kotlin.bassblog.player


import android.database.Cursor
import android.util.SparseArray
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.storage.BlogPost
import java.util.*

class PlayList {

    val mPlayList = SparseArray<BlogPost>()

    private var mCurrentPosition = 0

    fun updatePlayList(cursor: Cursor?) {
        if (cursor != null && cursor.moveToFirst()) {

            if (mPlayList.size() == cursor.count) {
                return
            }

            mPlayList.clear()
            do {
                mPlayList.put(cursor.position, PostUtils.getBlogPost(cursor))
            } while (cursor.moveToNext())
        }
    }

    fun updateCurrent(blogPost: BlogPost?) {
        mCurrentPosition = mPlayList.indexOfValue(blogPost)
    }

    fun getCurrentPost(): BlogPost? {
        return mPlayList.get(mCurrentPosition)
    }

    fun isEmpty(): Boolean = mPlayList.isEmpty()

    fun clear() {
        mPlayList.clear()
    }
}

private fun <E> SparseArray<E>.isEmpty(): Boolean {
    return size() == 0
}
