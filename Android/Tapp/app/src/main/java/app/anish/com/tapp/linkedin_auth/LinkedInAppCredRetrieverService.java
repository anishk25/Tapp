package app.anish.com.tapp.linkedin_auth;

import static app.anish.com.tapp.fragments.LinkedInDialogFragment.LoginState;
import static app.anish.com.tapp.fragments.LinkedInDialogFragment.LINKEDIN_LOGIN_STATUS_PREF_KEY;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.linkedin.platform.APIHelper;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;

/**
 * Service for retrieving LinkedIn user information
 * through the App
 * @author akhattar
 */

public class LinkedInAppCredRetrieverService extends IntentService {

    private static final String URL = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name)";
    private static final TappSharedPreferences sharedPrefs = TappSharedPreferences.getInstance();
    private static final String LOG_TAG = LinkedInAppCredRetrieverService.class.getName();

    public LinkedInAppCredRetrieverService() {
        super(LinkedInAppCredRetrieverService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Context context = getApplicationContext();

        APIHelper apiHelper = APIHelper.getInstance(context);


        apiHelper.getRequest(context, URL, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                processApiRequestSuccess(apiResponse);
                countDownLatch.countDown();
            }

            @Override
            public void onApiError(LIApiError LIApiError) {
                Log.d(LOG_TAG, "error in api request: " + LIApiError.getMessage());
                sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.ERROR.toString());
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void processApiRequestSuccess(ApiResponse apiResponse) {
        JSONObject jsonObject = apiResponse.getResponseDataAsJson();
        try {
            saveLinkedInInfo(jsonObject);
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Error parsing api response into JSON");
            sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.ERROR.toString());
        }
        sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.DONE.toString());
    }

    private void saveLinkedInInfo(JSONObject jsonObject) throws JSONException {
        String linkedInId = jsonObject.getString("id");
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("lastName");
        sharedPrefs.saveString(SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey(), linkedInId);
        sharedPrefs.saveString(SettingsInfo.LINKEDIN_NAME.getInfoPrefKey(), firstName + " " + lastName);
    }

}
