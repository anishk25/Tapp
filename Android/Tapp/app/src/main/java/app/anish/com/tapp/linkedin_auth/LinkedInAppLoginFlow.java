package app.anish.com.tapp.linkedin_auth;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.SharedPrefsUtils;

/**
 * Login Flow through LinkedIn's Android App
 */

public final class LinkedInAppLoginFlow extends LinkedInLoginFlow {

    private Context context;
    private FlowCompletionListener listener;
    private Fragment callingFragment;

    public LinkedInAppLoginFlow(Context context, Fragment callingFragment, FlowCompletionListener listener) {
        this.context = context;
        this.callingFragment = callingFragment;
        this.listener = listener;
    }

    @Override
    public void startFlow() {
        LISessionManager sessionManager = LISessionManager.getInstance(context);
        sessionManager.init(callingFragment, Scope.build(Scope.R_BASICPROFILE), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                getUserInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(context, "Error authenticating user to LinkedIn", Toast.LENGTH_LONG).show();
                listener.onFailure(new Exception(error.toString()));
            }
        }, false);
    }

    private void getUserInfo() {
        APIHelper apiHelper = APIHelper.getInstance(context);
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
        apiHelper.getRequest(context, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                processApiRequestSuccess(apiResponse);
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                Toast.makeText(context, "API error while getting user information for LinkedIn.", Toast.LENGTH_LONG).show();
                listener.onFailure(liApiError);
            }
        });
    }

    private void processApiRequestSuccess(ApiResponse apiResponse) {
        JSONObject jsonObject = apiResponse.getResponseDataAsJson();
        try {
            saveLinkedInInfo(jsonObject);
            listener.onSuccess();
        } catch (JSONException e) {
            Toast.makeText(context, "Error parsing information retrieved from LinkedIn", Toast.LENGTH_LONG).show();
            listener.onFailure(e);
        }
    }

    private void saveLinkedInInfo(JSONObject jsonObject) throws JSONException {
        String linkedInId = jsonObject.getString("id");
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        SharedPrefsUtils.saveString(context, SharedPrefsUtils.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey(), linkedInId);
        SharedPrefsUtils.saveString(context, SharedPrefsUtils.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.LINKEDIN_NAME.getInfoPrefKey(),
                firstName + " " + lastName);
    }
}
