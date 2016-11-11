package pixel.kotlin.bassblog.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.pager_activity.*
import pixel.kotlin.bassblog.R
import pixel.kotlin.bassblog.ui.mixes.allmixes.AllMixFragment
import pixel.kotlin.bassblog.ui.mixes.favourite.FavouriteFragment
import pixel.kotlin.bassblog.ui.search.SearchFragment

class PagerActivity : CommunicationActivity(), ViewPager.OnPageChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pager_activity)

        bottom_navigation_view.setOnNavigationItemSelectedListener { item -> onSelected(item) }

        val adapter = MixPagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        pager.addOnPageChangeListener(this)
        button_play_toggle_bottom.setOnClickListener { handleToggleClick() }
        bottom_control.setOnClickListener { handleBottomPanelClick() }
    }

    private fun handleBottomPanelClick() {
//        MusicPlayerActivity.start(this, text_view_name_bottom, getString(R.string.mix_text_transition))
        MusicPlayerActivity.start(this)
    }

    private fun handleToggleClick() {
        if (mPlaybackService == null) return
        mPlaybackService!!.toggle()
    }

    private fun onSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_all -> pager.setCurrentItem(0, true)
            R.id.action_favourite -> pager.setCurrentItem(1, true)
            R.id.action_search -> pager.setCurrentItem(2, true)
        }
        return true
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        for (i in 0..2) {
            bottom_navigation_view.menu.getItem(i).isChecked = false
        }
        bottom_navigation_view.menu.getItem(position).isChecked = true
    }

    class MixPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                1 -> FavouriteFragment()
                2 -> SearchFragment()
                else -> AllMixFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }

    override fun onPlayStatusChanged(isPlaying: Boolean) {
        updatePlayToggle(isPlaying)
        updateSongData()
    }

    // TODO: need progress loading
    fun updatePlayToggle(play: Boolean) {
        button_play_toggle_bottom.setImageResource(if (play) R.drawable.ic_icon_pause_player else R.drawable.ic_icon_play_player)
    }

    fun updateSongData() {
        if (mPlaybackService == null) return

        val mix = mPlaybackService!!.getPlayingMix()
        bottom_control.visibility = if (mix == null) View.GONE else View.VISIBLE
        text_view_name_bottom.text = mix?.title

        Picasso.with(this)
                .load(mix?.image)
                .into(image_view_album_bottom)
    }
}
