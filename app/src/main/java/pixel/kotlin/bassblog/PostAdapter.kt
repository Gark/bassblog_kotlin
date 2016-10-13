package pixel.kotlin.bassblog


import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.devbrackets.android.recyclerext.adapter.RecyclerCursorAdapter
import com.squareup.picasso.Picasso

internal class PostAdapter(context: Context) : RecyclerCursorAdapter<PostAdapter.PostVieHolder>(null) {

    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: PostVieHolder, cursor: Cursor, position: Int) {
        holder.displayData(cursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVieHolder {
        val view = mInflater.inflate(R.layout.item_post_list, parent, false)
        return PostVieHolder(view)
    }

    internal inner class PostVieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mPostTitle : TextView
        private val mPostLabel : TextView
        private val mPostImage : ImageView

        init {
            mPostTitle = itemView.findViewById(R.id.post_title) as TextView
            mPostLabel = itemView.findViewById(R.id.post_label) as TextView
            mPostImage = itemView.findViewById(R.id.post_image) as ImageView
        }

        fun displayData(cursor: Cursor) {
            val post = PostUtils.getBlogPost(cursor)
            mPostTitle.text = post.title
            mPostLabel.text = post.label
            Picasso.with(itemView.context).load(post.image).into(mPostImage)
        }
    }
}
