package app.anish.com.tapp.camera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Class for previewing the Camera
 */
@SuppressWarnings("deprecation")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final int CAMERA_ANGLE = 90;
    private static final String TAG = CameraPreview.class.getName();


    private boolean mPreviewing = true;
    private boolean mSurfaceCreated = false;

    private final SurfaceHolder mHolder;

    // this will be used to deliver camera frames to the class that initializes CameraPreview
    private final PreviewCallback mPreviewCallback;

    // using older camera API because I want to support older devices
    private final Camera mCamera;

    private final Handler mAutoFocusHandler;

    private final Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean b, Camera camera) {
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(params);

            scheduleAutoFocus();

        }
    };

    private final Runnable doAutoFocus = new Runnable() {
        @Override
        public void run() {
            safeAutoFocus();
        }
    };

    public CameraPreview(Context context, Camera camera, PreviewCallback previewCallback) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        mCamera = camera;
        mPreviewCallback = previewCallback;

        mAutoFocusHandler = new Handler();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceCreated = true;
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            // preview surface doesn't exist
            return;
        }

        stopCameraPreview();
        startCameraPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // take care of releasing camera preview in activity
        mSurfaceCreated = false;
        stopCameraPreview();
    }

    public void stopCameraPreview() {
        mPreviewing = false;
        mCamera.cancelAutoFocus();
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "Tried stopping non-existent preview");
        }
    }

    public void startCameraPreview() {
        try {
            mPreviewing = true;
            mCamera.setDisplayOrientation(CAMERA_ANGLE);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();

            if (mSurfaceCreated) {
                safeAutoFocus();
            } else {
                scheduleAutoFocus();
            }

        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void safeAutoFocus() {
        if (mCamera != null && mPreviewing && mSurfaceCreated) {
            try {
                mCamera.autoFocus(autoFocusCB);
            } catch (RuntimeException e) {
                scheduleAutoFocus();
            }
        }
    }

    private void scheduleAutoFocus() {
        mAutoFocusHandler.postDelayed(doAutoFocus, 1000);
    }

    public Camera getCamera() {
        return mCamera;
    }

}
