package pixel.kotlin.bassblog.ui.mixes.allmixes

import android.content.Context
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter

class AllMixesAdapter(context: Context, callback: MixSelectCallback) : BaseMixAdapter(context, callback) {

    override fun needResize(): Boolean = true

    override fun getLayout(): Int = R.layout.item_post_list

    val mAllMixes: RealmResults<Mix>

    init {
        mAllMixes = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mAllMixes.addChangeListener { handleUpdates() }
    }

    private fun handleUpdates() {
        updateMixList(mAllMixes)
    }

    override fun onFragmentDestroyed() {
        mAllMixes.removeChangeListeners()
    }
}
