package app.anish.com.tapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;

import app.anish.com.tapp.R;
import app.anish.com.tapp.activities.SettingsActivity;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_display, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        Button settingsButton = (Button) rootView.findViewById(R.id.bQRCodeSettings);
        settingsButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bQRCodeSettings:
                launchSettingsActivity();
                break;
        }
    }

    private void launchSettingsActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }
}
