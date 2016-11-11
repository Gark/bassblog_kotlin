package pixel.kotlin.bassblog.ui.mixes

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.all_mix.*
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.ui.BinderFragment
import pixel.kotlin.bassblog.ui.MusicPlayerActivity

abstract class BaseFragment : BinderFragment(), BaseMixAdapter.MixSelectCallback {

    protected var mBaseMixAdapter: BaseMixAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBaseMixAdapter = getAdapter()
        mixes_recycler.layoutManager = LinearLayoutManager(activity)
        mixes_recycler.adapter = mBaseMixAdapter
    }

    abstract fun getLayout(): Int

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

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        val mix = mPlaybackService?.getPlayingMix()
        mBaseMixAdapter?.updatePlayingMix(mix)
    }
}
