package pixel.kotlin.bassblog.download

import android.content.Context
import java.io.File

class FileUtils {
    // can be like an extension function
    companion object {
        fun getFile(mixId: Long, context: Context): File = File(context.cacheDir, mixId.toString())

        fun getFileSize(mixId: Long, context: Context): String {
            val file = getFile(mixId, context)
            if (!file.exists()) {
                return ""
            }
            val sizeInMb = file.length() / (1024 * 1024)
            return String.format("%d Mb", sizeInMb)
        }

        fun deleteFileMix(mixId: Long, context: Context) {
            val file = getFile(mixId, context)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
