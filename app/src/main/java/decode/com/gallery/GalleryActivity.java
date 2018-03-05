package decode.com.gallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

interface ICallback { void preview(String type); }

// AppCompatActivity pentru compatibilitate cu chestii vechi
// se poate si extends Activity
public class GalleryActivity extends AppCompatActivity implements ICallback {

    public static final int REQUEST_PREVIEW = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;

    // private Integer result = 0;
    private TabLayout tabs;
    private ViewPager pager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigation;
    private FloatingActionButton fab;
    private String tabTitles[] = new String[] { "Photos", "Videos" };

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
                arguments.putString("type", tabTitles[position]);
                fragment.setArguments(arguments);

                return fragment;
            }

            @Override
            public int getCount() {
                return tabTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles[position];
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
                    drawer.closeDrawers();

                    // Code to update the UI based on the item selected
                    selectMenuItem(item);

                    return true;
                }
            });

        fab = findViewById(R.id.action_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                PackageManager pm = getPackageManager();
                boolean deviceHasCameraFlag = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

                if (deviceHasCameraFlag) {
                    dispatchTakePictureIntent();
                } else {
                    toast("No camera available");
                }
            }
        });
    }

    public void preview(String type) {
        Intent intent = new Intent(this, PreviewActivity.class);
        // startActivity(intent);

        intent.putExtra("type", type);

        // requestCode e al meu, pun ce vreau
        startActivityForResult(intent, REQUEST_PREVIEW);
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

        switch (requestCode) {
            case REQUEST_PREVIEW:
                pager.setCurrentItem(resultCode - 1);
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    showImage(imageBitmap);
                } else {
                    toast("No Photo Taken");
                }
                break;
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
        // this works both from drawer menu & toolbar menu because we have same ids on buttons
        navigation.setCheckedItem(item.getItemId());
        // item.setChecked(true);

        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.action_photo:
                pager.setCurrentItem(0);
                Log.i("ITEM", "Photos");
                break;
            case R.id.action_video:
                pager.setCurrentItem(1);
                Log.i("ITEM", "Videos");
                break;
            case R.id.action_settings:
                Log.i("ITEM", "Settings");
                break;
            case R.id.action_about:
                Log.i("ITEM", "About");
                break;
        }
    }

    private void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void showImage(Bitmap imageBitmap) {
        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(this);
        // ImageDialog.setTitle("Captured photo preview");
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(imageBitmap);
        ImageDialog.setView(imageView);

        ImageDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        ImageDialog.show();
    }
}
