package app.anish.com.tapp.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.add_contact.AddContactListViewAdapter;
import app.anish.com.tapp.adapters.add_contact.builder.AddContactListViewFactory;
import app.anish.com.tapp.adapters.add_contact.builder.FacebookContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.LinkedInContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.PhoneContactListViewItemFactory;
import app.anish.com.tapp.adapters.ListViewItem;

/**
 * Interface for processing Camera Data through
 * {@link android.hardware.Camera}. The usage of
 * the deprecated Camera class makes the processor
 * backward compatible with older versions of Android
 * @author akhattar
 */

@SuppressWarnings("deprecation")
public class CameraScanProcessor {

    private static final int SCAN_FAIL_CODE = 0;

    private static final AddContactListViewFactory[] factories = {
            new PhoneContactListViewItemFactory(), new FacebookContactListViewItemFactory(),
            new LinkedInContactListViewItemFactory()
    };

    private final ImageScanner imageScanner;
    private ScanState scanState;
    private final CameraPreview cameraPreview;
    protected final Context mContext;


    public CameraScanProcessor(CameraPreview cameraPreview, Context context) {
        this.mContext = context;
        this.cameraPreview = cameraPreview;
        this.imageScanner = new ImageScanner();
        this.scanState = ScanState.SCANNING;
    }


    public void processCameraDataAndOpenContactsDialog(byte[] bytes) {

        // Maintaining a scanState prevents multiple contact dialogs
        // to be displayed.
        if (scanState == ScanState.SCANNING) {
            Camera.Parameters parameters = cameraPreview.getCamera().getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image cameraImage = new Image(size.width, size.height, "Y800");
            cameraImage.setData(bytes);

            int result = imageScanner.scanImage(cameraImage);

            if (result != SCAN_FAIL_CODE) {
                scanState = ScanState.SCANNED;
                cameraPreview.stopCameraPreview();
                String qrResult = getQRScanResults();
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


    private void showAddContactDialog(String qrCodeData) {
        try {
            JSONObject jsonObject = new JSONObject(qrCodeData);
            showAddContactDialog(jsonObject);
        } catch (JSONException e) {
            Toast.makeText(mContext, "Error : Malformed QR code data", Toast.LENGTH_LONG).show();
        }
    }


    private void showAddContactDialog(JSONObject jsonObject) {
        View dialogView = getAddContactDialogView(jsonObject);
        AlertDialog alertDialog = createAddContactDialog(dialogView);
        setupOnDismissListenerForDialog(alertDialog);
        alertDialog.show();
    }

    private AlertDialog createAddContactDialog(View dialogView) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        dialogBuilder
                .setView(dialogView)
                .setTitle(R.string.add_contact_dialog_title)
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return dialogBuilder.create();
    }

    private void setupOnDismissListenerForDialog(Dialog dialog) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                cameraPreview.startCameraPreview();
                scanState = ScanState.SCANNING;
            }
        });
    }

    private View getAddContactDialogView(JSONObject jsonObject) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.add_contact_list_view, null);
        ListView listView = (ListView) view.findViewById(R.id.lvAddContact);

        ArrayList<ListViewItem> listViewItems = new ArrayList<>();
        for (AddContactListViewFactory factory : factories) {
            ListViewItem item = factory.getContactListViewItem(jsonObject);
            if (item != null) {
                listViewItems.add(item);
            }
        }

        AddContactListViewAdapter adapter = new AddContactListViewAdapter(mContext, listViewItems);
        listView.setAdapter(adapter);
        return view;
    }

    enum ScanState {
        SCANNED,
        SCANNING
    }
}
