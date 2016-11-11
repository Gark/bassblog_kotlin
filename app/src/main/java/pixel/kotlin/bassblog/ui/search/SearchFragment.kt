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

class SearchFragment : BaseFragment(), TextWatcher, Callback<PostsResponse> {


    private var mCall: Call<PostsResponse>? = null
    private var mApi: BassBlogApi? = null

    private fun initNetworkModule() {
        val builder = Retrofit.Builder()
        builder.baseUrl(BuildConfig.BASE_URL)
        builder.addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
        mApi = retrofit.create(BassBlogApi::class.java)
    }

    override fun getEmptyText(): Int = R.string.no_results

    override fun getLayout(): Int = R.layout.search_layout

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        search_edit_text.addTextChangedListener(this)
    }

    override fun afterTextChanged(text: Editable?) {
        val filter = search_edit_text.text.toString().trim()
        if (filter.length > 2) {
            mApi.let { initNetworkModule() }
            mCall = mApi?.search(BuildConfig.BLOG_ID, filter)
            mCall?.enqueue(this)
        } else {
            mBaseMixAdapter?.handleChanges()
        }
    }

    override fun onStop() {
        super.onStop()
        mCall?.cancel()
    }

    override fun onResponse(call: Call<PostsResponse>?, response: Response<PostsResponse>?) {
        val body = response?.body()
        val mixList = body?.items?.map {
            Mix().apply {
                mixId = it.id
                title = it.title
                image = it.getImage()
                track = it.getTrack()
                label = it.getLabel()
                content = it.content
                published = it.getTime(NetworkService.FORMATTER)
            }
        }
        mixList?.let { mBaseMixAdapter?.updateMixList(mixList) }
    }

    override fun onFailure(call: Call<PostsResponse>?, t: Throwable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun getAdapter(): BaseMixAdapter = SearchAdapter(activity, this)

    override fun getTabId(): Int = PlayList.SEARCH

}
