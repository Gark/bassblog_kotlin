package pixel.kotlin.bassblog.network


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.os.ResultReceiver
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class Presenter(val context: Context, val callback: MixCallback) {

    interface MixCallback {
        fun onDataChanged(list: RealmResults<Mix>?)
    }

    private val mReceiver = NetworkResultReceiver()
    private val mMixResults: RealmResults<Mix>
    private var firstRun = true
    private var mLoadingState = NetworkService.IDLE

    init {
        mMixResults = Realm.getDefaultInstance().where(Mix::class.java).equalTo("favourite", true).findAllSortedAsync("published", Sort.DESCENDING)
        mMixResults.addChangeListener { handleChanges() }
    }


    fun onStop() {
        mMixResults.removeChangeListeners()
    }

    fun onStart() {
        callback.onDataChanged(mMixResults)
    }

    private fun handleChanges() {
        loadMixesOnFirstLaunch()
        callback.onDataChanged(mMixResults)
    }

    fun loadMixesOnFirstLaunch() {
        if (firstRun) {
            firstRun = false
            if (mMixResults.isEmpty()) {
                NetworkService.start(context, mReceiver)
            } else {
                // load from published to now.
                NetworkService.start(context, mReceiver, mMixResults.first().published, null, 200)
            }
        }
    }

    fun loadMoreIfNeed(lastVisible: Int, totalCount: Int) {
        if (totalCount - lastVisible <= 3 && mLoadingState == NetworkService.IDLE) {
            Toast.makeText(context, "" + mMixResults.size, Toast.LENGTH_SHORT).show()
            mLoadingState = NetworkService.LOADING
            NetworkService.start(context, mReceiver, null, mMixResults.last().published)
        }
    }

    inner class NetworkResultReceiver : ResultReceiver(Handler()) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            mLoadingState = NetworkService.IDLE
        }
    }
}
