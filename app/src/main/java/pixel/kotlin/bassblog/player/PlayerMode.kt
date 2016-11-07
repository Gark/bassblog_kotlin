package pixel.kotlin.bassblog.player


import pixel.kotlin.bassblog.R

/**
 * 0 - list
 * 1 - single
 * 2 - shuffle
 */
class PlayerMode {

    companion object {
        val LIST = 0
        val SINGLE = 1
        val SHUFFLE = 2
    }

    private val ARRAY_MODE = intArrayOf(
            R.drawable.ic_play_mode_list,
            R.drawable.ic_play_mode_single,
            R.drawable.ic_play_mode_shuffle)

    private var mIndex = 0

    fun nextMode(): Int {
        return ARRAY_MODE[++mIndex % ARRAY_MODE.size]
    }

    fun getCurrentMode(): Int {
        return mIndex % ARRAY_MODE.size
    }

    fun getCurrentModeResource(): Int {
        return ARRAY_MODE[mIndex % ARRAY_MODE.size]
    }
}
