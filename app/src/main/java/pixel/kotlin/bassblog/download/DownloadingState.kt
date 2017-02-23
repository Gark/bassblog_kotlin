package pixel.kotlin.bassblog.download

import android.support.annotation.IntDef

class DownloadingState {
    companion object {
//        @IntDef(NOT_DOWNLOADED, IN_PROGRESS, DOWNLOADED, PENDING)
//        annotation class DownloadingState

        const val NOT_DOWNLOADED = 0L
        const val IN_PROGRESS = 1L
        const val DOWNLOADED = 2L
        const val PENDING = 3L
    }
}