//package pixel.kotlin.bassblog.ui.mixes.allmixes
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import com.squareup.picasso.Picasso
//import io.realm.Realm
//import io.realm.RealmResults
//import io.realm.Sort
//
//import java.util.ArrayList
//
//import pixel.kotlin.bassblog.R
//import pixel.kotlin.bassblog.network.Mix
//
//
//internal class AllMixAdapter(context: Context, val callback: MixSelectCallback) : RecyclerView.Adapter<AllMixAdapter.MixHolder>() {
//
//    interface MixSelectCallback {
//        fun onMixSelected(mix: Mix?)
//    }
//
//    private val mInflater: LayoutInflater = LayoutInflater.from(context)
//    private val mAllMix: RealmResults<Mix>
//    private val picasso: Picasso
//
//    init {
//        setHasStableIds(true)
//        picasso = Picasso.with(context)
//        mAllMix = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
//        mAllMix.addChangeListener { handleChanges() }
//    }
//
//    fun onStop() {
//        mAllMix.removeChangeListeners()
//    }
//
//    private fun handleChanges() {
//        notifyDataSetChanged()
//    }
//
//    override fun onViewRecycled(holder: MixHolder?) {
//        holder?.onViewRecycled()
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MixHolder(mInflater.inflate(R.layout.item_post_list, parent, false))
//
//    override fun onBindViewHolder(holder: MixHolder, position: Int) = holder.displayData(mAllMix[position], position)
//
//    override fun getItemCount(): Int = mAllMix.size
//
//    internal inner class MixHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val mPostTitle = itemView.findViewById(R.id.post_title) as TextView
//        private val mPostLabel = itemView.findViewById(R.id.post_label) as TextView
//        private val mPostImage = itemView.findViewById(R.id.post_image) as ImageView
//        private var mMix: Mix? = null
//
//        init {
//            itemView.setOnClickListener { handleClick() }
//        }
//
//        private fun handleClick() {
//            callback.onMixSelected(mMix)
//        }
//
//        fun displayData(mix: Mix, position: Int) {
//            mMix = mix
//            mPostTitle.text = "$position ${mix.title}"
//            mPostLabel.text = mix.label
//            Picasso.with(itemView.context).load(mix.image).into(mPostImage)
//        }
//
//        fun onViewRecycled() {
//            picasso.cancelRequest(mPostImage)
//        }
//    }
//
//    override fun getItemId(position: Int): Long {
//        return mAllMix[position].mixId
//    }
//}
