package pixel.kotlin.bassblog.ui


import android.os.Bundle
import pixel.kotlin.bassblog.R

class MusicPlayerActivity : PlaybackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_music)
    }

}
