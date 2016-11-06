package pixel.kotlin.bassblog.ui

import android.content.ComponentName
import android.os.Bundle
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.network.Presenter


class AllMixFragment : BinderFragment(), MixAdapter.MixSelectCallback, Presenter.MixCallback {

    private var mMixAdapter: MixAdapter? = null
    private var mPresenter: Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.all_mix, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = Presenter(activity, this)
        mMixAdapter = MixAdapter(activity, this)
        all_mixes_recycler.layoutManager = LinearLayoutManager(activity)
        all_mixes_recycler.adapter = mMixAdapter
        all_mixes_recycler.addOnScrollListener(MixScrollListener())
    }

    inner class MixScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            val linearManager = recyclerView?.layoutManager as LinearLayoutManager

            val lastVisible = linearManager.findLastVisibleItemPosition()
            val totalCount = linearManager.itemCount
            mPresenter?.loadMoreIfNeed(lastVisible, totalCount)
        }
    }

    override fun onStart() {
        super.onStart()
        mPresenter?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mPresenter?.onStop()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        super.onServiceConnected(name, service)
        mPresenter?.onStart()
        updateCurrentMixData()
    }

    override fun onDataChanged(list: RealmResults<Mix>?) {
        mMixAdapter?.updateMixList(list)
        mPlaybackService?.updatePlayList(list)
        updateCurrentMixData()
    }

    private fun updateCurrentMixData() {
        // TODO
        if (activity is PagerActivity) {
            mPlaybackService?.isPlaying()?.let {
                (activity as PagerActivity).onPlayStatusChanged(it)
            }
        }
    }

    override fun onMixSelected(mix: Mix?) {
        mix?.let {
            mPlaybackService?.play(it)
        }
    }
}
