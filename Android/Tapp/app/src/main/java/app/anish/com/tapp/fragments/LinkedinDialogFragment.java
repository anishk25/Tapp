package app.anish.com.tapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;

import app.anish.com.tapp.R;
import app.anish.com.tapp.linkedin_auth.LinkedInLoginFlow.FlowCompletionListener;
import app.anish.com.tapp.linkedin_auth.LinkedInAppLoginFlow;
import app.anish.com.tapp.linkedin_auth.LinkedInWebLoginFlow;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.Token;
import app.anish.com.tapp.utils.PackageUtils;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import app.anish.com.tapp.utils.AppConstants;


/**
 * Created by akhattar on 4/28/17.
 * following this tutorial right now
 * https://www.studytutorial.in/linkedin-integration-and-login-in-android-tutorial
 */

public class LinkedinDialogFragment extends Fragment implements View.OnClickListener {


    private static final String LOG_TAG = LinkedinDialogFragment.class.getSimpleName();
    private static final String LINKEDIN_APP_PACKAGE_NAME = "com.linkedin.android";

    private final FlowCompletionListener flowCompletionListener = new MyFlowCompletionListener();

    private Button loginButton;
    private ProgressBar progressBar;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
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
        String linkedInId = SharedPrefsUtils.getString(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.toString());
        toggleLoginButton(linkedInId != null);
    }

    private void loginToLinkedIn() {
        toggleProgressBar(true);

        if (PackageUtils.isPackageInstalled(getContext(), LINKEDIN_APP_PACKAGE_NAME)) {
            getLinkedInInfoThroughApp();
        } else {
            getLinkedInInfoThroughWeb();
        }
    }

    private void getLinkedInInfoThroughApp() {
        LinkedInAppLoginFlow appLoginFlow = new LinkedInAppLoginFlow(getContext(), this, flowCompletionListener);
        appLoginFlow.startFlow();
    }


    private void getLinkedInInfoThroughWeb() {
        LinkedInWebLoginFlow appLoginFlow = new LinkedInWebLoginFlow(getContext(), flowCompletionListener);
        appLoginFlow.startFlow();
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
        SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, Token.LINKEDIN.toString());
        SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey());
        SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.LINKEDIN_NAME.getInfoPrefKey());
        toggleLoginButton(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bLinkedinButton:
                processLoginButtonClick();
                break;
        }
    }

    private class MyFlowCompletionListener implements FlowCompletionListener {

        @Override
        public void onSuccess() {
            Toast.makeText(context, "Successfully logged into LinkedIn", Toast.LENGTH_SHORT).show();
            toggleLoginButton(true);
            toggleProgressBar(false);
        }

        @Override
        public void onFailure(Throwable throwable) {
            Toast.makeText(context, "Failed to login into LinkedIn", Toast.LENGTH_SHORT).show();
            toggleProgressBar(false);
        }
    }
}
