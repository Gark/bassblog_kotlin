package pixel.kotlin.bassblog.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface for Google Blogger BassBlogApi
 */
internal interface BassBlogApi {
    //    String SEARCH_ITEMS = "items(content,images,labels,published,title),nextPageToken";

    @GET("blogger/v3/blogs/{BlogId}/posts")
    fun posts(
            @Path("BlogId") id: String,
            @Query("fetchBodies") fetchBodies: Boolean,
            @Query("fetchImages") fetchImages: Boolean,
            @Query("pageToken") pageToken: String?,
            @Query("fields") items: String,
            @Query("key") key: String,
            @Query("startDate") startDate: String?,
            @Query("endDate") endDate: String?,
            @Query("maxResults") maxResults: Int): Call<PostsResponse>

    companion object {
        //GET https://www.googleapis.com/blogger/v3/blogs/4928216501086861761/posts?fetchBodies=false&fetchImages=true&maxResults=1&
        //labels=deep&
        // fields=items(id%2Cimages%2Clabels%2Ctitle)%2CnextPageToken&key={YOUR_API_KEY}

        val ITEMS = "items(content,id,images,labels,published,title),nextPageToken"
    }


    // API
    //GET https://www.googleapis.com/blogger/v3/blogs/4928216501086861761/posts/search?q=total&fetchBodies=true&key={YOUR_API_KEY}
    //
    //    @GET("blogger/v3/blogs/{BlogId}/posts/search")
    //    Call<PostsResponse> search(
    //            @Path("BlogId") String id,
    //            @Query("q") String query,
    //            @Query("fields") String items,
    //            @Query("fetchBodies") boolean fetchBodies,
    //            @Query("key") String key);
}
