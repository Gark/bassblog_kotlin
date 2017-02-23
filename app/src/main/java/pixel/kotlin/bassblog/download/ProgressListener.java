package pixel.kotlin.bassblog.download;

public interface ProgressListener {
    void update(long mixId, int progress, int readMb, int totalMb, long state);
}
