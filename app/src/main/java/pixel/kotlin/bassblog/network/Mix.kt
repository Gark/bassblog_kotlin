package pixel.kotlin.bassblog.network

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Mix : RealmObject() {
    @PrimaryKey
    var mixId: String? = null
    var title: String? = null
    var image: String? = null
    var label: String? = null
    var track: String? = null
    var published = 0L
    var favourite: Boolean = false
}
