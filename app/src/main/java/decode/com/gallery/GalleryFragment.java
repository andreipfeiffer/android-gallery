package decode.com.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by andreipfeiffer on 2/28/18.
 */

public class GalleryFragment extends Fragment implements View.OnClickListener {

    private Button previewButton;
    private Integer type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        type = getArguments() != null ? getArguments().getInt("type") : 0;

        previewButton = (Button) root.findViewById(R.id.button_preview);
        previewButton.setOnClickListener(this);
        previewButton.setText("Preview " + type);

        return root;
    }

    @Override
    public void onClick(View view) {
        if (getActivity() instanceof ICallback && !getActivity().isDestroyed() && !getActivity().isFinishing()) {
            // tre sa cast-uim la interfata
            ((ICallback) getActivity()).preview(type);
        }
    }
}
