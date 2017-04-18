package app.anish.com.tapp.fragments;

import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import app.anish.com.tapp.CameraPreview;
import app.anish.com.tapp.R;

/**
 * Created by akhattar on 4/11/17.
 */

@SuppressWarnings("deprecation")
public class QRCodeScanFragment extends Fragment {


    private final PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            // do something here
        }
    };

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_scan, container, false);
        initializeCamera();
        initUI(rootView);
        return rootView;
    }

    private void initializeCamera() {
        mCamera = getCameraInstance();
        mCameraPreview = new CameraPreview(getContext(),mCamera, previewCallback);
    }

    private void initUI(View rootView) {
        FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.flCamera);
        frameLayout.addView(mCameraPreview);
    }

    private static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            return null;
        }
    }


}
