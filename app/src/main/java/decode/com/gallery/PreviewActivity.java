package decode.com.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class PreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Preview");
        setContentView(R.layout.activity_preview);

        Integer comingFromType = getIntent().getExtras().getInt("color");
        SquareRelativeLayout wrapper = findViewById(R.id.item_wrapper);

        wrapper.setBackgroundColor(comingFromType);
    }
}
