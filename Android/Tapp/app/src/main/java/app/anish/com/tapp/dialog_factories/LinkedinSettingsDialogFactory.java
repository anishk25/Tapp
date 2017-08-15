package app.anish.com.tapp.dialog_factories;

import app.anish.com.tapp.R;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class LinkedinSettingsDialogFactory extends FragmentBasedSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return "LinkedIn";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.linkedin_settings_alert_dialog;
    }
}
