package pixel.kotlin.bassblog.network;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pixel.kotlin.bassblog.storage.IoContract;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService extends IntentService {

    private static final String TAG = NetworkService.class.getName();
    private static final int MAX_RESULT = 5;
    private BassBlogApi mApi;

    public static void start(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mApi == null) {
            initNetworkModule();
        }
    }

    private void initNetworkModule() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BuildConfig.BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        mApi = retrofit.create(BassBlogApi.class);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NetworkService() {
        super(NetworkService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            handleRequest();
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.e(TAG, "network request error", e);
        }
    }

    private void handleRequest() throws IOException {
        Call<PostsResponse> call = mApi.posts(BuildConfig.BLOG_ID, false, true, null, BassBlogApi.ITEMS, BuildConfig.API_KEY, MAX_RESULT);
        Response<PostsResponse> response = call.execute();
        if (response.isSuccessful()) {
            PostsResponse body = response.body();
            List<PostsResponse.RawPost> list = body.getItems();
            applyBatch(list);
        } else {
            throw new IOException("Unsuccessful result");
        }
    }

    private void applyBatch(List<PostsResponse.RawPost> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (PostsResponse.RawPost item : list) {
            ops.add(ContentProviderOperation.newInsert(IoContract.Post.CONTENT_URI)
                    .withValue(IoContract.Post.COL_POST_ID, item.id)
                    .withValue(IoContract.Post.COL_TITLE, item.title)
                    .withValue(IoContract.Post.COL_LABEL, TextUtils.join(", ", item.labels))
                    .withValue(IoContract.Post.COL_IMAGE, getImageUrl(item))
                    .build());
        }

        try {
            ContentProviderResult[] result = getContentResolver().applyBatch(IoContract.AUTHORITY, ops);
            Log.e("NetworkService", " result " + result.length);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("NetworkService", "apply batch error", e);
        }
    }

    private String getImageUrl(PostsResponse.RawPost item) {
        String url = "";
        if (item.images != null && item.images.size() > 0) {
            url = item.images.get(0).url;
        }
        return url;
    }
}
