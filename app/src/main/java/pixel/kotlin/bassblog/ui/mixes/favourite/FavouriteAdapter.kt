package pixel.kotlin.bassblog.ui.mixes.favourite

import android.content.Context
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter

class FavouriteAdapter(context: Context, callback: MixSelectCallback) : BaseMixAdapter(context, callback) {

    override fun needResize(): Boolean = false

    override fun getLayout(): Int = R.layout.item_favourite_item

    val mFavoriteMixes: RealmResults<Mix>

    init {
        mFavoriteMixes = Realm.getDefaultInstance().where(Mix::class.java).equalTo("favourite", true).findAllSortedAsync("published", Sort.DESCENDING)
        mFavoriteMixes.addChangeListener { updateChanges() }
    }

    private fun updateChanges() {
        updateMixList(mFavoriteMixes)
    }

    override fun onFragmentDestroyed() {
        mFavoriteMixes.removeChangeListeners()
    }
}
