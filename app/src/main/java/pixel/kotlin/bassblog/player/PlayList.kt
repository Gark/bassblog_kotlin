package pixel.kotlin.bassblog.player


import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import pixel.kotlin.bassblog.network.Mix
import java.util.*

class PlayList {

    companion object {
        val ALL_MIX = 0
        val FAVOURITE_MIX = 1
        val SEARCH = 2
    }

    private val mPlayList = ArrayList<Mix>()
    private val mPlayMode = PlayerMode()

    private var mCurrentPosition = 0
    private var mCurrentMix: Mix? = null

    private val mAllMix: RealmResults<Mix>
    private val mFavouriteMix: RealmResults<Mix>

    init {
        mAllMix = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mAllMix.addChangeListener { handleChanges() }
        mFavouriteMix = Realm.getDefaultInstance().where(Mix::class.java).equalTo("favourite", true).findAllSortedAsync("published", Sort.DESCENDING)
    }

    private fun handleChanges() {
        mCurrentMix.let {
            if (!mAllMix.isEmpty()) {
                mCurrentMix = mAllMix.first()
            }
        }
    }


    fun updateCurrent(mix: Mix) {
        mCurrentMix = mix
        mCurrentPosition = Math.max(0, mAllMix.indexOf(mix))
    }

    fun getCurrentMix(): Mix? = mCurrentMix

    fun isEmpty(): Boolean = mPlayList.isEmpty()

    fun clear() = mPlayList.clear()

    fun moveToNext() {
//        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
//            PlayerMode.LIST -> if (mCurrentPosition == mPlayList.size - 1) 0 else mCurrentPosition + 1
//            PlayerMode.SHUFFLE -> Random().nextInt(mPlayList.size)
//            else -> mCurrentPosition
//        }
    }

    fun moveToPrevious() {
//        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
//            PlayerMode.LIST -> Math.max(0, mCurrentPosition - 1)
//            PlayerMode.SHUFFLE -> Random().nextInt(mPlayList.size)
//            else -> mCurrentPosition
//        }
    }

    fun nextPlayMode(): Int = mPlayMode.nextMode()
}