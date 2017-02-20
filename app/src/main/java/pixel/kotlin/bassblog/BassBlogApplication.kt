package pixel.kotlin.bassblog

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import io.realm.RealmConfiguration
import pixel.kotlin.bassblog.download.MixDownloader
import pixel.kotlin.bassblog.network.GcmUpdateService
import android.os.StrictMode


class BassBlogApplication : Application() {

    private var mMixDownloader: MixDownloader? = null
    private var mTracker: Tracker? = null

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
        Realm.init(applicationContext)
        mTracker = getDefaultTracker()
//        mTracker?.send(HitBuilders.EventBuilder().setCategory("Application start").setAction("Application start").build())

        GcmUpdateService.start(applicationContext)

        // TODO migration.
        val config2 = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        val realm = Realm.getInstance(config2)
        realm.close()

        mMixDownloader = MixDownloader(applicationContext)
//        enableStrictMode()
    }

    fun getMixDownloader(): MixDownloader = mMixDownloader!!

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

    fun fireEventPlay(mixName: String) {
        val event = HitBuilders.EventBuilder().setAction("Selected mix").setLabel(mixName).build()
        getDefaultTracker()?.send(event)
    }

    fun fireEventFavourite(mixName: String) {
        val event = HitBuilders.EventBuilder().setAction("Favourite mix").setLabel(mixName).build()
        getDefaultTracker()?.send(event)
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
//                .penaltyDeath()
                .build())
    }
}


