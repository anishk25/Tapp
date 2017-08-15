package app.anish.com.tapp.dialog_factories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import app.anish.com.tapp.R;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public abstract class FragmentBasedSettingsDialogFactory extends AbstractSettingsDialogFactory {

    @Override
    public Dialog getDialog(final Context context) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(getLayoutId(), null);
        dialogBuilder.setView(view)
                .setTitle(getTitle())
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        return dialogBuilder.create();
    }
}
