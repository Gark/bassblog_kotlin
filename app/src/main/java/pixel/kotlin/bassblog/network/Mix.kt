package pixel.kotlin.bassblog.network

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

// TODO covert to pojo
open class Mix : RealmObject() {
    @PrimaryKey
    var mixId: Long = 0L
    var title: String? = null
    var image: String? = null
    var label: String? = null
    var track: String? = null
    var published = 0L
    var favourite: Boolean = false
}
