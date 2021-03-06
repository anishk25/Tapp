package app.anish.com.tapp.linkedin_auth;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.linkedin.platform.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import app.anish.com.tapp.BuildConfig;
import app.anish.com.tapp.dialogs.LinkedInWebViewLoginDialog;
import app.anish.com.tapp.retrofit.LinkedInAuthEndpoint;
import app.anish.com.tapp.retrofit.LinkedInBasicProfileData;
import app.anish.com.tapp.retrofit.LinkedInInfoEndpoint;
import app.anish.com.tapp.retrofit.LinkedInWebToken;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static app.anish.com.tapp.fragments.LinkedInDialogFragment.LINKEDIN_LOGIN_STATUS_PREF_KEY;
import static app.anish.com.tapp.fragments.LinkedInDialogFragment.LoginState;

/**
 * Service for retrieving user LinkedIn information through
 * the LinkedIn Web Api
 * @author akhattar
 */

public class LinkedInWebCredRetrieverService extends IntentService {

    public static final String LINKEDIN_AUTHORIZATION_TOKEN_KEY = "authorization_token";

    private static final String GRANT_TYPE = "authorization_code";
    private static final TappSharedPreferences sharedPrefs = TappSharedPreferences.getInstance();
    private static final String LOG_TAG = LinkedInWebCredRetrieverService.class.getName();

    private static final LinkedInAuthEndpoint authEndpoint =
            new Retrofit.Builder()
                    .baseUrl("https://www.linkedin.com/uas/oauth2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(LinkedInAuthEndpoint.class);

    private static final LinkedInInfoEndpoint infoEndpoint =
            new Retrofit.Builder()
                    .baseUrl("https://api.linkedin.com/v1/people/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(LinkedInInfoEndpoint.class);

    public LinkedInWebCredRetrieverService() {
        super(LinkedInWebCredRetrieverService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String authToken = intent.getExtras().getString(LINKEDIN_AUTHORIZATION_TOKEN_KEY);
        startUserInfoRetrieval(authToken);
    }

    private void startUserInfoRetrieval(String authToken) {
        Call<LinkedInWebToken> call = authEndpoint.getToken(GRANT_TYPE, authToken,
                LinkedInWebViewLoginDialog.REDIRECT_URI, BuildConfig.LINKEDIN_API_KEY, BuildConfig.LINKEDIN_SECRET_KEY);
        try {
            Response<LinkedInWebToken> response = call.execute();
            if (response != null && response.isSuccessful()) {
                LinkedInWebToken linkedInWebToken = response.body();
                if (linkedInWebToken != null) {
                    AccessToken accessToken = getAccessToken(linkedInWebToken);
                    getUserInfo(accessToken);
                } else {
                    throw new Exception("LinkedIn Web token retrieved from response is null");
                }
            } else {
                throw new Exception("Error getting LinkedIn Web Token response " + getResponseErrorString(response));
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error getting LinkedIn Web Token. Exception : " + e.getMessage());
            sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.ERROR.toString());
        }
    }

    private void getUserInfo(AccessToken accessToken) {
        Log.d("LINKEDIN", "Access token is " + accessToken.getValue());
        Call<LinkedInBasicProfileData> call = infoEndpoint.getBasicProfileData("Bearer " + accessToken.getValue());
        try {
            Response<LinkedInBasicProfileData> response = call.execute();
            if (response != null && response.isSuccessful()) {
                LinkedInBasicProfileData data = response.body();
                if (data != null) {
                    sharedPrefs.saveString(SecuredSharedPrefs.LINKEDIN_WEB_TOKEN.getInfoPrefKey(), accessToken.toString());
                    sharedPrefs.saveString(SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey(), data.getId());
                    sharedPrefs.saveString(SettingsInfo.LINKEDIN_NAME.getInfoPrefKey(), data.getFirstName() + " " + data.getLastName());
                    Log.d("LINKEDIN! LOGIN!", "The linkedin id is " + data.getId());
                } else {
                    throw new Exception("Profile data received from response is null");
                }
            } else {
                throw new Exception("Error getting profile data response " + getResponseErrorString(response));
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Error getting profile data. Exception : " + e.getMessage());
            sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.ERROR.toString());
        }
        sharedPrefs.saveString(LINKEDIN_LOGIN_STATUS_PREF_KEY, LoginState.DONE.toString());
    }

    /**
     * converts the webToken parameter to {@link AccessToken}
     * @param webToken
     * @return {@link JSONObject}
     */
    private AccessToken getAccessToken (LinkedInWebToken webToken) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        //Calculate date of expiration, the expiration value
        //return by web authorization tells us in how many
        // seconds the token will expire from the time it was
        // retrieved
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, webToken.getExpirationInSec());
        String expireDateInMs = Long.toString(calendar.getTimeInMillis());

        jsonObject.put(AccessToken.ACCESS_TOKEN_VALUE, webToken.getTokenValue());
        jsonObject.put(AccessToken.EXPIRES_ON, expireDateInMs);
        return AccessToken.buildAccessToken(jsonObject);
    }

    private <T> String getResponseErrorString(Response<T> response) {
        return response == null ? "Response is null" : "Error Code: " + response.code();
    }
}
