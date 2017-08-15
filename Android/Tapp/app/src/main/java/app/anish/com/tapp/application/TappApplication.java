package app.anish.com.tapp.application;

import android.app.Application;

import app.anish.com.tapp.shared_prefs.TappSharedPreferences;

/**
 * Custom application class for the App
 * @author akhattar
 */

public class TappApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TappSharedPreferences.init(getApplicationContext());
    }
}
