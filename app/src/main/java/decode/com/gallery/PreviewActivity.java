package decode.com.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PreviewActivity extends AppCompatActivity {

    private Picasso thumbPhoto;
    private Media media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Preview");
        setContentView(R.layout.activity_preview);

        media = getIntent().getExtras().getParcelable("media");
        ImageView imgView = findViewById(R.id.thumb_image);

        thumbPhoto = new Picasso.Builder(getApplicationContext()).build();
        thumbPhoto.load("file://" + media.getUrl()).fit().centerCrop().into(imgView, new Callback() {
            @Override
            public void onSuccess() {
                // start now the transition
                startPostponedEnterTransition();
            }

            @Override
            public void onError() {

            }
        });

        // thumbVideo = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();

        // don't start until I say so
        supportPostponeEnterTransition();

    }

    // add the media data to our result, when we press the Back button
    @Override
    public void finish() {
        Intent result = new Intent();
        result.putExtra("media", media);
        setResult(RESULT_OK, result);
        super.finish();
    }

}
