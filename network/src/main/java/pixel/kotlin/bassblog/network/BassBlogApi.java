package pixel.kotlin.bassblog.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for Google Blogger BassBlogApi
 */
interface BassBlogApi {
    //GET https://www.googleapis.com/blogger/v3/blogs/4928216501086861761/posts?fetchBodies=false&fetchImages=true&maxResults=1&
    //labels=deep&
    // fields=items(id%2Cimages%2Clabels%2Ctitle)%2CnextPageToken&key={YOUR_API_KEY}

    String ITEMS = "items(content,id,images,labels,published,title),nextPageToken";
//    String SEARCH_ITEMS = "items(content,images,labels,published,title),nextPageToken";

    @GET("blogger/v3/blogs/{BlogId}/posts")
    Call<PostsResponse> posts(
            @Path("BlogId") String id,
            @Query("fetchBodies") boolean fetchBodies,
            @Query("fetchImages") boolean fetchImages,
            @Query("pageToken") String pageToken,
            @Query("fields") String items,
            @Query("key") String key,
            @Query("maxResults") int maxResults);


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
