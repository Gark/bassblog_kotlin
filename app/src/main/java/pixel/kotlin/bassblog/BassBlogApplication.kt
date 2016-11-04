package pixel.kotlin.bassblog

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration


class BassBlogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)

        // TODO clear data on conflict.
        val realmConfiguration = RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.getInstance(realmConfiguration)
    }
}
