package pixel.kotlin.bassblog.presenter


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.os.ResultReceiver
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import pixel.kotlin.bassblog.network.Mix
import pixel.kotlin.bassblog.network.NetworkService

class Presenter(val context: Context) {

    private val mReceiver = NetworkResultReceiver()
    private val mMixResults: RealmResults<Mix>
    private var firstRun = true
    private var mLoadingState = NetworkService.IDLE

    init {
        mMixResults = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
        mMixResults.addChangeListener { handleChanges() }
    }

    fun onStop() {
        mMixResults.removeChangeListeners()
    }

    private fun handleChanges() {
        loadMixesOnFirstLaunch()
    }

    fun loadMixesOnFirstLaunch() {
        if (firstRun) {
            firstRun = false
            if (mMixResults.isEmpty()) {
                NetworkService.start(context, mReceiver)
            } else {
                NetworkService.start(context, mReceiver, mMixResults.first().published, null, 200)
            }
        }
    }

    fun loadMoreIfNeed(lastVisible: Int, totalCount: Int) {
        if (totalCount - lastVisible <= 5 && mLoadingState == NetworkService.IDLE) {
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
