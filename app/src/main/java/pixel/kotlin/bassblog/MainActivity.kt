package pixel.kotlin.bassblog

import android.app.LoaderManager
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loaderManager.initLoader(0, Bundle.EMPTY, this)
        this.all_posts_recycler.adapter = PostAdapter(this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
