package pixel.kotlin.bassblog

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import pixel.kotlin.bassblog.network.GcmUpdateService


class BassBlogApplication : Application() {

    private var mTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(applicationContext)
        mTracker = getDefaultTracker()
        mTracker?.send(HitBuilders.EventBuilder()
                .setCategory("Application start")
                .setAction("Application start")
                .build())

        GcmUpdateService.start(applicationContext)

//        FirebaseCrash.report(Exception("My first Android non-fatal error"))

//        iniCanary()
//        // TODO clear data on conflict.
//        val realmConfiguration = RealmConfiguration.Builder()
//                .name(Realm.DEFAULT_REALM_NAME)
//                .deleteRealmIfMigrationNeeded()
//                .build()
//        Realm.getInstance(realmConfiguration)
    }

//    fun iniCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//        LeakCanary.install(this)
//    }

    fun getDefaultTracker(): Tracker? {
        mTracker.let {
            val analytics = GoogleAnalytics.getInstance(this)
            mTracker = analytics.newTracker(getString(R.string.google_analytics))
        }
        return mTracker
    }
}
