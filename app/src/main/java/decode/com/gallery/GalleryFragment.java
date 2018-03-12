package decode.com.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by andreipfeiffer on 2/28/18.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

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

        LayoutManager = new GridLayoutManager(getContext(), this.getActivity().getResources().getInteger(R.integer.nr_columns));
        recycler_view.setLayoutManager(LayoutManager);

        adapter = new Adapter(type);
        recycler_view.setAdapter(adapter);

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

        private Media[] media;

        private Adapter(String type) {
            media = Media.getMedia(type);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater in = LayoutInflater.from(getContext());
            View v = in.inflate(R.layout.item_media, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int color = media[position].getColor();
            holder.label.setText(media[position].getName());
            holder.wrapper.setBackgroundColor(color);
            holder.wrapper.setTag(media[position]);
            holder.wrapper.setOnClickListener(GalleryFragment.this);
        }

        @Override
        public int getItemCount() {
            return media.length;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView label;
        // the type can be any super class
        private View wrapper;

        private ViewHolder(View v) {
            super(v);

            label = v.findViewById(R.id.item_label);
            wrapper = v.findViewById(R.id.item_wrapper);
        }
    }
}
