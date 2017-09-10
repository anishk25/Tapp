package app.anish.com.tapp.dialog_factories;

import android.text.InputType;

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

    @Override
    protected int getTypeOfInputText() {
        return InputType.TYPE_CLASS_PHONE;
    }
}
