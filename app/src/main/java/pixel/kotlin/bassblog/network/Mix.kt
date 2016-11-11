package pixel.kotlin.bassblog.network

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Mix : RealmObject() {
    @PrimaryKey
    var mixId: Long = 0L
    var title: String? = null
    var image: String? = null
    var label: String? = null
    var track: String? = null
    var published = 0L
    var favourite: Boolean = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mix) return false

        if (mixId != other.mixId) return false

        return true
    }

    override fun hashCode(): Int {
        return mixId.hashCode()
    }
}
