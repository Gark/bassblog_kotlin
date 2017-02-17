package pixel.kotlin.bassblog.download;

public interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
