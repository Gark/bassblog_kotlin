package pixel.kotlin.bassblog.download;

public interface ProgressListener {
//    void update(long mixId, long bytesRead, long contentLength, boolean done);
    void update(long mixId, int progress, int readMb, int totalMb, boolean done);
}
