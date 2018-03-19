package decode.com.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class PreviewActivity extends AppCompatActivity {

    private Picasso thumbPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Preview");
        setContentView(R.layout.activity_preview);

        Media media = getIntent().getExtras().getParcelable("media");
        ImageView imgView = findViewById(R.id.thumb_image);

        thumbPhoto = new Picasso.Builder(getApplicationContext()).build();
        thumbPhoto.load("file://" + media.getUrl()).fit().centerCrop().into(imgView);

        //        thumbVideo = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();

//        wrapper.setBackgroundColor(media.getColor());
    }
}
