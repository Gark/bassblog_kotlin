package pixel.kotlin.bassblog.network

class PostsResponse {
    val nextPageToken: String? = null
    val items: List<RawPost>? = null

    inner class RawPost {
        var id: Long = 0L
        var title: String? = null
        var content: String? = null
        var published: String? = null
        var labels: List<String>? = null
        var images: List<RawImages>? = null

//        fun getImage(item: PostsResponse.RawPost): String? = item.images?.get(0)?.url
    }

    inner class RawImages {
        var url: String? = null
    }
}


