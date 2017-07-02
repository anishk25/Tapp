package app.anish.com.tapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
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
import java.util.Map;

import app.anish.com.tapp.R;
import app.anish.com.tapp.activities.SettingsActivity;
import app.anish.com.tapp.data.ContactInfo;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.SharePrefKeyInfo;
import app.anish.com.tapp.utils.AppConstants;
import app.anish.com.tapp.utils.SharedPrefsUtils;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = QRCodeDisplayFragment.class.getName();
    private static final String APP_OPENED_FIRST_TIME_KEY = "APP_OPENED_FIRST_TIME";
    private static final String QR_CODE_CHAR_SET = "ISO-8859-1";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int SETTINGS_ACTIVITY_RESULT_CODE = 2;

    // UI Elements
    private Activity mActivity;
    private ImageView qrImage;
    private String currQRData;
    private ProgressBar qrProgressBar;



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
            } else {
                showPermNotGrantedToast();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTIVITY_RESULT_CODE) {
            redrawQRCode();
        }
    }

    private void initUI(View rootView) {
        qrProgressBar = (ProgressBar) rootView.findViewById(R.id.pbQrCode);
        qrImage = (ImageView) rootView.findViewById(R.id.ivQrCode);
        initQRImage();
        Button settingsButton = (Button) rootView.findViewById(R.id.bQRCodeSettings);
        settingsButton.setOnClickListener(this);
    }


    private void launchSettingsActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_ACTIVITY_RESULT_CODE);
    }

    private void populateContactInfo() {
        boolean opened = SharedPrefsUtils.getBoolean(mActivity, AppConstants.SETTINGS_SHARED_PREFS_KEY, APP_OPENED_FIRST_TIME_KEY);
        if (!opened) {
            SharedPrefsUtils.saveBoolean(mActivity, AppConstants.SETTINGS_SHARED_PREFS_KEY, APP_OPENED_FIRST_TIME_KEY, true);
            showPermissionDialog();
        }
    }

    private void populateSharedPrefs() {
        String ownerName = ContactInfo.getOwnerName(mActivity);
        String phoneNumber = ContactInfo.getCurrentPhoneNumber(mActivity);
        String email = ContactInfo.getOwnerEmail(mActivity);

        SharedPreferences sharedPref = mActivity.getSharedPreferences(AppConstants.SETTINGS_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SettingsInfo.OWNER_NAME.getInfoPrefKey(), ownerName);
        editor.putString(SettingsInfo.PHONE_NUMBER.getInfoPrefKey(), phoneNumber);
        editor.putString(SettingsInfo.EMAIL.getInfoPrefKey(), email);
        editor.putBoolean(SettingsInfo.OWNER_NAME.getShareInfoPrefKey(), true);
        editor.putBoolean(SettingsInfo.PHONE_NUMBER.getShareInfoPrefKey(), true);
        editor.putBoolean(SettingsInfo.EMAIL.getShareInfoPrefKey(), true);
        editor.commit();
    }

    private void showPermissionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Read Contacts Permission needed")
                .setMessage("This permission is needed to get your name, phone number and email. This will be automatically encoded in the QR code if you allow this permission.")
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

    private void initQRImage() {
        try {
            currQRData = getSavedContactInfo();
            drawQRCode(currQRData);
        } catch (Exception e) {
            Toast.makeText(mActivity, "Error Intializing QR Code with contact data", Toast.LENGTH_LONG);
        }
    }

    private void redrawQRCode() {
        // do diff of current data on QR code and new data
        try {
            String updatedData = getSavedContactInfo();
            if (!currQRData.equals(updatedData)) {
                drawQRCode(updatedData);
                currQRData = updatedData;
            }
        } catch (Exception e) {
            Toast.makeText(mActivity, "Error redrawing QR code", Toast.LENGTH_LONG);
        }

    }


    private void drawQRCode(final String data) throws Exception {
        qrImage.setVisibility(View.GONE);
        qrProgressBar.setVisibility(View.VISIBLE);

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {

                    final Bitmap bitmap = createQRCode(data, 400, 400);
                    qrImage.post(new Runnable() {
                        public void run() {
                            qrImage.setImageBitmap(bitmap);
                            qrImage.setVisibility(View.VISIBLE);
                            qrProgressBar.setVisibility(View.GONE);
                        }
                    });
                } catch (Exception e) {
                    Log.d(TAG, "Error generating bitmap image");
                }
            }
        });
        thread.start();
    }

    private String getSavedContactInfo() throws JSONException {
        JSONObject obj1 = getSavedData(SettingsInfo.values());
        JSONObject obj2 = getSavedData(SecuredSharedPrefs.values());
        JSONObject result = mergeJSONObjects(obj1, obj2);

        // insert App Json Signature into final Json object
        String signature = getResources().getString(R.string.json_share_signature);
        result.put(AppConstants.JSON_SIGNATURE_KEY, signature);

        return result.toString();
    }


    private JSONObject getSavedData(SharePrefKeyInfo[] keyInfoArr) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (SharePrefKeyInfo info : keyInfoArr) {
            boolean shareable = SharedPrefsUtils.getBoolean(mActivity, AppConstants.SETTINGS_SHARED_PREFS_KEY, info.getShareInfoPrefKey());
            if (shareable) {
                String data = SharedPrefsUtils.getString(mActivity, AppConstants.SETTINGS_SHARED_PREFS_KEY, info.getInfoPrefKey());
                if (data != null) {
                    jsonObject.put(info.toString(), data);
                }
            }
        }
        return jsonObject;
    }

    private JSONObject mergeJSONObjects(JSONObject object1, JSONObject object2) throws JSONException {
        JSONObject result = new JSONObject(object1.toString());
        Iterator<String> it = object2.keys();
        while (it.hasNext()) {
            result.put(it.next(), object2.get(it.next()));
        }
        return result;
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
