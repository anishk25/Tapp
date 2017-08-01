package app.anish.com.tapp.linkedin_auth;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;

import com.linkedin.platform.AccessToken;

import java.io.IOException;

import app.anish.com.tapp.dialogs.LinkedInWebViewLoginDialog;
import app.anish.com.tapp.retrofit.LinkedInBasicProfileData;
import app.anish.com.tapp.retrofit.LinkedInInfoEndpoint;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Login flow through through Web Dialog
 */

public final class LinkedInWebLoginFlow extends LinkedInLoginFlow {

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.linkedin.com/v1/people/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final LinkedInInfoEndpoint infoEndpoint = retrofit.create(LinkedInInfoEndpoint.class);

    private Context context;
    private FlowCompletionListener listener;

    public LinkedInWebLoginFlow(Context context, FlowCompletionListener listener) {
        this.context = context;
        this.listener = listener;
    }


    @Override
    public void startFlow() {
        Dialog dialog = new LinkedInWebViewLoginDialog(context, new LinkedInWebViewLoginDialog.AuthListener() {
            @Override
            public void onAuthSuccess(AccessToken accessToken) {
                getUserInfo(accessToken);
            }

            @Override
            public void onAuthError(String error) {
                listener.onFailure(new Exception(error));
            }
        });

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        dialog.getWindow().setLayout((6 * metrics.widthPixels) / 7, (4 * metrics.heightPixels) / 5);
        dialog.show();
    }

    private void getUserInfo(final AccessToken accessToken) {

        Call<LinkedInBasicProfileData> call = infoEndpoint.getBasicProfileData("Bearer " + accessToken.getValue());
        call.enqueue(new Callback<LinkedInBasicProfileData>() {
            @Override
            public void onResponse(Call<LinkedInBasicProfileData> call, Response<LinkedInBasicProfileData> response) {
                if (response.isSuccessful()) {
                    saveLinkedInInfo(response.body());
                    listener.onSuccess();
                } else {
                    processResponseFailure(response);
                }
            }

            @Override
            public void onFailure(Call<LinkedInBasicProfileData> call, Throwable t) {
                listener.onFailure(t);
            }


            private void processResponseFailure(Response<LinkedInBasicProfileData> response) {
                String errorMsg = "Unable to retrieve profile data";
                try {
                    errorMsg += " Error: " + response.errorBody().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listener.onFailure(new Exception(errorMsg));
            }
        });
    }

    private void saveLinkedInInfo(LinkedInBasicProfileData profileData) {
        SharedPrefsUtils.saveString(context, SharedPrefsUtils.SETTINGS_SHARED_PREFS_KEY, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey(), profileData.getId());
        SharedPrefsUtils.saveString(context, SharedPrefsUtils.SETTINGS_SHARED_PREFS_KEY, SettingsInfo.LINKEDIN_NAME.getInfoPrefKey(),
                profileData.getFirstName() + " " + profileData.getLastName());
    }
}
