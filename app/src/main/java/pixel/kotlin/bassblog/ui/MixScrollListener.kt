package pixel.kotlin.bassblog.ui

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class MixScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        val adapter = recyclerView?.adapter as MixAdapter

        val linearManager = recyclerView?.layoutManager as LinearLayoutManager

        linearManager.findLastVisibleItemPosition()
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
    }
}
