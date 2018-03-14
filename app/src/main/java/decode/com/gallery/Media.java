package decode.com.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lucian.cioroga on 3/7/2018.
 */

// aici doar am scris "implements Parcelable"
// si am dat sa implementeze el tot ce trebuie
public class Media implements Parcelable {

    public static final String TYPE_IMAGE = "photos";
    public static final String TYPE_VIDEO = "videos";

    private String mName;
    private String mType;
    private String mUrl;

    public Media(String type, String name, String url) {
        mType = type;
        mName = name;
        mUrl = url;
    }

    protected Media(Parcel in) {
        mName = in.readString();
        mType = in.readString();
        mUrl = in.readString();
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }

    public String getUrl() {
        return mUrl;
    }

    public static List<Media> getMedia(Activity activity, String type) {
        int mediaType;

        // Strings are compared with .equals()
        if (type.equals(TYPE_VIDEO)) {
            mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        } else {
            mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        }

        return getMediaList(activity.getApplicationContext(), mediaType);
    }

    public static List<Media> getMediaList(Context context, int mediaType) {

        Uri queryUri = MediaStore.Files.getContentUri("external");

        String[] queryProjection = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Video.Media.DURATION
        };

        String querySelection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + mediaType;

        CursorLoader cursorLoader = new CursorLoader(context, queryUri, queryProjection, querySelection, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        List<Media> media = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                // for videos, we need the duration, for images the title
                String name = (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) ? U.format(cursor.getLong(5)) : cursor.getString(4);
                media.add(
                    new Media(
                        TYPE_IMAGE,
                        name,
                        cursor.getString(1)
                    )
                );
            } while (cursor.moveToNext());
        }

        cursor.close();

        return media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mType);
        parcel.writeString(mUrl);
    }
}
