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
    private static final int PERMISSIONS_REQUEST_FOR_CAMERA = 1;

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
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.qr_code_scan, container, false);
        initReqPermButton();
        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_FOR_CAMERA) {
            checkCameraPermResult(grantResults);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_FOR_CAMERA);
            } else {
                startCameraPreview();
            }
        } else {
            if (mCamera != null) {
                mCamera.stopPreview();
            }
        }
    }

    private void checkCameraPermResult(int [] permGrantResults) {
        if (!(permGrantResults.length > 0 && permGrantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            reqCamPermButton.setVisibility(View.VISIBLE);
            reqCamPermButton.setEnabled(true);
        } else {
            startCameraPreview();
        }
    }

    private void initializeCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            cameraScanProcessor = new CameraScanProcessor(mCamera, getContext());
            mCameraPreview = new CameraPreview(getContext(), mCamera, previewCallback);
            reqCamPermButton.setVisibility(View.GONE);
            reqCamPermButton.setEnabled(false);
            FrameLayout frameLayout = (FrameLayout) rootView.findViewById(R.id.flCamera);
            frameLayout.addView(mCameraPreview);
        }
    }

    private void startCameraPreview() {
        if (mCamera == null) {
            initializeCamera();
            mCamera.startPreview();
        } else {
            mCamera.startPreview();
        }
    }

    private void initReqPermButton() {
        reqCamPermButton = (Button) rootView.findViewById(R.id.bReqCameraPerm);
        reqCamPermButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_FOR_CAMERA);
                }
            }
        });

    }

    private static Camera getCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.d(TAG,"Error getting camera instance", e);
            return null;
        }
    }
}
