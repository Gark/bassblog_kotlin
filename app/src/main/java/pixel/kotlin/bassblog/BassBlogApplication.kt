package pixel.kotlin.bassblog

import android.app.Application
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.realm.Realm
import io.realm.RealmConfiguration


class BassBlogApplication : Application() {

    private var mTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        mTracker = getDefaultTracker()
        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Application start")
                .setAction("Application start")
                .build())


//        // TODO clear data on conflict.
//        val realmConfiguration = RealmConfiguration.Builder()
//                .name(Realm.DEFAULT_REALM_NAME)
//                .deleteRealmIfMigrationNeeded()
//                .build()
//        Realm.getInstance(realmConfiguration)
    }

    fun getDefaultTracker(): Tracker? {
        mTracker.let {
            val analytics = GoogleAnalytics.getInstance(this)
            mTracker = analytics.newTracker(getString(R.string.google_analytics))
        }
        return mTracker
    }
}
