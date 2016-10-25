package pixel.kotlin.bassblog.ui

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.PostUtils
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.NetworkService
import pixel.kotlin.bassblog.storage.BlogPost
import pixel.kotlin.bassblog.ui.PostAdapter.PostCallback


class MainActivity : CommunicationActivity(), LoaderManager.LoaderCallbacks<Cursor>, PostCallback {

    private var mAdapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = PostAdapter(this, this)

        all_posts_recycler.layoutManager = LinearLayoutManager(applicationContext)
        all_posts_recycler.adapter = mAdapter

        supportLoaderManager.initLoader(0, Bundle.EMPTY, this)
        NetworkService.start(this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mAdapter?.swapCursor(data)
        mPlaybackService?.updatePlayList(data)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return PostUtils.createLoader(this)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter?.swapCursor(null)
    }

    override fun onPostSelected(blogPost: BlogPost?) {
        MusicPlayerActivity.start(applicationContext, blogPost)
    }
}
