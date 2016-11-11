package pixel.kotlin.bassblog.ui.mixes

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.all_mix.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.ui.BinderFragment
import pixel.kotlin.bassblog.ui.MusicPlayerActivity

abstract class BaseFragment : BinderFragment(), BaseMixAdapter.MixSelectCallback {

    private var mBaseMixAdapter: BaseMixAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.all_mix, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBaseMixAdapter = getAdapter()
        all_mixes_recycler.layoutManager = LinearLayoutManager(activity)
        all_mixes_recycler.adapter = mBaseMixAdapter
    }

    abstract fun getAdapter(): BaseMixAdapter

    abstract fun getTabId(): Int

    override fun onDestroy() {
        super.onDestroy()
        mBaseMixAdapter?.onFragmentDestroyed()
    }

    override fun onMixSelected(mix: Mix?) {
        mix?.let {
            mPlaybackService?.play(it, getTabId())
            MusicPlayerActivity.start(activity)
        }
    }

    override fun onDataUpdated(showEmptyView: Boolean) {
        if (showEmptyView) {
            empty_view.visibility = View.VISIBLE
        } else {
            empty_view.visibility = View.GONE
        }
    }
}
