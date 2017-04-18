package app.anish.com.tapp;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.camera2.CameraDevice;
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

    private final SurfaceHolder mHolder;

    // this will be used to deliver camera frames to the class that initializes CameraPreview
    private final PreviewCallback mPreviewCallback;

    // using older camera API because I want to support older devices
    private final Camera mCamera;


    public CameraPreview(Context context, Camera camera, PreviewCallback previewCallback) {
        super(context);

        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mCamera = camera;
        mPreviewCallback = previewCallback;
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initCamera(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            // preview surface doesn't exist
            return;
        }

        //stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.d(TAG, "Tried stopping non-existent preview");
        }

        initCamera(mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        // take care of releasing camera preview in activity
    }

    private void initCamera(SurfaceHolder holder) {
        try {
            mCamera.setDisplayOrientation(CAMERA_ANGLE);
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
