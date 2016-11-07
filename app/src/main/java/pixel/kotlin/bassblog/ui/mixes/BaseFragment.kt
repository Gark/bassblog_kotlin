package pixel.kotlin.bassblog.ui.mixes

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.ui.BinderFragment

abstract class BaseFragment : BinderFragment(), BaseMixAdapter.MixSelectCallback {

    private var mMixAdapter: BaseMixAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.all_mix, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMixAdapter = getAdapter()
        all_mixes_recycler.layoutManager = LinearLayoutManager(activity)
        all_mixes_recycler.adapter = mMixAdapter
    }

    abstract fun getAdapter(): BaseMixAdapter

    abstract fun getTabId () : Int

    override fun onStop() {
        super.onStop()
        mMixAdapter?.onStop()
    }

    override fun onMixSelected(mix: Mix?) {
        mix?.let {
            mPlaybackService?.play(it, getTabId())
        }
    }
}
