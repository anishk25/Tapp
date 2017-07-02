package app.anish.com.tapp.data;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import app.anish.com.tapp.R;
import app.anish.com.tapp.utils.SharedPrefsUtils;
import app.anish.com.tapp.utils.AppConstants;
import app.anish.com.tapp.utils.StringUtils;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public abstract class SingleEditTextSettingsDialogFactory extends BinarySettingsDialogFactory {

    @Override
    protected void initUI(View rootView, Context context) {
        EditText editText = (EditText) rootView.findViewById(R.id.etSettingsDialogValue);
        String savedData = SharedPrefsUtils.getString(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, getKey().toString());
        if (!StringUtils.stringIsEmpty(savedData)) {
            editText.setText(savedData);
        }
    }

    @Override
    protected void saveData(View rootView, Context context) {
        String data  = ((EditText) rootView.findViewById(R.id.etSettingsDialogValue)).getText().toString();
        if (StringUtils.stringIsEmpty(data)) {
            SharedPrefsUtils.deleteKey(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, getKey());
        } else {
            SharedPrefsUtils.saveString(context, AppConstants.SETTINGS_SHARED_PREFS_KEY, getKey(), data);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.settings_alertdialog_edittext;
    }

    protected abstract String getKey();
}
