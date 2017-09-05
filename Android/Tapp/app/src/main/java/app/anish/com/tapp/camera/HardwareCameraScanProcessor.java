package app.anish.com.tapp.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONObject;

/**
 * Interface for processing Camera Data through
 * {@link android.hardware.Camera}. The usage of
 * the deprecated Camera class makes the processor
 * backward compatible with older versions of Android
 * @author akhattar
 */

@SuppressWarnings("deprecation")
public class HardwareCameraScanProcessor extends CameraScanProcessor {

    private static final int SCAN_FAIL_CODE = 0;

    private final Camera mCamera;
    private final ImageScanner imageScanner;
    private ScanState scanState;




    public HardwareCameraScanProcessor (Camera camera, Context context) {
        super(context);
        this.mCamera = camera;
        this.imageScanner = new ImageScanner();
        this.scanState = ScanState.SCANNING;
    }

    @Override
    public void processCameraDataAndOpenContactsDialog(byte[] bytes) {
        if (scanState == ScanState.SCANNING) {
            Camera.Parameters parameters = mCamera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image cameraImage = new Image(size.width, size.height, "Y800");
            cameraImage.setData(bytes);

            int result = imageScanner.scanImage(cameraImage);

            if (result != SCAN_FAIL_CODE) {
                mCamera.stopPreview();
                String qrResult = getQRScanResults();
                scanState = ScanState.SCANNED;
                showAddContactDialog(qrResult);
            }
        }
    }

    private String getQRScanResults() {
        SymbolSet symbolSet = imageScanner.getResults();
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbolSet) {
            sb.append(symbol.getData());
        }
        return sb.toString();
    }


    enum ScanState {
        SCANNED,
        SCANNING
    }
}
