package pixel.kotlin.bassblog.player


import io.realm.RealmResults
import pixel.kotlin.bassblog.network.Mix
import java.util.*

class PlayList {
    private val mPlayList = ArrayList<Mix>()
    private val mPlayMode = PlayerMode()

    private var mCurrentPosition = 0

    fun updatePlayList(list: RealmResults<Mix>?) {
        list?.toList()?.let {
            mPlayList.clear()
            mPlayList.addAll(it)
        }
    }

//    fun updatePlayList(cursor: Cursor?) {
//        if (cursor != null && cursor.moveToFirst()) {
//            if (mPlayList.size == cursor.count) {
//                return
//            }
//            do {
//                val post = PostUtils.getBlogPost(cursor)
//                mPlayList.add(post)
//            } while (cursor.moveToNext())
//        }
//    }
//
//    fun updateCurrent(post: BlogPost) {
//        mCurrentPosition = mPlayList.indexOf(post)
//    }

    fun updateCurrent(mix: Mix) {
        mCurrentPosition = mPlayList.indexOf(mix)
    }

    // TODO optimise it.
    fun getCurrentPost(): Mix? {
        if (isEmpty()) {
            return null
        } else {
            return mPlayList[mCurrentPosition]
        }
    }

    fun isEmpty(): Boolean = mPlayList.isEmpty()

    fun clear() = mPlayList.clear()

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

    fun nextPlayMode(): Int = mPlayMode.nextMode()
}