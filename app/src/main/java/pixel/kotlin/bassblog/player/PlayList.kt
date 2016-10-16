package pixel.kotlin.bassblog.player


import android.database.Cursor
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.storage.BlogPost
import java.util.*

class PlayList {

    private var mCurrentPosiotion = 0
    private val mList = ArrayList<BlogPost>()

    fun updatePlayList(cursor: Cursor?) {
        if (cursor != null && cursor.moveToFirst()) {

            if (mList.size == cursor.count) {
                return
            }

            mList.clear()
            do {
                mList.add(PostUtils.getBlogPost(cursor))
            } while (cursor.moveToNext())
        }
    }

    fun getCurrentPost(): BlogPost {
        return mList[mCurrentPosiotion]
    }

    fun isEmpty(): Boolean {
        return mList.isEmpty()
    }

    fun clear() {
        mList.clear()
    }
}
