//package pixel.kotlin.bassblog.ui
//
//import android.content.ComponentName
//import android.os.Bundle
//import android.os.IBinder
//import android.support.design.widget.BottomSheetBehavior
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.view.View
//import io.realm.RealmResults
//import kotlinx.android.synthetic.main.activity_main.*
//import pixel.kotlin.bassblog.R
//import pixel.kotlin.bassblog.network.Mix
//import pixel.kotlin.bassblog.presenter.Presenter
//import pixel.kotlin.bassblog.presenter.Presenter.MixCallback
//
//class MainActivity : CommunicationActivity(), AllMixAdapter.MixSelectCallback, MixCallback {
//
//    private var mMixAdapter: AllMixAdapter? = null
//    private var mBehavior: BottomSheetBehavior<View>? = null
//    private var mPresenter: Presenter? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        mPresenter = Presenter(applicationContext, this)
//        mMixAdapter = AllMixAdapter(applicationContext, this)
//        all_mixes_recycler.layoutManager = LinearLayoutManager(applicationContext)
//        all_mixes_recycler.adapter = mMixAdapter
//        all_mixes_recycler.addOnScrollListener(MixScrollListener())
//
//        initBottomSheet()
//    }
//
//    inner class MixScrollListener : RecyclerView.OnScrollListener() {
//        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//            val linearManager = recyclerView?.layoutManager as LinearLayoutManager
//
//            val lastVisible = linearManager.findLastVisibleItemPosition()
//            val totalCount = linearManager.itemCount
//            mPresenter?.loadMoreIfNeed(lastVisible, totalCount)
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mPresenter?.onStart()
//    }
//
//    override fun onFragmentDestroyed() {
//        super.onFragmentDestroyed()
//        mPresenter?.onFragmentDestroyed()
//    }
//
//    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//        super.onServiceConnected(name, service)
//        mPresenter?.onStart()
//        updateCurrentMixData()
//    }
//
//    override fun onDataChanged(list: RealmResults<Mix>?) {
//        mMixAdapter?.updateMixList(list)
//        mPlaybackService?.updatePlayList(list)
//        updateCurrentMixData()
//    }
//
//    private fun updateCurrentMixData() {
//        // TODO
//        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ooo) as PlayerFragment
//        fragment.updateSongData()
//    }
//
//    override fun onMixSelected(mix: Mix?) {
//        mix?.let { mPlaybackService?.play(it) }
//    }
//
//    inner class MyCallback : BottomSheetBehavior.BottomSheetCallback() {
//
//        override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//        }
//
//        override fun onStateChanged(bottomSheet: View, newState: Int) {
//            // TODO
////            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ooo) as PlayerFragment
////            when (newState) {
////                BottomSheetBehavior.STATE_EXPANDED -> fragment.setPanelVisibility(View.INVISIBLE)
////                BottomSheetBehavior.STATE_COLLAPSED -> fragment.setPanelVisibility(View.VISIBLE)
////            }
//        }
//    }
//
//    private fun initBottomSheet() {
//        val bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet)
//        mBehavior = BottomSheetBehavior.from(bottomSheet)
//        mBehavior?.setBottomSheetCallback(MyCallback())
//    }
//
//    fun toggle() {
//        mBehavior?.state = if (mBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
//    }
//
//    override fun onBackPressed() {
//        if (mBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
//            mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
//        } else {
//            super.onBackPressed()
//        }
//    }
//}
