package app.anish.com.tapp.data;

import app.anish.com.tapp.shared_prefs.SharedPrefsKey;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class EmailSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {

    private static final String EMAIL_KEY = "EMAIL";

    @Override
    protected String getTitle() {
        return "Email";
    }

    @Override
    protected SharedPrefsKey getKey() {
        return SharedPrefsKey.EMAIL;
    }
}
