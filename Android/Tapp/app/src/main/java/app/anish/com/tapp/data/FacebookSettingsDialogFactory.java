package app.anish.com.tapp.data;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import app.anish.com.tapp.R;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class FacebookSettingsDialogFactory extends LoginBasedSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return "Facebook";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.facebook_settings_alert_dialog;
    }

}
