package pixel.kotlin.bassblog.ui.mixes

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import pixel.kotlin.bassblog.BassBlogApplication
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.download.DownloadingState
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
        private var mProgressListener: ItemProgressListener

        init {
            mProgressListener = ItemProgressListener()
            itemView.setOnClickListener { handleClick() }
            mCancel?.setOnClickListener { view -> handleCancel() }
        }

        private fun handleCancel() {
            mMix?.let {
                val entity = mMixDownLoader.getDownloadingEntity(it.mixId)
                entity?.cancelDownloading()
            }
        }

        private fun handleClick() {
            callback.onMixSelected(mMix)
        }

        fun displayData(mix: Mix, showHeader: Boolean) {
            mMix = mix
            mMixDownLoader.addProgressListener(mProgressListener, mix.mixId) // TODO
            handleDownloadState(mix.mixId)
            displayTextInfo(mix)
            displayHeader(showHeader)
            displayImage(mix)
        }

        private fun handleDownloadState(mixId: Long) {
            val entity = mMixDownLoader.getDownloadingEntity(mixId)
            if (entity == null) {
                mDownloadIcon?.setColorFilter(Color.LTGRAY)
                mFileSize?.text = null
                mProgressBar?.progress = 0
                mProgressPercent?.text = null
            } else {
                fillProgressWithData(mixId, 100 * entity.getReadSize() / entity.getTotalSize(),
                        entity.getReadSize(), entity.getTotalSize(), entity.getState())
            }
        }

        private inner class ItemProgressListener : ProgressListener {
            override fun update(mixId: Long, progress: Int, readMb: Int, totalMb: Int, state: Long) {
                fillProgressWithData(mixId, progress, readMb, totalMb, state)
            }
        }

        private fun fillProgressWithData(mixId: Long, progress: Int, readMb: Int, totalMb: Int, state: Long) {
            mFileSize?.text = String.format("%d Mb", totalMb)
            mProgressPercent?.text = String.format("%d %%", progress)
            mProgressBar?.progress = progress

            when (state) {
                DownloadingState.DOWNLOADED -> mDownloadIcon?.setColorFilter(Color.RED)
                DownloadingState.NOT_DOWNLOADED -> mDownloadIcon?.setColorFilter(Color.LTGRAY)
                DownloadingState.IN_PROGRESS -> mDownloadIcon?.setColorFilter(Color.CYAN)
                DownloadingState.PENDING -> mDownloadIcon?.setColorFilter(Color.YELLOW)

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
