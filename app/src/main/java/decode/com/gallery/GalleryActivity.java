package decode.com.gallery;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

interface ICallback {
    void preview(Media type, View view);
    int getVisits(Media media);
}

// AppCompatActivity pentru compatibilitate cu chestii vechi
// se poate si extends Activity
public class GalleryActivity extends AppCompatActivity implements ICallback {

    public static final int REQUEST_PREVIEW = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_STORAGE = 3;
    public static final String PREFERENCES_PREVIEW_COUNTS = "previewCounts";

    // private Integer result = 0;
    private TabLayout tabs;
    private ViewPager pager;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigation;
    private FloatingActionButton fab;
    private CoordinatorLayout coordinator;
    private String capturedPhotoPath;
    private HashMap<String, Integer> previewCounts;
    private Gson gson;
    private DB.Helper mDB;

    private String tabTitles[] = new String[]{"Photos", "Videos", "Music"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new Gson();
        mDB = new DB.Helper(this);

        // transitions
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());

        // R = resources (tot ce e in /res/)
        setContentView(R.layout.activity_gallery);


        if (savedInstanceState != null) {
            // result = savedInstanceState.getInt("result", 0);
            if (savedInstanceState.containsKey("previewCounts")) {
                previewCounts = (HashMap<String, Integer>) savedInstanceState.getSerializable("previewCounts");
            }
        } else {
            // try to get shared prefs
//            SharedPreferences prefs = getSharedPreferences(PREFERENCES_PREVIEW_COUNTS, MODE_PRIVATE);
//            previewCounts = gson.fromJson(prefs.getString("visits", ""), new TypeToken<HashMap<String, Integer>>() {
//            }.getType());

            previewCounts = new HashMap<>();
            SQLiteDatabase db = mDB.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DB.Visit.Entry.TABLE_NAME, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        previewCounts.put(
                            cursor.getString(cursor.getColumnIndex(DB.Visit.Entry.COLUMN_URL)),
                            cursor.getInt(cursor.getColumnIndex(DB.Visit.Entry.COLUMN_VISITS))
                        );
                    } while (cursor.moveToNext());
            }

            // it it's still null, we init an empty one
//            if (previewCounts == null) {
//                previewCounts = new HashMap<String, Integer>();
//            }
        }

        tabs = findViewById(R.id.tabs);
        pager = findViewById(R.id.pager);

        init();

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
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                openCamera();
            }
        });

        coordinator = findViewById(R.id.coordinator_Layout);
    }

    private void openCamera() {
        PackageManager pm = getPackageManager();
        boolean deviceHasCameraFlag = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

        if (deviceHasCameraFlag) {
            dispatchTakePictureIntent();
        } else {
            toast("No camera available");
        }
    }

    @SuppressLint("RestrictedApi")
    public void preview(Media media, View view) {
        Intent intent = new Intent(this, PreviewActivity.class);
        // startActivity(intent);

        intent.putExtra("media", media);

        // spunem ca vrem tranzitii spre activitate
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view.findViewById(R.id.thumb_image), "thumb");

        // requestCode e al meu, pun ce vreau
        startActivityForResult(intent, REQUEST_PREVIEW, options.toBundle());
    }

    @Override
    public int getVisits(Media media) {
        return previewCounts.containsKey(media.getUrl()) ? previewCounts.get(media.getUrl()) : 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("paused", "paused");

//        SharedPreferences prefs = getSharedPreferences(PREFERENCES_PREVIEW_COUNTS, Context.MODE_PRIVATE);
//        prefs.edit().putString("visits", gson.toJson(previewCounts)).commit();
//        Log.d("Preferences", "wrote " + prefs.getString("visits", ""));

        SQLiteDatabase db = mDB.getWritableDatabase();
        for (String key : previewCounts.keySet()) {
            ContentValues values = new ContentValues();
            values.put(DB.Visit.Entry.COLUMN_URL, key);
            values.put(DB.Visit.Entry.COLUMN_VISITS, previewCounts.get(key));

            if (db.update(DB.Visit.Entry.TABLE_NAME, values, DB.Visit.Entry.COLUMN_URL + "= ?", new String[]{key}) <= 0) {
                db.insert(DB.Visit.Entry.TABLE_NAME, null, values);
            }
        }
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
                if (resultCode == RESULT_OK) {
                    Media media = data.getParcelableExtra("media");
                    String url = media.getUrl();
                    int count = previewCounts.containsKey(url) ? previewCounts.get(url) : 0;
                    previewCounts.put(url, count + 1);

                    // pass result to all Fragments
                    for (Fragment f: getSupportFragmentManager().getFragments()) {
                        f.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    // when we store the image on disk, we don't receive any result data
                    // Bundle extras = data.getExtras();
                    // final Bitmap imageBitmap = (Bitmap) extras.get("data");

                    Snackbar snackbar = Snackbar.make(coordinator, "Photo was captured", Snackbar.LENGTH_LONG).setAction("VIEW", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // showImage(imageBitmap);
                            showImage(capturedPhotoPath);
                        }
                    });
                    snackbar.show();
                } else {
                    toast("No photo taken");
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

        outState.putSerializable("previewCounts", previewCounts);
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

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                toast("Error occurred while creating the File");
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "decode.com.gallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void showImage(String imagePath) {
        File imgFile = new File(imagePath);

        if (!imgFile.exists()) {
            return;
        }

        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(this);
        ImageDialog.setTitle("Captured photo preview").setIcon(R.drawable.ic_camera).setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            }
        });
        ;

        ImageView imageView = new ImageView(this);
        Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth() / 4, imageBitmap.getHeight() / 4, true);

        imageView.setImageBitmap(resizedBitmap);

        ImageDialog.setView(imageView);
        ImageDialog.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.i("PHOTO_DIR", storageDir.toString());

        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);

        // Save a file: path for use with ACTION_VIEW intents
        capturedPhotoPath = image.getAbsolutePath();
        Log.i("PHOTO_PATH", capturedPhotoPath);
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.i("PERMISSION", String.valueOf(requestCode));

        if (requestCode == REQUEST_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("PERMISSION", "Storage granted");
            init();
        } else {
            Log.i("PERMISSION", "Storage NOT granted");
        }
    }

    private void init() {
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                GalleryFragment fragment = new GalleryFragment();

                Bundle arguments = new Bundle();
                arguments.putString("type", tabTitles[position].toLowerCase());
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
    }
}
