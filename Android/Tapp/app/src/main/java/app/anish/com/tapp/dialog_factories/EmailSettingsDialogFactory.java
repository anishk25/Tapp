package app.anish.com.tapp.dialog_factories;

import android.text.InputType;
import android.util.Patterns;
import android.widget.EditText;

import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class EmailSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return SettingsInfo.EMAIL.getTitle();
    }

    @Override
    protected String getKey() {
        return SettingsInfo.EMAIL.getInfoPrefKey();
    }

    @Override
    protected int getTypeOfInputText() {
        return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
    }

    @Override
    protected boolean isDataInValidFormat(String data) {
        return Patterns.EMAIL_ADDRESS.matcher(data).matches();
    }

    @Override
    protected String getValidationErrorMsg() {
        return "Email address is not in correct format.";
    }
}
