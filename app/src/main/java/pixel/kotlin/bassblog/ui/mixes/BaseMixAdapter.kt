package pixel.kotlin.bassblog.ui.mixes

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix

abstract class BaseMixAdapter(context: Context, val callback: MixSelectCallback) : RecyclerView.Adapter<BaseMixAdapter.MixHolder>() {

    interface MixSelectCallback {
        fun onMixSelected(mix: Mix?)

        fun onDataUpdated(showEmptyView: Boolean)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mAllMix: RealmResults<Mix>
    private val picasso: Picasso

    private var mCurrentMix: Mix? = null

    init {
        setHasStableIds(true)
        picasso = Picasso.with(context)
        mAllMix = getRealmMixes()
        mAllMix.addChangeListener { handleChanges() }
    }

    abstract fun getRealmMixes(): RealmResults<Mix>

    abstract fun getLayout(): Int

    abstract fun needResize(): Boolean

    fun onFragmentDestroyed() {
        mAllMix.removeChangeListeners()
    }

    private fun handleChanges() {
        callback.onDataUpdated(mAllMix.isEmpty())
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: MixHolder?) {
        holder?.onViewRecycled()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MixHolder(mInflater.inflate(getLayout(), parent, false))

    override fun onBindViewHolder(holder: MixHolder, position: Int) = holder.displayData(mAllMix[position])

    override fun getItemCount(): Int = mAllMix.size

    inner class MixHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mPostTitle = itemView.findViewById(R.id.post_title) as TextView
        private val mPostLabel = itemView.findViewById(R.id.post_label) as TextView
        private val mPostImage = itemView.findViewById(R.id.mix_image) as ImageView
        private val mNowPlayingImage = itemView.findViewById(R.id.now_playing) as ImageView
        private var mMix: Mix? = null

        init {
            itemView.setOnClickListener { handleClick() }
        }

        private fun handleClick() {
            callback.onMixSelected(mMix)
        }

        fun displayData(mix: Mix) {
            mMix = mix
            mPostTitle.text = mix.title
            mPostLabel.text = mix.label
            mNowPlayingImage.visibility = if (mix == mCurrentMix) View.VISIBLE else View.INVISIBLE

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
