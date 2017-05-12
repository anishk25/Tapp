package app.anish.com.tapp.data;

import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class PhoneNumberSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return "Phone Number";
    }

    @Override
    protected String getKey() {
        return SettingsInfo.PHONE_NUMBER.getInfoPrefKey();
    }
}
