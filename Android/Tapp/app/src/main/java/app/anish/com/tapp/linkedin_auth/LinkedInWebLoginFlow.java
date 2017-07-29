package app.anish.com.tapp.linkedin_auth;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.linkedin.platform.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

import app.anish.com.tapp.dialogs.LinkedInWebViewLoginDialog;
import app.anish.com.tapp.utils.HttpUtils;

/**
 * Login flow through through Web Dialog
 */

public final class LinkedInWebLoginFlow extends LinkedInLoginFlow {

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
        AsyncTask<String, Void, JSONObject> asyncTask = new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(String... strings) {
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.addRequestProperty("Authorization", "Bearer " + accessToken.getValue());
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        String responseString = HttpUtils.getResponseString(connection);
                        JSONObject jsonObject =  new JSONObject(responseString);
                        return jsonObject;
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error getting response from LinkedIn Web API", Toast.LENGTH_LONG).show();
                }
                return null;
            }
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                if (jsonObject != null) {
                    try {
                        saveLinkedInInfo(jsonObject, context);
                        listener.onSuccess();
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error parsing information retrieved from LinkedIn", Toast.LENGTH_LONG).show();
                        listener.onFailure(e);
                    }
                }
            }
        };

        String url = "https://api.linkedin.com/v1/people/~?format=json";
        asyncTask.execute(url);
    }
}
