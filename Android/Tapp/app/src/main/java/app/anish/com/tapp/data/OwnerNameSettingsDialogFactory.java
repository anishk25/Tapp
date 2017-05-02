package app.anish.com.tapp.data;

import app.anish.com.tapp.shared_prefs.SharedPrefsKey;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public class OwnerNameSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {
    @Override
    protected String getTitle() {
        return "Name";
    }

    @Override
    protected SharedPrefsKey getKey() {
        return SharedPrefsKey.OWNER_NAME;
    }
}
