package pixel.kotlin.bassblog.download;

import android.util.Log;

import java.io.IOException;


public class Test {

    public static void main(String[] arg) throws IOException {
        java.lang.CharSequence uri = "http://mixes.bassblog.pro/DJ_B-12_-_Bass_Blog_Guest_MIx_-_February_2017.mp3";
        java.net.URL url = new java.net.URL(uri.toString());
        java.net.HttpURLConnection httpURLConnection = (java.net.HttpURLConnection) url.openConnection();
        httpURLConnection.setInstanceFollowRedirects(false);
        httpURLConnection.connect();
        int responseCode = httpURLConnection.getResponseCode();
        Log.i("Response Code", String.valueOf(responseCode));
        String header = httpURLConnection.getHeaderField("Location");
        Log.i("Final URL", header);

    }

}
