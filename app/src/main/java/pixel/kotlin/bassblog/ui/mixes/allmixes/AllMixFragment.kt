package pixel.kotlin.bassblog.ui.mixes.allmixes

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.all_mix.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.player.PlayList
import pixel.kotlin.bassblog.presenter.Presenter
import pixel.kotlin.bassblog.ui.mixes.BaseFragment
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter

class AllMixFragment : BaseFragment() {

    private var mPresenter: Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter = Presenter(activity)
    }

    override fun getLayout(): Int = R.layout.all_mix

    override fun onStop() {
        super.onStop()
        mPresenter?.onStop()
    }

    override fun getAdapter(): BaseMixAdapter {
        return AllMixesAdapter(activity, this)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mixes_recycler.addOnScrollListener(MixScrollListener())
    }

    inner class MixScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            val linearManager = recyclerView?.layoutManager as LinearLayoutManager

            val lastVisible = linearManager.findLastVisibleItemPosition()
            val totalCount = linearManager.itemCount
            mPresenter?.loadMoreIfNeed(lastVisible, totalCount)
        }
    }

    override fun getTabId(): Int = PlayList.ALL_MIX

    override fun getEmptyText(): Int = R.string.no_results
}
