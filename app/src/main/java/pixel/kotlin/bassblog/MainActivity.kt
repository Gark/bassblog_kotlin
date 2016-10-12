package pixel.kotlin.bassblog

import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import pixel.kotlin.bassblog.network.NetworkService


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private var mAdapter: PostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = PostAdapter(this)

        all_posts_recycler.layoutManager = LinearLayoutManager(applicationContext)
        all_posts_recycler.adapter = mAdapter

        supportLoaderManager.initLoader(0, Bundle.EMPTY, this)
        NetworkService.start(this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        mAdapter?.swapCursor(data)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return PostUtils.createLoader(this)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter?.swapCursor(null)
    }

}
