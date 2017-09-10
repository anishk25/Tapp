package app.anish.com.tapp.dialog_factories;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import app.anish.com.tapp.R;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;
import app.anish.com.tapp.utils.StringUtils;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public abstract class SingleEditTextSettingsDialogFactory extends BinarySettingsDialogFactory {

    @Override
    protected void initUI(View rootView, Context context) {
        EditText editText = (EditText) rootView.findViewById(R.id.etSettingsDialogValue);
        editText.setInputType(getTypeOfInputText());
        String savedData = TappSharedPreferences.getInstance().getString(getKey());
        if (!StringUtils.stringIsEmpty(savedData)) {
            editText.setText(savedData);
        }
    }

    @Override
    protected void saveData(View rootView, Context context) {
        String data  = ((EditText) rootView.findViewById(R.id.etSettingsDialogValue)).getText().toString();
        if (StringUtils.stringIsEmpty(data)) {
            TappSharedPreferences.getInstance().deleteKey(getKey());
        } else {
            TappSharedPreferences.getInstance().saveString(getKey(), data);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.settings_alertdialog_edittext;
    }

    protected abstract String getKey();

    protected int getTypeOfInputText () {
        // default is just normal text
        return InputType.TYPE_CLASS_TEXT;
    }
}
