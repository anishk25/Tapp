package app.anish.com.tapp.shared_prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Observable;

/**
 * Singleton class to represent Shared Preferences in the app
 * @author akhattar
 */

public final class TappSharedPreferences extends Observable {

    public static final String ERRROR_VALUE = "error";

    private static TappSharedPreferences tappSharedPreferences;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private TappSharedPreferences () {}

    public static TappSharedPreferences getInstance() {
        if (tappSharedPreferences == null) {
            throw new RuntimeException("Need to call init before getting an instance of TappSharedPreferences");
        }
        return tappSharedPreferences;
    }

    public static void init(Context context) {
        if (tappSharedPreferences == null) {
            tappSharedPreferences = new TappSharedPreferences();
            sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public void saveString(String key, String value) {
        if (!getString(key).equals(value)) {
            editor.putString(key, value);
            editor.commit();
            notifyPrefsObervers(key);
        }
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void deleteKey(String key) {
        editor.remove(key);
        editor.commit();
    }


    public void saveBoolean(String key, boolean value) {
        if (getBoolean(key) != value) {
            editor.putBoolean(key, value);
            editor.commit();
            notifyPrefsObervers(key);
        }
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    private void notifyPrefsObervers(String sharedPrefKey) {
        setChanged();
        notifyObservers(sharedPrefKey);
    }

}
