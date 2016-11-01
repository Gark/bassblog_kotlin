package pixel.kotlin.bassblog.network

internal class PostsResponse {
    val nextPageToken: String? = null
    val items: List<RawPost>? = null

    inner class RawPost {
        var id: String? = null
        var title: String? = null
        var content: String? = null
        var published: String? = null
        var labels: List<String>? = null
        var images: List<RawImages>? = null
    }
    inner class RawImages {
        var url: String? = null
    }
}


