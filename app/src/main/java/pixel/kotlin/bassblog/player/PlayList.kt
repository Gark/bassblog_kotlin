package pixel.kotlin.bassblog.player


import android.database.Cursor
import android.support.v4.util.ArrayMap
import android.util.SparseArray
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.storage.BlogPost
import java.util.*

class PlayList {
    //    private val mPlayList = ArrayMap<String, BlogPost>()
    private val mPlayList = ArrayList<BlogPost>()

    private var mCurrentPosition = 0

    fun updatePlayList(cursor: Cursor?) {
        if (cursor != null && cursor.moveToFirst()) {
            if (mPlayList.size == cursor.count) {
                return
            }
            do {
                val post = PostUtils.getBlogPost(cursor)
                mPlayList.add(post)
            } while (cursor.moveToNext())
        }
    }

    fun updateCurrent(post: BlogPost) {
        mCurrentPosition = mPlayList.indexOf(post)
    }

    fun moveToNext() {
        mCurrentPosition = if (mCurrentPosition == mPlayList.size - 1) 0 else mCurrentPosition + 1
    }

    fun getCurrentPost(): BlogPost? {
        if (isEmpty()) {
            return null
        } else {
            return mPlayList[mCurrentPosition]
        }
    }

    fun isEmpty(): Boolean = mPlayList.isEmpty()

    fun clear() {
        mPlayList.clear()
    }

    fun moveToPrevious() {
        if (mCurrentPosition > 0) {
            mCurrentPosition -= 1
        }
    }
}