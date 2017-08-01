package app.anish.com.tapp.linkedin_auth;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.AppConstants;
import app.anish.com.tapp.utils.SharedPrefsUtils;

/**
 * Interface representing a login flow
 * that is used for retrieving the user's
 * LinkedIn Profile Information
 */

public abstract class LinkedInLoginFlow {
    public abstract void startFlow();

    public interface FlowCompletionListener {
        void onSuccess();
        void onFailure(Throwable throwable);
    }
}
