package pixel.kotlin.bassblog.player


import android.database.Cursor
import android.support.v4.util.ArrayMap
import android.util.SparseArray
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.storage.BlogPost
import java.util.*

class PlayList {
    private val mPlayList = ArrayMap<String, BlogPost>()

    private var mCurrentPosition = 0

    fun updatePlayList(cursor: Cursor?) {
        if (cursor != null && cursor.moveToFirst()) {
            if (mPlayList.size == cursor.count) {
                return
            }
            mPlayList.clear()
            do {
                val post = PostUtils.getBlogPost(cursor)
                mPlayList.put(post.postId, post)
            } while (cursor.moveToNext())
        }
    }

    fun updateCurrent(id: String) {
        mCurrentPosition = mPlayList.indexOfKey(id)
    }

    fun moveToNext() {
        mCurrentPosition = if (mCurrentPosition == mPlayList.size - 1) 0 else mCurrentPosition + 1
    }

    fun getCurrentPost(): BlogPost? {
        if (isEmpty()) {
            return null
        } else {
            return mPlayList.valueAt(mCurrentPosition)
        }
    }

    fun isEmpty(): Boolean = mPlayList.isEmpty

    fun clear() {
        mPlayList.clear()
    }

    fun moveToPrevious() {
        if (mCurrentPosition > 0) {
            mCurrentPosition = mCurrentPosition - 1
        }
    }
}