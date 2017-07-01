package app.anish.com.tapp.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

/**
 * Class for processing Camera Data
 * Created by anish_khattar25 on 4/19/17.
 */

@SuppressWarnings("deprecation")
public class CameraScanProcessor {
    
    private static final int SCAN_FAIL_CODE = 0;

    private Camera mCamera;
    private Context mContext;
    private ImageScanner imageScanner;
    private ScanState scanState;


    public CameraScanProcessor(Camera camera, Context context) {
        this.mCamera = camera;
        this.mContext = context;
        imageScanner = new ImageScanner();
        scanState = ScanState.SCANNING;
    }

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
                showDialog(qrResult);
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

    private void showDialog(String qrResults) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("QR CODE Result:" + qrResults)
                .setPositiveButton("Scan Again?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanState = ScanState.SCANNING;
                        mCamera.startPreview();
                    }
                });
        builder.create().show();
    }

    enum ScanState {
        SCANNED,
        SCANNING
    }
}
