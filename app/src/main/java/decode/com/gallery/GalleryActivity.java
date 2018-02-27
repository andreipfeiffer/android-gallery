package decode.com.gallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


// AppCompatActivity pentru compatibilitate cu chestii vechi
// se poate si extends Activity
public class GalleryActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int PREVIEW_REQUEST_TYPE = 1;

    private Button mPreviewButton;
    private TextView resultText;
    private Integer result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // R = resources (tot ce e in /res/)
        setContentView(R.layout.activity_gallery);

        mPreviewButton = (Button) findViewById(R.id.button_preview);
        mPreviewButton.setOnClickListener(this);

        resultText = (TextView) findViewById(R.id.text_result);

        if (savedInstanceState != null) {
            result = savedInstanceState.getInt("result", 0);
        }

        refresh();
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        // startActivity(intent);

        // requestCode e al meu, pun ce vreau
        startActivityForResult(intent, PREVIEW_REQUEST_TYPE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("paused", "paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("resumed", "resumed");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PREVIEW_REQUEST_TYPE) {
            result = resultCode;
            refresh();
        }
    }

    private void refresh() {
        // asta e doar pentru re-utilizare
        resultText.setText("Result " + (result));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // asta se apeleaza cand aplicatia intra in background
        // putem sa stocam starea aplicatiei
        // care o recuperam din savedInstanceState, la onCreate pe activitate
        // ea e stocata la nivel de aplicatie
        // e pierde cand se inchide aplicatia
        outState.putInt("result", result);
    }
}
