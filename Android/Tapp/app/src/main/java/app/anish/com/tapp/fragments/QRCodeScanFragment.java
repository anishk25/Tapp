package app.anish.com.tapp.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import app.anish.com.tapp.camera.CameraPreview;
import app.anish.com.tapp.R;
import app.anish.com.tapp.camera.CameraScanProcessor;

/**
 * Created by akhattar on 4/11/17.
 */

@SuppressWarnings("deprecation")
public class QRCodeScanFragment extends Fragment {

    private static final String TAG = QRCodeScanFragment.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_FOR_CAMERA = 1;

    private final PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] bytes, Camera camera) {
            cameraScanProcessor.processCameraDataAndOpenContactsDialog(bytes);
        }
    };

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private Button reqCamPermButton;
    private CameraScanProcessor cameraScanProcessor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_scan, container, false);
        initReqPermButton(rootView);
        checkForPermissionAndInitCamera();
        initCameraUI(rootView);
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FOR_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera Permission granted
                    initializeCamera();
                } else {
                    reqCamPermButton.setVisibility(View.VISIBLE);
                    reqCamPermButton.setEnabled(true);
                }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mCamera != null) {
            if (isVisibleToUser) {
                mCamera.startPreview();
            } else {
                mCamera.stopPreview();
            }
        }
    }

    private void initializeCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            cameraScanProcessor = new CameraScanProcessor(mCamera, getContext());
            mCameraPreview = new CameraPreview(getContext(), mCamera, previewCallback);
            reqCamPermButton.setVisibility(View.GONE);
            reqCamPermButton.setEnabled(false);
        }
    }

    private void initReqPermButton(View rootView) {
        reqCamPermButton = (Button) rootView.findViewById(R.id.bReqCameraPerm);
        reqCamPermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForPermissionAndInitCamera();
            }
        });

    }

    private void initCameraUI(View rootView) {
        if (mCameraPreview != null) {
            FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.flCamera);
            frameLayout.addView(mCameraPreview);
        }
    }

    private static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.d(TAG,"Error getting camera instance", e);
            return null;
        }
    }

    private void checkForPermissionAndInitCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_FOR_CAMERA);
        } else {
            initializeCamera();
        }
    }
}
