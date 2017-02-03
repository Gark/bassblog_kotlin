package pixel.kotlin.bassblog.download;

interface ProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
