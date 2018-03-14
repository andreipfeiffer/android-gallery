package decode.com.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by andreipfeiffer on 2/28/18.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_STORAGE = 3;

    private String type;
    private RecyclerView recycler_view;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager LayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        type = getArguments() != null ? getArguments().getString("type") : "";

        recycler_view = root.findViewById(R.id.recycler_view);

        checkPermission();

        return root;
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
            // tre sa cast-uim la interfata
            Log.i("CLICK", view.getTag().toString());

            ((ICallback) getActivity()).preview((Media) view.getTag());
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Media> media;
        private Picasso thumbPhoto;
        private Picasso thumbVideo;

        private Adapter(String type) {
            media = Media.getMedia(getActivity(), type);
            thumbPhoto = new Picasso.Builder(getContext()).build();
            thumbVideo = new Picasso.Builder(getContext()).addRequestHandler(new VideoRequestHandler()).build();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_media, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String url = media.get(position).getUrl();
            String title = media.get(position).getName();

            holder.label.setText(title);

            holder.wrapper.setTag(media.get(position));
            holder.wrapper.setOnClickListener(GalleryFragment.this);

            thumbPhoto.load("file://" + url).fit().centerCrop().into(holder.thumb);
            thumbVideo.load("video://" + url).fit().centerCrop().into(holder.thumb);
        }

        @Override
        public int getItemCount() {
            return media.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label;
        private ImageView thumb;
        // the type can be any super class
        private View wrapper;

        private ViewHolder(View v) {
            super(v);

            label = v.findViewById(R.id.item_label);
            wrapper = v.findViewById(R.id.item_wrapper);
            thumb = v.findViewById(R.id.thumb_image);
        }
    }

    // @todo move this on activity level
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i("PERMISSION", "Storage granted");
            loadGallery();
        } else {
            Log.i("PERMISSION", "Storage NOT granted");
        }
    }

    private void loadGallery() {
        LayoutManager = new GridLayoutManager(getContext(), this.getActivity().getResources().getInteger(R.integer.nr_columns));
        recycler_view.setLayoutManager(LayoutManager);

        adapter = new Adapter(type);
        recycler_view.setAdapter(adapter);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.i("PERMISSION", "we DON'T have Storage permission");

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.i("PERMISSION", "can request permission");
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_STORAGE);
            } else {
                Log.i("PERMISSION", "cannot request permission");
                requestPermissions(new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_STORAGE);
            }
        } else {
            Log.i("PERMISSION", "we have Storage permission");

            // this updates only the current fragment, not other fragmets
            loadGallery();
        }
    }
}
