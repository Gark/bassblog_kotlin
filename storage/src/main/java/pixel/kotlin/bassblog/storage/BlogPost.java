package pixel.kotlin.bassblog.storage;


import android.os.Parcel;
import android.os.Parcelable;

public class BlogPost implements Parcelable {

    private final String mId;
    private final String mPostId;
    private final String mTitle;
    private final String mImage;
    private final String mLabel;
    private final String mTrack;
    private final boolean mFavourite;

    public BlogPost(String id, String postId, String title, String image, String label, String track, boolean favourite) {
        this.mId = id;
        this.mPostId = postId;
        this.mTitle = title;
        this.mImage = image;
        this.mLabel = label;
        this.mTrack = track;
        this.mFavourite = favourite;
    }

    private BlogPost(Parcel in) {
        mId = in.readString();
        mPostId = in.readString();
        mTitle = in.readString();
        mImage = in.readString();
        mLabel = in.readString();
        mTrack = in.readString();
        mFavourite = in.readByte() != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlogPost blogPost = (BlogPost) o;
        return mPostId.equals(blogPost.mPostId);
    }

    @Override
    public int hashCode() {
        return mPostId.hashCode();
    }

    public static final Creator<BlogPost> CREATOR = new Creator<BlogPost>() {
        @Override
        public BlogPost createFromParcel(Parcel in) {
            return new BlogPost(in);
        }

        @Override
        public BlogPost[] newArray(int size) {
            return new BlogPost[size];
        }
    };

    public String getId() {
        return mId;
    }

    public String getPostId() {
        return mPostId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImage() {
        return mImage;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getTrack() {
        return mTrack;
    }

    public boolean isFavourite() {
        return mFavourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mTitle);
        dest.writeString(mImage);
        dest.writeString(mLabel);
        dest.writeString(mTrack);
        dest.writeByte((byte) (mFavourite ? 1 : 0));
    }


}
