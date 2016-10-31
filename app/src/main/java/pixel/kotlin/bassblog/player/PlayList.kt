package pixel.kotlin.bassblog.player


import android.database.Cursor
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.storage.BlogPost
import java.util.*

class PlayList {
    private val mPlayList = ArrayList<BlogPost>()
    private val mPlayMode = PlayerMode()

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

    fun moveToNext() {
        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
            PlayerMode.LIST -> if (mCurrentPosition == mPlayList.size - 1) 0 else mCurrentPosition + 1
            PlayerMode.SHUFFLE -> Random().nextInt(mPlayList.size)
            else -> mCurrentPosition
        }
    }

    fun moveToPrevious() {
        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
            PlayerMode.LIST -> Math.max(0, mCurrentPosition - 1)
            PlayerMode.SHUFFLE -> Random().nextInt(mPlayList.size)
            else -> mCurrentPosition
        }
    }

    fun nextPlayMode(): Int {
        return mPlayMode.nextMode()
    }
}