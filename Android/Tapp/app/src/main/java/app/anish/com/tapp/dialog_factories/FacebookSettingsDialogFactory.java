package app.anish.com.tapp.dialog_factories;

import app.anish.com.tapp.R;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class FacebookSettingsDialogFactory extends FragmentBasedSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return "Facebook";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.facebook_settings_alert_dialog;
    }

}
