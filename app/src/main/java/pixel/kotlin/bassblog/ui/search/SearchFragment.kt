package pixel.kotlin.bassblog.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.search_layout.*
import pixel.kotlin.bassblog.BuildConfig
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.*
import pixel.kotlin.bassblog.player.PlayList
import pixel.kotlin.bassblog.ui.mixes.BaseFragment
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : BaseFragment(), TextWatcher {


    override fun getEmptyText(): Int = R.string.no_results

    override fun getLayout(): Int = R.layout.search_layout

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_edit_text.addTextChangedListener(this)
    }

    override fun afterTextChanged(text: Editable?) {
        val filter = search_edit_text.text.toString().trim()
        if (filter.length > 2) {
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun getAdapter(): BaseMixAdapter = SearchAdapter(activity, this)

    override fun getTabId(): Int = PlayList.SEARCH

}
