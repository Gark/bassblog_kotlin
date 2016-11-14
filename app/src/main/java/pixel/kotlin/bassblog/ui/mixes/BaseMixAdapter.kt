package pixel.kotlin.bassblog.ui.mixes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseMixAdapter(context: Context, val callback: MixSelectCallback) : RecyclerView.Adapter<BaseMixAdapter.MixHolder>() {

    interface MixSelectCallback {
        fun onMixSelected(mix: Mix?)

        fun onDataUpdated(showEmptyView: Boolean)
    }

    private val mCalendar = Calendar.getInstance()
    private var fmt: DateFormat = SimpleDateFormat("MMMM yyyy", Locale.US)
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mAllMix: ArrayList<Mix>
    private val picasso: Picasso

    private var mCurrentMix: Mix? = null

    init {
        setHasStableIds(true)
        picasso = Picasso.with(context)
        mAllMix = ArrayList()
    }

    fun updateMixList(list: List<Mix>) {
        mAllMix.clear()
        mAllMix.addAll(list)
        handleChanges()
    }

    abstract fun getLayout(): Int

    abstract fun needResize(): Boolean

    abstract fun onFragmentDestroyed()

    fun handleChanges() {
        callback.onDataUpdated(mAllMix.isEmpty())
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: MixHolder?) {
        holder?.onViewRecycled()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MixHolder(mInflater.inflate(getLayout(), parent, false))

    override fun onBindViewHolder(holder: MixHolder, position: Int) = holder.displayData(mAllMix[position], showHeader(position), position)

    private fun showHeader(position: Int): Boolean {
        if (position == 0) {
            return true
        } else {
            mCalendar.timeInMillis = mAllMix[position - 1].published
            val previousMonth = mCalendar.get(Calendar.MONTH)

            mCalendar.timeInMillis = mAllMix[position].published
            val currentMonth = mCalendar.get(Calendar.MONTH)
            return currentMonth != previousMonth
        }
    }

    override fun getItemCount(): Int = mAllMix.size

    inner class MixHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mPostTitle = itemView.findViewById(R.id.post_title) as TextView
        private val mPostLabel = itemView.findViewById(R.id.post_label) as TextView
        private val mHeader = itemView.findViewById(R.id.header_date) as TextView?
        private val mPostImage = itemView.findViewById(R.id.mix_image) as ImageView
        private val mNowPlayingImage = itemView.findViewById(R.id.now_playing) as ImageView
        private val mCalendar = Calendar.getInstance()
        private var mMix: Mix? = null

        init {
            itemView.setOnClickListener { handleClick() }
        }

        private fun handleClick() {
            callback.onMixSelected(mMix)
        }

        fun displayData(mix: Mix, showHeader: Boolean, position: Int) {
            mMix = mix
            mPostTitle.text = "$position ${mix.title}"
            mPostLabel.text = mix.label
            mNowPlayingImage.visibility = if (mix == mCurrentMix) View.VISIBLE else View.INVISIBLE
            mCalendar.timeInMillis = mix.published

            mHeader?.let {
                it.text = fmt.format(mCalendar.time)
                it.visibility = if (showHeader) View.VISIBLE else View.GONE
            }

            val requestCreator = Picasso.with(itemView.context).load(mix.image)
            if (needResize()) {
                requestCreator.resizeDimen(R.dimen.image_width, R.dimen.image_height)
            } else {
                requestCreator.fit()
            }
            requestCreator.into(mPostImage)
        }

        fun onViewRecycled() {
            picasso.cancelRequest(mPostImage)
        }
    }

    override fun getItemId(position: Int): Long {
        return mAllMix[position].mixId
    }

    fun updatePlayingMix(mix: Mix?) {
        mCurrentMix = mix
        notifyDataSetChanged()
    }
}
