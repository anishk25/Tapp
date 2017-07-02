package app.anish.com.tapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import app.anish.com.tapp.R;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import app.anish.com.tapp.utils.AppConstants;

/**
 * TODO: You need to encrypt the Facebook Id being stored
 * Created by akhattar on 4/26/17.
 */

public class FacebookDialogFragment extends Fragment {


    private CallbackManager mCallBackManager;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initProfileTracker();
        context = getActivity().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.facebook_login, container, false);
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void initUI(View rootView) {
        initFacebookButton(rootView);
    }

    private void initFacebookButton(View rootView) {
        mCallBackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.bFbLogin);

        // read more on permissions here: https://developers.facebook.com/docs/facebook-login/permissions
        loginButton.setReadPermissions("email", "public_profile", "user_about_me");
        loginButton.setFragment(this);
        registerCallBackOnFbButton(loginButton);
    }


    private void registerCallBackOnFbButton(LoginButton loginButton) {
        loginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                // do nothing for now
            }

            @Override
            public void onCancel() {
                // do nothing for now
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, "Error logging in: " + error.getMessage(), Toast.LENGTH_LONG);
            }
        });
    }

    private void initProfileTracker() {
        ProfileTracker profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                int toastStringId;
                if (currentProfile != null) {
                    toastStringId = R.string.login_success_message;
                    saveFacebookInfo(currentProfile);
                } else {
                    toastStringId = R.string.logout_success_message;
                    deleteFacebookInfo();
                }
                Toast.makeText(context, toastStringId, Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void saveFacebookInfo(Profile profile) {
        SharedPrefsUtils.saveString(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.FACEBOOK_NAME.getInfoPrefKey(),
                profile.getFirstName() + " " + profile.getLastName());
        SharedPrefsUtils.saveString(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.FACEBOOK_ID.getInfoPrefKey(),
                profile.getId());

    }

    private void deleteFacebookInfo() {
        SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.FACEBOOK_NAME.getInfoPrefKey());
        SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.FACEBOOK_ID.getInfoPrefKey());
    }
}
