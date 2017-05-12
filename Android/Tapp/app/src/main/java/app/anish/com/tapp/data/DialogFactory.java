package app.anish.com.tapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * Factory interface for creating the settings dialogs
 * Created by anish_khattar25 on 4/23/17.
 */

public interface DialogFactory {
    Dialog getDialog(Context context);
}
