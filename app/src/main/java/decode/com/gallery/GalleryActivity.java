package decode.com.gallery;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

interface ICallback { void preview(Integer type); }

// AppCompatActivity pentru compatibilitate cu chestii vechi
// se poate si extends Activity
public class GalleryActivity extends AppCompatActivity implements ICallback {

    public static final int PREVIEW_REQUEST_TYPE = 1;

    // private Integer result = 0;
    private TabLayout tabs;
    private ViewPager pager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigation;
    private FloatingActionButton fab;

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
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "Photos" : "Videos";
            }
        });
        tabs.setupWithViewPager(pager);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        // asta adauga butonul de Home/Back
        actionbar.setDisplayHomeAsUpEnabled(true);
        // asta doar seteaza icoana
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        // change icon color
        toolbar.getNavigationIcon().setTint(Color.argb(255, 255, 255, 255));

        drawer = findViewById(R.id.drawer_layout);

        navigation = findViewById(R.id.drawer_navigation);
        navigation.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    // set item as selected to persist highlight
                    item.setChecked(true);
                    drawer.closeDrawers();

                    // Code to update the UI based on the item selected
                    selectMenuItem(item);

                    return true;
                }
            });

        fab = findViewById(R.id.action_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Floating Action Button clicked", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectMenuItem(item);
        return true;
    }

    private void selectMenuItem(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        if (item.getItemId() == R.id.action_about) {
            Log.i("ITEM", "About");
        }
        if (item.getItemId() == R.id.action_settings) {
            Log.i("ITEM", "Settings");
        }
        if (item.getItemId() == R.id.action_photo) {
            Log.i("ITEM", "Photo");
            pager.setCurrentItem(0);
        }
        if (item.getItemId() == R.id.action_video) {
            Log.i("ITEM", "Video");
            pager.setCurrentItem(1);
        }
    }
}
