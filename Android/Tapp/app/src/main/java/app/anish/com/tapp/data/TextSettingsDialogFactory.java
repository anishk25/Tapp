package app.anish.com.tapp.data;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import app.anish.com.tapp.R;
import app.anish.com.tapp.utils.StringUtils;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public abstract class TextSettingsDialogFactory implements SettingsDialogFactory {


    @Override
    public Dialog getDialog(final Context activityContext) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activityContext);

        LayoutInflater inflater = LayoutInflater.from(activityContext);
        final View view = inflater.inflate(R.layout.settings_alertdialog_edittext, null);
        initDialogUI(view, activityContext);

        dialogBuilder.setView(inflater.inflate(R.layout.settings_alertdialog_edittext, null))
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String data = ((EditText) view.findViewById(R.id.etSettingsDialogValue)).getText().toString();
                        saveChanges(data, activityContext);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do Nothing for cancel
                    }
                });
        return dialogBuilder.create();
    }

    private void initDialogUI(View rootView, Context context) {
        TextView tvTitle = (TextView) rootView.findViewById(R.id.tvSettingsDialogTitle);
        tvTitle.setText(getTitle(context));
        EditText editText = (EditText) rootView.findViewById(R.id.etSettingsDialogValue);
        String savedData = getTextData(context);
        if (!StringUtils.stringIsEmpty(savedData)) {
            editText.setText(savedData);
        }
    }

    protected abstract void saveChanges(String data, Context context);
    protected abstract String getTextData(Context context);
    protected abstract String getTitle(Context context);
}
