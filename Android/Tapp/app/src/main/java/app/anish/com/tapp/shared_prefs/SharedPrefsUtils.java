package app.anish.com.tapp.shared_prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Util class for saving to and reading date from
 * {@link SharedPreferences}
 * Created by akhattar on 4/24/17.
 */

public final class SharedPrefsUtils {

    public static String getString(Context context, String prefsKey, String valueKey) {
        SharedPreferences sharedPref = context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        return sharedPref.getString(valueKey, null);
    }

    public static void saveString(Context context, String prefsKey, String valueKey, String value) {
        SharedPreferences sharedPref = context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(valueKey, value);
        editor.commit();
    }


    public static void deleteKey(Context context, String prefsKey, String valueKey) {
        SharedPreferences sharedPref = context.getSharedPreferences(prefsKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(valueKey);
        editor.commit();
    }
}
