package app.anish.com.tapp.data;

import android.app.Dialog;
import android.content.Context;

/**
 * Class to hold data for the settings view
 * Created by anish_khattar25 on 4/21/17.
 */

public class SettingsDataModel {

    private final String title;
    private final String details;
    private final boolean isEnabled;
    private final SettingsDialogFactory dialogFactory;


    public SettingsDataModel(String title, String details, boolean isEnabled, SettingsDialogFactory dialogFactory) {
        this.title = title;
        this.details = details;
        this.isEnabled = isEnabled;
        this.dialogFactory = dialogFactory;
    }

    public Dialog getAlertDialog(Context activityContext) {
        return dialogFactory.getDialog(activityContext);
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public SettingsDialogFactory getDialogFactory() {
        return dialogFactory;
    }
}
