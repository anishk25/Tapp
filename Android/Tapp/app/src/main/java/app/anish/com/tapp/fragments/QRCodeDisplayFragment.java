package app.anish.com.tapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;

import app.anish.com.tapp.R;
import app.anish.com.tapp.activities.SettingsActivity;
import app.anish.com.tapp.data.ContactInfo;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.Constants;
import app.anish.com.tapp.utils.SharedPrefsUtils;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment implements View.OnClickListener {

    private static final String APP_OPENED_FIRST_TIME_KEY = "APP_OPENED_FIRST_TIME";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private Activity mActivity;


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

    private void initUI(View rootView) {
        Button settingsButton = (Button) rootView.findViewById(R.id.bQRCodeSettings);
        settingsButton.setOnClickListener(this);
    }



    private void launchSettingsActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    private void populateContactInfo() {
        boolean opened = SharedPrefsUtils.getBoolean(mActivity, Constants.SETTINGS_SHARED_PREFS_KEY, APP_OPENED_FIRST_TIME_KEY);
        if (!opened) {
            SharedPrefsUtils.saveBoolean(mActivity, Constants.SETTINGS_SHARED_PREFS_KEY, APP_OPENED_FIRST_TIME_KEY, true);
            showPermissionDialog();
        }
    }

    private void populateSharedPrefs() {
        String ownerName = ContactInfo.getOwnerName(mActivity);
        String phoneNumber = ContactInfo.getCurrentPhoneNumber(mActivity);
        String email = ContactInfo.getOwnerEmail(mActivity);

        SharedPreferences sharedPref = mActivity.getSharedPreferences(Constants.SETTINGS_SHARED_PREFS_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SettingsInfo.OWNER_NAME.getInfoPrefKey(), ownerName);
        editor.putString(SettingsInfo.PHONE_NUMBER.getInfoPrefKey(), phoneNumber);
        editor.putString(SettingsInfo.EMAIL.getInfoPrefKey(), email);
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
                                && mActivity.checkSelfPermission(Manifest.permission.READ_SMS)  != PackageManager.PERMISSION_GRANTED) {
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

    private boolean allPermissionsGranted(int [] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
