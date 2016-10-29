package pixel.kotlin.bassblog.ui

import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetBehavior.BottomSheetCallback
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.NetworkService
import pixel.kotlin.bassblog.storage.BlogPost
import pixel.kotlin.bassblog.ui.PostAdapter.PostCallback


class MainActivity : CommunicationActivity(), LoaderManager.LoaderCallbacks<Cursor>, PostCallback {

    private var mAdapter: PostAdapter? = null
    private var mBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAdapter = PostAdapter(this, this)

        all_posts_recycler.layoutManager = LinearLayoutManager(applicationContext)
        all_posts_recycler.adapter = mAdapter
        initBottomSheet()

        supportLoaderManager.initLoader(0, Bundle.EMPTY, this)
        NetworkService.start(this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mAdapter?.swapCursor(data)
        mPlaybackService?.updatePlayList(data)
        // TODO
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_ooo) as PlayerFragment
        fragment.updateSongData()
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return PostUtils.createLoader(this)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter?.swapCursor(null)
    }

    override fun onPostSelected(blogPost: BlogPost) {
        mPlaybackService?.play(blogPost.postId)
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
