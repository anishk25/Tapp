package app.anish.com.tapp.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by anish_khattar25 on 11/26/17.
 */

public class JSONUtils {

    private static final String LOG_TAG = JSONUtils.class.getSimpleName();

    public static String getString(JSONObject jsonObject, String key) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            Log.w(LOG_TAG, "Key " + key + "not present in json object " + jsonObject.toString());
            return null;
        }
    }
}
