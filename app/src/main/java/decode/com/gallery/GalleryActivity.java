package decode.com.gallery;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

interface ICallback { void preview(Integer type); }

// AppCompatActivity pentru compatibilitate cu chestii vechi
// se poate si extends Activity
public class GalleryActivity extends AppCompatActivity implements ICallback {

    public static final int PREVIEW_REQUEST_TYPE = 1;

    // private Integer result = 0;
    private TabLayout tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // R = resources (tot ce e in /res/)
        setContentView(R.layout.activity_gallery);


        if (savedInstanceState != null) {
            // result = savedInstanceState.getInt("result", 0);
        }

        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GalleryFragment fragment = new GalleryFragment();

                Bundle arguments = new Bundle();
                arguments.putInt("type", position + 1);
                fragment.setArguments(arguments);

                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return "Page " + (position + 1);
            }
        });
        tabs.setupWithViewPager(pager);
    }

    public void preview(Integer type) {
        Intent intent = new Intent(this, PreviewActivity.class);
        // startActivity(intent);

        intent.putExtra("type", type);

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
            pager.setCurrentItem(resultCode - 1);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // asta se apeleaza cand aplicatia intra in background
        // putem sa stocam starea aplicatiei
        // care o recuperam din savedInstanceState, la onCreate pe activitate
        // ea e stocata la nivel de aplicatie
        // e pierde cand se inchide aplicatia

        //outState.putInt("result", result);
    }
}
