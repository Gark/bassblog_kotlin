package pixel.kotlin.bassblog


import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.devbrackets.android.recyclerext.adapter.RecyclerCursorAdapter

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
        private val text : TextView

        init {
            text = itemView.findViewById(R.id.text_item) as TextView
        }

        fun displayData(cursor: Cursor) {
            text.text = cursor.position.toString();
        }
    }

}
