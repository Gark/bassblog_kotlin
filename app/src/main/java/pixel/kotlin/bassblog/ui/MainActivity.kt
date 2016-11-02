package pixel.kotlin.bassblog.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.network.NetworkService

class MainActivity : CommunicationActivity(), MixAdapter.MixSelectCallback {

    private var mMixAdapter: MixAdapter? = null
    private var mBehavior: BottomSheetBehavior<View>? = null
    private var mMixResults: RealmResults<Mix>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMixAdapter = MixAdapter(applicationContext, this)
        all_mixes_recycler.layoutManager = LinearLayoutManager(applicationContext)
        all_mixes_recycler.adapter = mMixAdapter
        all_mixes_recycler.addOnScrollListener(MixScrollListener())

        initBottomSheet()
        NetworkService.start(this)
    }

    override fun onStart() {
        super.onStart()
        requestDataAsync()
    }

    override fun onStop() {
        super.onStop()
        mMixResults?.removeChangeListeners()
    }

    private fun requestDataAsync() {
        mMixResults = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mMixResults?.addChangeListener { handleChanges() }
    }

    private fun handleChanges() {
        mMixAdapter?.updateMixList(mMixResults)
        mPlaybackService?.updatePlayList(mMixResults)

        // TODO
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ooo) as PlayerFragment
        fragment.updateSongData()
    }

    override fun onMixSelected(mix: Mix?) {
        mix?.let { mPlaybackService?.play(it) }
    }

    inner class MyCallback : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            // TODO
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ooo) as PlayerFragment
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> fragment.setPanelVisibility(View.INVISIBLE)
                BottomSheetBehavior.STATE_COLLAPSED -> fragment.setPanelVisibility(View.VISIBLE)
            }
        }
    }

    private fun initBottomSheet() {
        val bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet)
        mBehavior = BottomSheetBehavior.from(bottomSheet)
        mBehavior?.setBottomSheetCallback(MyCallback())
    }

    fun toggle() {
        mBehavior?.state = if (mBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onBackPressed() {
        if (mBehavior?.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}
