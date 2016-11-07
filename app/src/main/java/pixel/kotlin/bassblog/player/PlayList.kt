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

    private var mCurrentPosition = 0
    private var mCurrentMix: Mix? = null

    private val mPlayMode = PlayerMode()
    private var mCurrentList: RealmResults<Mix>
    private val mAllMix: RealmResults<Mix>
    private val mFavouriteMix: RealmResults<Mix>

    init {
        mAllMix = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mAllMix.addChangeListener { handleChanges() }
        mFavouriteMix = Realm.getDefaultInstance().where(Mix::class.java).equalTo("favourite", true).findAllSortedAsync("published", Sort.DESCENDING)

        mCurrentList = mAllMix
    }

    private fun handleChanges() {
        if (mCurrentMix == null && !mAllMix.isEmpty()) {
            mCurrentMix = mAllMix.first()
        }
    }

    fun updateCurrent(mix: Mix, tab: Int) {
        mCurrentMix = mix

        mCurrentList = when (tab) {
            ALL_MIX -> mAllMix
            FAVOURITE_MIX -> mFavouriteMix
            else -> mAllMix
        }

        mCurrentPosition = Math.max(0, mCurrentList.indexOf(mix))
    }

    fun getCurrentMix(): Mix? = mCurrentMix

    fun isEmpty(): Boolean = mCurrentList.isEmpty()

    fun clear() = mAllMix.removeChangeListeners()

    fun moveToNext() {
        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
            PlayerMode.LIST -> if (mCurrentPosition == mCurrentList.size - 1) 0 else mCurrentPosition + 1
            PlayerMode.SHUFFLE -> Random().nextInt(mCurrentList.size)
            else -> mCurrentPosition
        }
        mCurrentMix = mCurrentList[mCurrentPosition]
    }

    fun moveToPrevious() {
        mCurrentPosition = when (mPlayMode.getCurrentMode()) {
            PlayerMode.LIST -> Math.max(0, mCurrentPosition - 1)
            PlayerMode.SHUFFLE -> Random().nextInt(mCurrentList.size)
            else -> mCurrentPosition
        }
        mCurrentMix = mCurrentList[mCurrentPosition]
    }

    fun nextPlayMode() = mPlayMode.nextMode()

    fun getCurrentPlayMode() = mPlayMode.getCurrentModeResource()
}