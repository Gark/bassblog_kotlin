package pixel.kotlin.bassblog

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import pixel.kotlin.bassblog.network.GcmUpdateService


class BassBlogApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)

        GcmUpdateService.start(applicationContext)

        // TODO migration.
        val config2 = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        val realm = Realm.getInstance(config2)
        realm.close()


    }

//    fun iniCanary() {
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//        LeakCanary.install(this)
//    }


}


