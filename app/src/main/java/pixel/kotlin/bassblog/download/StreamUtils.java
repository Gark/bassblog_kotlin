package pixel.kotlin.bassblog.download;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class StreamUtils {

    private StreamUtils() {
    }

    public static void copy(InputStream stream, File destination) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(destination));
        final byte[] buf = new byte[32 * 1024];
        int len;
        while ((len = stream.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        stream.close();
        out.close();
    }
}

