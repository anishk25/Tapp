package app.anish.com.tapp.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.util.Observable;
import java.util.Observer;

import app.anish.com.tapp.R;
import app.anish.com.tapp.dialogs.LinkedInWebViewLoginDialog;
import app.anish.com.tapp.linkedin_auth.LinkedInAppCredRetrieverService;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;
import app.anish.com.tapp.shared_prefs.Token;


/**
 * Created by akhattar on 4/28/17.
 * following this tutorial right now
 * https://www.studytutorial.in/linkedin-integration-and-login-in-android-tutorial
 */

public class LinkedInDialogFragment extends Fragment implements View.OnClickListener, Observer {

    private static final String LINKEDIN_APP_PACKAGE_NAME = "com.linkedin.android";

    private static final TappSharedPreferences sharedPrefs = TappSharedPreferences.getInstance();
    public static final String LINKEDIN_LOGIN_STATUS_PREF_KEY = "linkedInLoginState";

    private Button loginButton;
    private ProgressBar progressBar;
    private Context context;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPrefs.addObserver(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        sharedPrefs.deleteObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPrefs.addObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.linkedin_login, container, false);
        initUIElements(rootView);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LISessionManager.LI_SDK_AUTH_REQUEST_CODE) {
            LISessionManager.getInstance(getContext())
                    .onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initUIElements(View rootView) {
        loginButton = (Button) rootView.findViewById(R.id.bLinkedinButton);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pbLinkedin);

        initInitialButtonState();
        loginButton.setOnClickListener(this);
    }

    private void processLoginButtonClick() {
        Boolean loggedIntoLinkedIn = (Boolean) loginButton.getTag(R.integer.linkedin_login_status);
        if (loggedIntoLinkedIn == null || !loggedIntoLinkedIn) {
            loginToLinkedIn();
        } else {
            logoutOfLinkedIn();
        }
    }

    private void initInitialButtonState() {
        String linkedInId = sharedPrefs.getString(SecuredSharedPrefs.LINKEDIN_ID.toString());
        toggleLoginButton(linkedInId != null);
    }

    private void loginToLinkedIn() {
        toggleProgressBar(true);
//        if (PackageUtils.isPackageInstalled(getContext(), LINKEDIN_APP_PACKAGE_NAME)) {
//            getLinkedInInfoThroughApp();
//        } else {
//            getLinkedInInfoThroughWeb();
//        }
        getLinkedInInfoThroughWeb();
    }

    private void getLinkedInInfoThroughApp() {
        LISessionManager sessionManager = LISessionManager.getInstance(context);
        sessionManager.init(this, Scope.build(Scope.R_BASICPROFILE), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Intent intent = new Intent(context, LinkedInAppCredRetrieverService.class);
                context.startService(intent);
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(context, "Error authenticating user to LinkedIn", Toast.LENGTH_LONG).show();
            }
        }, false);
    }


    private void getLinkedInInfoThroughWeb() {
        Dialog dialog = new LinkedInWebViewLoginDialog(getContext());
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dialog.getWindow().setLayout((6 * metrics.widthPixels) / 7, (4 * metrics.heightPixels) / 5);
        dialog.show();
    }

    private void toggleProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void toggleLoginButton(boolean loggedIn) {
        if (loggedIn) {
            loginButton.setText(R.string.linkedin_logout_text);
            loginButton.setTag(R.integer.linkedin_login_status, true);
        } else {
            loginButton.setText(R.string.linkedin_login_text);
            loginButton.setTag(R.integer.linkedin_login_status, false);
        }
    }

    private void logoutOfLinkedIn() {
        // remove the linkedin id and access token from shared preferences
        sharedPrefs.deleteKey(SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey());
        sharedPrefs.deleteKey(SettingsInfo.LINKEDIN_NAME.getInfoPrefKey());
        sharedPrefs.deleteKey(SecuredSharedPrefs.LINKEDIN_WEB_TOKEN.getInfoPrefKey());
        toggleLoginButton(false);
    }

    private void processLoginResult() {
        String result = sharedPrefs.getString(LINKEDIN_LOGIN_STATUS_PREF_KEY);
        if (result != null) {
            LoginState loginState = LoginState.valueOf(sharedPrefs.getString(LINKEDIN_LOGIN_STATUS_PREF_KEY));
            switch (loginState) {
                case DONE:
                    Toast.makeText(context, "Successfully logged into LinkedIn", Toast.LENGTH_LONG).show();
                    toggleLoginButton(true);
                    break;
                case ERROR:
                    Toast.makeText(context, "Error logging into LinkedIn. Please try again", Toast.LENGTH_LONG).show();
                    toggleLoginButton(false);
                    break;
            }
            toggleProgressBar(false);
            sharedPrefs.deleteKey(LINKEDIN_LOGIN_STATUS_PREF_KEY);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLinkedinButton:
                processLoginButtonClick();
                break;
        }
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable instanceof TappSharedPreferences) {
            String key = (String) arg;
            if (LINKEDIN_LOGIN_STATUS_PREF_KEY.equals(key)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processLoginResult();
                    }
                });
            }
        }
    }

    public enum LoginState {
        DONE,
        ERROR
    }
}
