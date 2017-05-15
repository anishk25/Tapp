package app.anish.com.tapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.AccessToken;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.R;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.Token;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import app.anish.com.tapp.utils.Constants;

/**
 * TODO: you need to encrypt the Linkedin Credential stuff
 * Created by akhattar on 4/28/17.
 * following this tutorial right now
 * https://www.studytutorial.in/linkedin-integration-and-login-in-android-tutorial
 */


public class LinkedinDialogFragment extends Fragment {

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

        initInitialButtonState(loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean loggedIntoLinkedin = (Boolean) loginButton.getTag(R.integer.linkedin_login_status);
                if (loggedIntoLinkedin == null || !loggedIntoLinkedin) {
                    startLoginFlow();
                } else {
                    startLogoutFlow();
                }
            }
        });
    }

    private void startLoginFlow() {
        loginButton.setText(R.string.linkedin_logout_text);
        loginButton.setTag(R.integer.linkedin_login_status, true);
        loginToLinkedin();
    }

    private void startLogoutFlow() {
        loginButton.setText(R.string.linkedin_login_text);
        loginButton.setTag(R.integer.linkedin_login_status, false);
        logoutOfLinkedin();
    }

    private void initInitialButtonState(Button loginButton) {
        String linkedInId = SharedPrefsUtils.getString(context, Constants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.toString());
        if (linkedInId != null) {
            loginButton.setText(R.string.linkedin_logout_text);
            loginButton.setTag(R.integer.linkedin_login_status, true);
        } else {
            loginButton.setText(R.string.linkedin_login_text);
            loginButton.setTag(R.integer.linkedin_login_status, false);
        }
    }

    private void loginToLinkedin() {
        LISessionManager sessionManager = LISessionManager.getInstance(context);
        toggleProgressBar();
        AccessToken existingToken = retrieveAccessToken();
        if (existingToken != null) {
            sessionManager.init(existingToken);
            getUserId();
        } else {
            sessionManager.init(this, buildScope(), new AuthListener() {
                @Override
                public void onAuthSuccess() {
                    saveCurrentAccessToken();
                    getUserId();
                }

                @Override
                public void onAuthError(LIAuthError error) {
                    toggleProgressBar();
                    Toast.makeText(context, "Error logging into linkedin", Toast.LENGTH_LONG).show();
                    Log.d("LINKEDIN ERROR", error.toString());
                }
            }, true);
        }
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE);
    }


    private void getUserId() {
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
        final APIHelper apiHelper = APIHelper.getInstance(context);
        apiHelper.getRequest(context, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                JSONObject result = apiResponse.getResponseDataAsJson();
                if (result != null) {
                    processAPISucess(result);
                } else {
                    Toast.makeText(context, "Didn't get any data from LinkedIn", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                Toast.makeText(context, "Error retrieving user information from LinkedIn",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void processAPISucess(JSONObject jsonObject) {
        saveLinkedinInfo(jsonObject);
        toggleProgressBar();
        Toast.makeText(context, "Successfully logged into Linkedin!", Toast.LENGTH_LONG).show();
    }

    private void saveCurrentAccessToken() {
        String accessToken = LISessionManager.getInstance(context)
                                .getSession().getAccessToken().toString();
        SharedPrefsUtils.saveString(context, Constants.SETTINGS_SHARED_PREFS_KEY,
                Token.LINKEDIN.toString(), accessToken);
    }

    private AccessToken retrieveAccessToken() {
        String tokenString = SharedPrefsUtils.getString(context, Constants.SETTINGS_SHARED_PREFS_KEY, Token.LINKEDIN.toString());
        if (tokenString != null) {
            return AccessToken.buildAccessToken(tokenString);
        }
        return null;
    }

    private void toggleProgressBar() {
        if (progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void saveLinkedinInfo(JSONObject jsonObject) {
        try {
            String linkedinId = jsonObject.getString("id");
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
            SharedPrefsUtils.saveString(context, Constants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey(), linkedinId);
            SharedPrefsUtils.saveString(context, Constants.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.LINKEDIN_NAME.getInfoPrefKey(),
                    firstName + " " + lastName);

        } catch (JSONException e) {
            Toast.makeText(context, "Error retrieving Linkedin Info from result", Toast.LENGTH_LONG);
        }
    }

    private void logoutOfLinkedin() {
        // remove the linkedin id and access token from shared preferences
        SharedPrefsUtils.deleteKey(context, Constants.SETTINGS_SHARED_PREFS_KEY, Token.LINKEDIN.toString());
        SharedPrefsUtils.deleteKey(context, Constants.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey());
        SharedPrefsUtils.deleteKey(context, Constants.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.LINKEDIN_NAME.getInfoPrefKey());
    }
}
