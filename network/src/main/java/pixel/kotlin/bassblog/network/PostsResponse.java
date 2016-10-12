package pixel.kotlin.bassblog.network;


import java.util.List;

public class PostsResponse {
    private String nextPageToken;
    private List<RawPost> items;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public List<RawPost> getItems() {
        return items;
    }

    public class RawPost {
        public String id;
        public String title;
        public String content;
        public String published;
        public List<String> labels;
        public List<RawImages> images;

    }

    public class RawImages {
        public String url;
    }
}


