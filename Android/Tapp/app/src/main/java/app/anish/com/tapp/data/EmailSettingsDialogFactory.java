package app.anish.com.tapp.data;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.anish.com.tapp.R;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import app.anish.com.tapp.utils.StringUtils;

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
