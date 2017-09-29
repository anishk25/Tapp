package app.anish.com.tapp.adapters.qr_code.lv_item;

import app.anish.com.tapp.dialog_factories.DialogFactory;
import app.anish.com.tapp.dialog_factories.FacebookSettingsDialogFactory;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 9/17/17.
 */

public class FacebookListViewItem extends QRCodeListViewItem {
    @Override
    protected SettingsInfo getSettingsInfo() {
        return SettingsInfo.FACEBOOK_NAME;
    }

    @Override
    protected DialogFactory getDialogFactory() {
        return new FacebookSettingsDialogFactory();
    }
}
