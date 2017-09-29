package app.anish.com.tapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.anish.com.tapp.R;
import app.anish.com.tapp.activities.QRCodeSettingsActivity;
import app.anish.com.tapp.utils.ContactInfo;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.SharePrefKeyInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;
import app.anish.com.tapp.utils.FileUtils;
import app.anish.com.tapp.utils.StringUtils;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = QRCodeDisplayFragment.class.getName();
    private static final String APP_OPENED_FIRST_TIME_KEY = "appOpenFirstTime";

    private static final String QR_IMAGE_FILE_NAME = "tapp_qrcode.png";
    private static final String QR_CODE_CHAR_SET = "ISO-8859-1";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int SETTINGS_ACTIVITY_REQUEST_CODE = 2;
    private static final int QR_CODE_BITMAP_WH = 400;

    private static final TappSharedPreferences sharedPrefs = TappSharedPreferences.getInstance();

    private static final List<SettingsInfo> mandatorySettings = SettingsInfo.getAllMandatorySettings();

    // UI Elements
    private Activity mActivity;
    private ImageView qrImage;
    private String currQRData;
    private ProgressBar qrProgressBar;
    private TextView tvQrErrorMsg;

    // used for making QR image loading faster for first time
    // the QR code is displayed to the user
    private boolean firstTimeDisplay = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        populateContactInfo();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_display, container, false);
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bQRCodeSettings:
                launchSettingsActivity();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (allPermissionsGranted(grantResults)) {
                populateSharedPrefs();
                drawQRCode();
            } else {
                showPermNotGrantedToast();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTIVITY_REQUEST_CODE) {
            drawQRCode();
        }
    }



    private void initUI(View rootView) {
        qrProgressBar = (ProgressBar) rootView.findViewById(R.id.pbQrCode);
        qrImage = (ImageView) rootView.findViewById(R.id.ivQrCode);
        tvQrErrorMsg = (TextView) rootView.findViewById(R.id.tvQrErrorMsg);
        drawQRCode();
        Button settingsButton = (Button) rootView.findViewById(R.id.bQRCodeSettings);
        settingsButton.setOnClickListener(this);
    }


    private void launchSettingsActivity() {
        Intent intent = new Intent(getContext(), QRCodeSettingsActivity.class);
        startActivityForResult(intent, SETTINGS_ACTIVITY_REQUEST_CODE);
    }

    private void populateContactInfo() {
        boolean opened = sharedPrefs.getBoolean(APP_OPENED_FIRST_TIME_KEY);
        if (!opened) {
            sharedPrefs.saveBoolean(APP_OPENED_FIRST_TIME_KEY, true);
            showPermissionDialog();
        }
    }

    private void populateSharedPrefs() {
        String ownerName = ContactInfo.getOwnerName(mActivity);
        String phoneNumber = ContactInfo.getCurrentPhoneNumber(mActivity);
        String email = ContactInfo.getOwnerEmail(mActivity);

        sharedPrefs.edit();
        sharedPrefs.saveString(SettingsInfo.OWNER_NAME.getInfoPrefKey(), ownerName);
        sharedPrefs.saveString(SettingsInfo.PHONE_NUMBER.getInfoPrefKey(), phoneNumber);
        sharedPrefs.saveString(SettingsInfo.EMAIL.getInfoPrefKey(), email);
        sharedPrefs.commit();
    }



    private void showPermissionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.permissions_info_title)
                .setMessage(R.string.permission_info_body)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mActivity.checkSelfPermission(Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED
                                && mActivity.checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showPermNotGrantedToast();
                    }
                })
                .create();
        alertDialog.show();
    }



    private void showPermNotGrantedToast() {
        Toast.makeText(mActivity, "Read contacts permission not granted, contact info will not be" +
                " automatically encoded in QR code. Go to settings to enter contact info", Toast.LENGTH_LONG).show();
    }


    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void drawQRCode() {
        // do diff of current data on QR code and new data
        try {
            JSONObject updatedData = getSavedContactInfo();
            if (containsMandatorySettingsFields(updatedData)) {
                drawQRCode(updatedData);
            } else {
                displayQRErrorMsg();
            }
        } catch (Exception e) {
            Toast.makeText(mActivity, "Error drawing QR code", Toast.LENGTH_LONG).show();
        }

    }

    private void drawQRCode(JSONObject jsonObject) throws Exception {
        String dataStr = jsonObject.toString();
        if (!dataStr.equals(currQRData)) {
            tvQrErrorMsg.setVisibility(View.GONE);
            drawQRCode(dataStr);
            currQRData = dataStr;
        }
    }

    private void displayQRErrorMsg() {
        tvQrErrorMsg.setText(R.string.qr_error_text);
        tvQrErrorMsg.setVisibility(View.VISIBLE);
        qrImage.setVisibility(View.GONE);
    }



    private void drawQRCode(final String data) throws Exception {
        qrImage.setVisibility(View.GONE);
        qrProgressBar.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    final Bitmap bitmap = getQRBitMap(data);
                    qrImage.post(new Runnable() {
                        public void run() {
                            qrImage.setImageBitmap(bitmap);
                            qrImage.setVisibility(View.VISIBLE);
                            qrProgressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Log.d(LOG_TAG, "Error generating bitmap image");
                }
            }
        });
        thread.start();
    }

    private JSONObject getSavedContactInfo() throws JSONException {
        JSONObject obj1 = getSavedData(SettingsInfo.values());
        JSONObject obj2 = getSavedData(SecuredSharedPrefs.values());
        JSONObject result = mergeJSONObjects(obj1, obj2);
        Log.d(LOG_TAG, "Got QR ! \n" + result.toString());
        return result;
    }


    private JSONObject getSavedData(SharePrefKeyInfo[] keyInfoArr) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (SharePrefKeyInfo info : keyInfoArr) {
            String data = sharedPrefs.getString(info.getInfoPrefKey());
            if (data != null) {
                boolean shareable = sharedPrefs.getBoolean(info.getShareInfoPrefKey()) || info.isMandatoryShare();
                if (shareable) {
                    jsonObject.put(info.getInfoPrefKey(), data);
                }
            }
        }
        return jsonObject;
    }

    private JSONObject mergeJSONObjects(JSONObject object1, JSONObject object2) throws JSONException {
        JSONObject result = new JSONObject(object1.toString());
        Iterator<String> it = object2.keys();
        while (it.hasNext()) {
            String key = it.next();
            String value = object2.getString(key);
            result.put(key, value);
        }
        return result;
    }


    private boolean containsMandatorySettingsFields(JSONObject jsonObject) {
        for (SettingsInfo settingsInfo : mandatorySettings) {
            try {
                String val = jsonObject.getString(settingsInfo.getInfoPrefKey());
                if (StringUtils.stringIsEmpty(val)) {
                    return false;
                }
            } catch (JSONException e){
                return false;
            }
        }
        return true;
    }

    private Bitmap getQRBitMap(String data) throws Exception {
        Bitmap bitmap = null;
        if (firstTimeDisplay) {
            firstTimeDisplay = false;
            bitmap = FileUtils.getImageFromInternalStorage(QR_IMAGE_FILE_NAME, getContext());
        }

        if (bitmap == null) {
            bitmap = createQRCode(data, QR_CODE_BITMAP_WH, QR_CODE_BITMAP_WH);
            FileUtils.saveImageToInternalStore(bitmap, QR_IMAGE_FILE_NAME, getContext());
        }

        return bitmap;
    }

    private Bitmap createQRCode(String data, int width, int height) throws Exception {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        BitMatrix matrix;
        matrix = new MultiFormatWriter().encode(new String(data.getBytes(QR_CODE_CHAR_SET), QR_CODE_CHAR_SET),
                BarcodeFormat.QR_CODE, width, height, hintMap);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, matrix.get(x, y) ? 0xff000000 : 0xffffffff);
            }
        }
        return bitmap;
    }
}
