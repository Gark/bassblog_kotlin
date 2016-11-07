package pixel.kotlin.bassblog.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.ui.BinderFragment


class SearchFragment : BinderFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.all_mix, container, false)
    }

}
