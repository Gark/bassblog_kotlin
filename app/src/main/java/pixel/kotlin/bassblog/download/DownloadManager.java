package pixel.kotlin.bassblog.download;


import android.content.Context;
import android.support.annotation.IntDef;

import java.io.IOException;
import java.lang.annotation.Retention;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class DownloadManager {

    @Retention(SOURCE)
    @IntDef({NOT_DOWNLOADED, PENDING, IN_PROGRESS, DOWNLOADED})
    public @interface DownloadingState {
    }

    public static final int NOT_DOWNLOADED = 0;
    public static final int PENDING = 1;
    public static final int IN_PROGRESS = 2;
    public static final int DOWNLOADED = 3;


    private final OkHttpClient mOkHttpClient;
    private final Context mContext;

    public DownloadManager(final Context context) {
        mContext = context;
        mOkHttpClient = new OkHttpClient.Builder().build();
    }

    @DownloadManager.DownloadingState
    public int getState(final String mixId) {
        return NOT_DOWNLOADED;
    }

    public void scheduleMixDownloading(final String mixId, final String mixUrl) throws IOException {
        Request request = new Request.Builder()
                .url(mixUrl)
                .build();

        final Response response = mOkHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            responseBody.byteStream();
        }


//        try (Response response = mOkHttpClient.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw new IOException("");
//            }
//            ResponseBody responseBody = response.body();
//            try {
//                if (progressListener != null) {
//                    responseBody = new ProgressResponseBody(responseBody, progressListener);
//                }
//                StreamUtils.copy(responseBody.byteStream(), destination);
//            } finally {
//                if (responseBody != response.body()) {
//                    // We need to close our custom progress response manually, otherwise
//                    // progress handler thread will not be shut down
//                    responseBody.close();
//                }
//            }
//        }
    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final Progress.ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, Progress.ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }

}
