package decode.com.gallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PreviewActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton1;
    private Button mButton2;
    private Button mButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Preview");
        setContentView(R.layout.activity_preview);

        mButton1 = (Button) findViewById(R.id.button_back1);
        mButton1.setOnClickListener(this);

        mButton2 = (Button) findViewById(R.id.button_back2);
        mButton2.setOnClickListener(this);

        mButton3 = (Button) findViewById(R.id.button_back3);
        mButton3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_back1) {
            setResult(1);
        }
        if (view.getId() == R.id.button_back2) {
            setResult(2);
        }
        if (view.getId() == R.id.button_back3) {
            setResult(3);
        }
        finish();
    }
}
