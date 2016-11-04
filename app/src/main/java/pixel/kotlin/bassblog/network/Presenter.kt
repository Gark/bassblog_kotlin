package pixel.kotlin.bassblog.network


import android.content.Context
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

class Presenter(val context: Context, val callback: MixCallback) {

    interface MixCallback {
        fun onDataChanged(list: RealmResults<Mix>?)
    }

    private val mMixResults = Realm.getDefaultInstance().where(Mix::class.java).findAllSortedAsync("published", Sort.DESCENDING)
    private var firstRun = true

    fun onStop() {
        mMixResults.removeChangeListeners()
    }

    fun onStart() {
        mMixResults.addChangeListener { handleChanges() }
    }

    private fun handleChanges() {
        loadMixesOnFirstLaunch()
        callback.onDataChanged(mMixResults)
    }

    fun loadMixesOnFirstLaunch() {
        if (firstRun) {
            firstRun = false
            if (mMixResults.isEmpty()) {
                NetworkService.start(context)
            } else {
                // load from published to now.
                // TODO instead of 100 use nextPageToken
                NetworkService.start(context, mMixResults.first().published, 100)
            }
        }
    }

    fun loadMoreIfNeed(lastVisible: Int, totalCount: Int) {
        if (totalCount - lastVisible <= 3) {
            Toast.makeText(context, "bottom", Toast.LENGTH_SHORT).show()
        }
    }
}
