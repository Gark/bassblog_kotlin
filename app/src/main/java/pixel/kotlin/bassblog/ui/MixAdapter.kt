package pixel.kotlin.bassblog.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.RealmResults

import java.util.ArrayList

import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.network.Mix


internal class MixAdapter constructor(context: Context, val callback: MixSelectCallback) : RecyclerView.Adapter<MixAdapter.MixHolder>() {

    interface MixSelectCallback {
        fun onMixSelected(mix: Mix?)
    }

    init {
        setHasStableIds(true)
    }

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private val mixList = ArrayList<Mix>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MixHolder(mInflater.inflate(R.layout.item_post_list, parent, false))

    override fun onBindViewHolder(holder: MixHolder, position: Int) = holder.displayData(mixList[position])

    override fun getItemCount(): Int = mixList.size

    internal inner class MixHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mPostTitle = itemView.findViewById(R.id.post_title) as TextView
        private val mPostLabel = itemView.findViewById(R.id.post_label) as TextView
        private val mPostImage = itemView.findViewById(R.id.post_image) as ImageView
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
            Picasso.with(itemView.context).load(mix.image).into(mPostImage)
        }
    }

    fun updateMixList(result: RealmResults<Mix>?) {
        val list = result?.toList()
        list?.let {
            mixList.clear()
            mixList.addAll(it)
        }
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return mixList[position].mixId
    }
}
