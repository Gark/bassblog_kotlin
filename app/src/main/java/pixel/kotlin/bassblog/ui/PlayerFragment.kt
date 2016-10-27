package pixel.kotlin.bassblog.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.play_music_fragmnet.*

import pixel.kotlin.bassblog.R

class PlayerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.play_music_fragmnet, container)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        top_panel.setOnClickListener { handleClick() }
        button_play_toggle.setOnClickListener { toggleClick() }
    }

    private fun toggleClick() {
        Toast.makeText(activity, "toast", Toast.LENGTH_SHORT).show()
    }

    private fun handleClick() {
        (activity as MainActivity).toggle()
    }

    public fun setPanelVisibility(visibility: Int) {
        top_panel.visibility = visibility
    }
}
