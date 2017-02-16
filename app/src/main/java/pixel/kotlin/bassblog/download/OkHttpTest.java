package pixel.kotlin.bassblog.download;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpTest {


    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
//                .followRedirects(false)
//                .followSslRedirects(false)
                .build();

        Request request = new Request.Builder()
                .url("https://upload.wikimedia.org/wikipedia/commons/f/ff/Pizigani_1367_Chart_10MB.jpg")
                .build();

        Response response = client.newCall(request).execute();
        System.out.println(response.isRedirect());
        System.out.println(response.isSuccessful());
        System.out.println(response.code());
        System.out.println(response.headers());
        ResponseBody responseBody = response.body();
        InputStream stream = responseBody.byteStream();
        File file = new File("file");

//        FileUtils.copyInputStreamToFile(stream, file);
//        System.out.println(file.length());

    }


}
