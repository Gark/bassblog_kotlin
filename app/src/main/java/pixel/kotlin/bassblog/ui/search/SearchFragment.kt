package pixel.kotlin.bassblog.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import io.realm.Case
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.search_layout.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.*
import pixel.kotlin.bassblog.player.PlayList
import pixel.kotlin.bassblog.ui.mixes.BaseFragment
import pixel.kotlin.bassblog.ui.mixes.BaseMixAdapter
import java.util.*


class SearchFragment : BaseFragment(), TextWatcher {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_edit_text.addTextChangedListener(this)

        val array = arrayListOf(_320_kbps, deep, drumfunk, hard, liquid, neurofunk, oldschool, ragga_jungle)
        for (item in array) item.setOnClickListener {
            search_edit_text.append(it.tag as String)
        }
        search_edit_text.setOnEditorActionListener({ textView, i, keyEvent -> handleSearch() })

    }

    private fun handleSearch(): Boolean {
        return true
    }

    fun query(filter: String) {
        val mAllMixes = Realm.getDefaultInstance().where(Mix::class.java)
                .contains("title", filter, Case.INSENSITIVE)
                .or()
                .contains("label", filter, Case.INSENSITIVE)
                .findAllSortedAsync("published", Sort.DESCENDING)
        mAllMixes?.addChangeListener { list -> handleUpdates(list) }
    }

    private fun handleUpdates(list: RealmResults<Mix>) {
        mBaseMixAdapter?.updateMixList(list)
    }

    override fun afterTextChanged(text: Editable?) {
        val filter = search_edit_text.text.toString().trim()
        if (filter.isNotEmpty()) {
            query(filter)
        } else {
            mBaseMixAdapter?.updateMixList(Collections.emptyList())
        }
    }

    override fun onDataUpdated(showEmptyView: Boolean) {
        if (showEmptyView) {
            if (search_edit_text.text.toString().isEmpty()) {
                categories_panel.visibility = View.VISIBLE
                empty_view.visibility = View.GONE
            } else {
                categories_panel.visibility = View.GONE
                empty_view.visibility = View.VISIBLE
            }
        } else {
            categories_panel.visibility = View.GONE
            empty_view.visibility = View.GONE
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun getEmptyText(): Int = R.string.no_results

    override fun getLayout(): Int = R.layout.search_layout

    override fun getAdapter(): BaseMixAdapter = SearchAdapter(activity, this)

    override fun getTabId(): Int = PlayList.SEARCH

}
