package pixel.kotlin.bassblog.ui.mixes

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import pixel.kotlin.bassblog.BassBlogApplication
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.download.DownloadEntity.Companion.DOWNLOADED
import pixel.kotlin.bassblog.download.DownloadEntity.Companion.IN_PROGRESS
import pixel.kotlin.bassblog.download.DownloadEntity.Companion.NOT_DOWNLOADED
import pixel.kotlin.bassblog.download.DownloadEntity.Companion.PENDING
import pixel.kotlin.bassblog.download.MixDownloader
import pixel.kotlin.bassblog.download.ProgressListener
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
    private val mAllMix: ArrayList<Mix> = ArrayList()
    private val picasso: Picasso = Picasso.with(context)
    private val mMixDownLoader = (context.applicationContext as BassBlogApplication).getMixDownloader()
    private var mCurrentMix: Mix? = null

    init {
        setHasStableIds(true)
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

    override fun onBindViewHolder(holder: MixHolder, position: Int) = holder.displayData(mAllMix[position], showHeader(position))

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
        private val mDownloadIcon = itemView.findViewById(R.id.download_icon) as ImageView?
        private val mFileSize = itemView.findViewById(R.id.file_size_item) as TextView?
        private val mProgressPercent = itemView.findViewById(R.id.progress_percent) as TextView?
        private val mProgressBar = itemView.findViewById(R.id.downloading_progressbar) as ProgressBar?
        private val mCancel = itemView.findViewById(R.id.download_cancel) as TextView?
        private val mCalendar = Calendar.getInstance()

        private var mMix: Mix? = null
        private var mMyProgress: ItemProgressListener? = null

        init {
            mMyProgress = ItemProgressListener()
            itemView.setOnClickListener { handleClick() }
            mCancel?.setOnClickListener { view -> handleCancel(view) }
        }

        private fun handleCancel(view: View) {
            Toast.makeText(view.context, "cancel -> ${mMix?.mixId}", Toast.LENGTH_SHORT).show()
        }

        private fun handleClick() {
            callback.onMixSelected(mMix)
        }

        fun displayData(mix: Mix, showHeader: Boolean) {
            mMix = mix
            mMixDownLoader.addProgressListener(mMyProgress!!, mMix?.mixId) // TODO
            handleDownloadState(mix)
            displayTextInfo(mix)

//            mFileSize?.text = mMixDownLoader.getFileSize(mix.mixId) // TODO

            displayHeader(showHeader)
            displayImage(mix)
        }

        private fun handleDownloadState(mix: Mix) {
            val state = mMixDownLoader.getState(mix.mixId)
            when (state) {
                DOWNLOADED -> mDownloadIcon?.setColorFilter(Color.RED)
                NOT_DOWNLOADED -> mDownloadIcon?.setColorFilter(Color.LTGRAY)
                IN_PROGRESS -> mDownloadIcon?.setColorFilter(Color.CYAN)
                PENDING -> mDownloadIcon?.setColorFilter(Color.YELLOW)
            }
        }

        private inner class ItemProgressListener : ProgressListener {
            override fun update(mixId: Long, progress: Int, readMb: Int, totalMb: Int, done: Boolean) {
                mFileSize?.text = String.format("%d Mb", totalMb)
                mProgressPercent?.text = String.format("%d %%", progress)
                mProgressBar?.progress = progress
                handleDownloadState(mMix!!)
            }
        }

        private fun displayHeader(showHeader: Boolean) {
            mHeader?.let {
                it.text = fmt.format(mCalendar.time)
                it.visibility = if (showHeader) View.VISIBLE else View.GONE
            }
        }

        private fun displayImage(mix: Mix) {
            val requestCreator = Picasso.with(itemView.context).load(mix.image)
            if (needResize()) {
                requestCreator.resizeDimen(R.dimen.image_width, R.dimen.image_height)
            }
            requestCreator.into(mPostImage)
        }

        private fun displayTextInfo(mix: Mix) {
            mPostTitle.text = mix.title
            mPostLabel.text = mix.label
            mNowPlayingImage.visibility = if (mix == mCurrentMix) View.VISIBLE else View.GONE
            mCalendar.timeInMillis = mix.published
        }

        fun onViewRecycled() {
            // TODO fix
            mFileSize?.text = null
            mProgressPercent?.text = null
            mProgressBar?.progress = 0
            mMixDownLoader.removeListener(mMix?.mixId)// TODO
            picasso.cancelRequest(mPostImage)
        }
    }

    override fun getItemId(position: Int): Long {
        return mAllMix[position].mixId
    }

    fun updatePlayingMix(mix: Mix?) {
        mCurrentMix = mix
        // TODO fix it
//        notifyDataSetChanged()
    }
}
