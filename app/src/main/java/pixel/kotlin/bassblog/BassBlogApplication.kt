package pixel.kotlin.bassblog

import android.app.Application
import io.realm.Realm


class BassBlogApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
    }
}
