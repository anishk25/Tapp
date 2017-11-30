package app.anish.com.tapp.camera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Camera;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.add_contact.AddContactListViewAdapter;
import app.anish.com.tapp.adapters.add_contact.builder.AddContactListViewFactory;
import app.anish.com.tapp.adapters.add_contact.builder.FacebookContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.LinkedInContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.PhoneContactListViewItemFactory;
import app.anish.com.tapp.adapters.ListViewItem;
import app.anish.com.tapp.database.PeopleMetDaoHolder;
import app.anish.com.tapp.database.PeopleMetEngine;
import app.anish.com.tapp.database.PersonMet;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.SharePrefKeyInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;
import app.anish.com.tapp.utils.JSONUtils;

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
    private static final String LAST_QR_SCAN_SHARED_PREF_KEY = "LastQrScan";

    private static final TappSharedPreferences sharedPrefs = TappSharedPreferences.getInstance();

    private static final AddContactListViewFactory[] factories = {
            new PhoneContactListViewItemFactory(), new FacebookContactListViewItemFactory(),
            new LinkedInContactListViewItemFactory()
    };

    private final ImageScanner imageScanner;
    private ScanState scanState;
    private final CameraPreview cameraPreview;
    protected final Context mContext;

    private String lastQRScan;

    private FloatingActionButton lastQRScanButton;
    private ProgressBar progBarCamScan;


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
                progBarCamScan.setVisibility(View.VISIBLE);
                processScanSuccess();
                progBarCamScan.setVisibility(View.GONE);
            }
        }
    }

    public void setLastQRScanButton(FloatingActionButton lastQRScanButton) {
        this.lastQRScanButton = lastQRScanButton;
        if (lastQRScan == null) {
            lastQRScan = sharedPrefs.getString(LAST_QR_SCAN_SHARED_PREF_KEY);
            if (lastQRScan != null) {
                this.lastQRScanButton.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setProgBarCamScan(ProgressBar progBarCamScan) {
        this.progBarCamScan = progBarCamScan;
    }

    public void showLastQRScanDialog() {
        if (scanState == ScanState.SCANNING) {
           showAddContactDialog();
        }
    }

    private void processScanSuccess() {
        lastQRScan = getQRScanResults();
        sharedPrefs.saveString(LAST_QR_SCAN_SHARED_PREF_KEY, lastQRScan);
        showAddContactDialog();
        if (lastQRScanButton.getVisibility() == View.GONE) {
            lastQRScanButton.setVisibility(View.VISIBLE);
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


    private void showAddContactDialog() {
        try {
            setScanState(ScanState.SCANNED);
            JSONObject jsonObject = new JSONObject(lastQRScan);
            PeopleMetEngine.initScannedPerson(jsonObject);
            showAddContactDialog(jsonObject);
        } catch (JSONException e) {
            Toast.makeText(mContext, "Error : Malformed QR code data", Toast.LENGTH_LONG).show();
        }
    }


    private void showAddContactDialog(JSONObject jsonObject) {
        View dialogView = getAddContactDialogView(jsonObject);
        AlertDialog dialog = createAddContactDialog(dialogView, jsonObject);
        setupOnDismissListenerForDialog(dialog);
        dialog.show();
    }

    private AlertDialog createAddContactDialog(View dialogView, JSONObject qrJSON) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        String contactName = JSONUtils.getString(qrJSON, SettingsInfo.OWNER_NAME.getInfoPrefKey());
        String dialogTitle = contactName == null ? "Connect" : "Connect with " + contactName;
        dialogBuilder
                .setView(dialogView)
                .setTitle(dialogTitle)
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
                setScanState(ScanState.SCANNING);
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

    private void setScanState(ScanState scanState) {
        this.scanState = scanState;
        switch (scanState){
            case SCANNED:
                cameraPreview.stopCameraPreview();
                break;
            case SCANNING:
                cameraPreview.startCameraPreview();
                break;
        }
    }

    enum ScanState {
        SCANNED,
        SCANNING
    }
}
