package pixel.kotlin.bassblog.ui.search

import android.content.Context
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter

class SearchAdapter(context: Context, callback: MixSelectCallback) : BaseMixAdapter(context, callback) {

    override fun onFragmentDestroyed() {
        // do nothing
    }

    override fun getLayout(): Int = R.layout.item_post_list

    override fun needResize() = true

}