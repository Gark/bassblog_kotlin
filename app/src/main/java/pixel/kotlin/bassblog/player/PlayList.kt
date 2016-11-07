package pixel.kotlin.bassblog.player


import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import pixel.kotlin.bassblog.network.Mix

class PlayList {

    companion object {
        val ALL_MIX = 0
        val FAVOURITE_MIX = 1
        val SEARCH = 2
    }

    private var mCurrentPosition = 0
    private var mCurrentMix: Mix? = null

    private val mPlayMode = PlayerMode()
    private val mAllMix: RealmResults<Mix>
    private val mFavouriteMix: RealmResults<Mix>

    init {
        mAllMix = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mAllMix.addChangeListener { handleChanges() }
        mFavouriteMix = Realm.getDefaultInstance().where(Mix::class.java).equalTo("favourite", true).findAllSortedAsync("published", Sort.DESCENDING)
    }

    private fun handleChanges() {
        if (mCurrentMix == null && !mAllMix.isEmpty()) {
            mCurrentMix = mAllMix.first()
        }
    }

    fun updateCurrent(mix: Mix, tab: Int) {
        mCurrentMix = mix
        mCurrentPosition = Math.max(0, mAllMix.indexOf(mix))
    }

    fun getCurrentMix(): Mix? = mCurrentMix

    fun isEmpty(): Boolean = mAllMix.isEmpty()

    fun clear() = mAllMix.removeChangeListeners()

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