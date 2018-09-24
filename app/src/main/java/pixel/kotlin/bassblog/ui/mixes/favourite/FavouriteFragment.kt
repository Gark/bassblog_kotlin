package pixel.kotlin.bassblog.ui.mixes.favourite

import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.player.PlayList
import pixel.kotlin.bassblog.ui.mixes.BaseFragment
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter

class FavouriteFragment : BaseFragment() {

    override fun getTabId(): Int = PlayList.FAVOURITE_MIX

    override fun getAdapter(): BaseMixAdapter = FavouriteAdapter(activity!!.applicationContext, this)

    override fun getLayout(): Int = R.layout.all_mix

    override fun getEmptyText(): Int = R.string.no_favourites
}
