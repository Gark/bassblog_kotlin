package pixel.kotlin.bassblog.network

import android.content.Context
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.OneoffTask
import com.google.android.gms.gcm.TaskParams
import io.realm.Realm
import io.realm.Sort
import java.io.IOException

class GcmUpdateService : GcmTaskService() {

    companion object {
        fun start(context: Context) {
            val myTask = OneoffTask.Builder()
                    .setService(GcmUpdateService::class.java)
                    .setExecutionWindow(0, 50)
                    .setTag("mix-download")
                    .build()
            GcmNetworkManager.getInstance(context).schedule(myTask)
            println("GcmNetworkManager start")
        }
    }

    override fun onRunTask(taskParams: TaskParams): Int {
        val list = Realm.getDefaultInstance().where(Mix::class.java).findAllSorted("published", Sort.DESCENDING)
        val time = if (list.isEmpty()) System.currentTimeMillis() else list.last().published
        val helper = NetworkHelper()

        try {
            val resultCount = helper.requestMixes(400, NetworkService.DEFAULT_TIME, time)
            println("GcmNetworkManager $resultCount")
            println("GcmNetworkManager ${list.last().published}")

            if (resultCount > 0) {
                GcmUpdateService.start(applicationContext)
            }

        } catch (exp: IOException) {
            exp.printStackTrace()
            return GcmNetworkManager.RESULT_FAILURE
        }
        return GcmNetworkManager.RESULT_SUCCESS
    }
}
