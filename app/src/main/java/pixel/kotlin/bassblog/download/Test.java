package pixel.kotlin.bassblog.download;

import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Test {

    public static void main(String[] arg) throws IOException {

//        Thread thread = new Thread() {
//
//            @Override
//            public void run() {
//                super.run();
//                try {
        dd();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();
    }

    private static void dd() throws IOException {
        CharSequence uri = "http://mixes.bassblog.pro/DJ_B-12_-_Bass_Blog_Guest_MIx_-_February_2017.mp3";
        URL url = new URL(uri.toString());
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(true);
        httpURLConnection.setRequestProperty("User-Agent", "Android");
        httpURLConnection.connect();


        int responseCode = httpURLConnection.getResponseCode();
//        Log.i("Response Code", String.valueOf(responseCode));
        System.out.print("Response Code" + String.valueOf(responseCode));
        String header = httpURLConnection.getHeaderField("Location");
//        Log.i("Final URL", header);
        System.out.print("Final URL" + header);
    }

}
