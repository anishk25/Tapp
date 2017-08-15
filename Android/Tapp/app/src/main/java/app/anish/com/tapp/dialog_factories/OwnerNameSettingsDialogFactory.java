package app.anish.com.tapp.dialog_factories;

import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public class OwnerNameSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {
    @Override
    protected String getTitle() {
        return "Name";
    }

    @Override
    protected String getKey() {
        return SettingsInfo.OWNER_NAME.getInfoPrefKey();
    }
}
