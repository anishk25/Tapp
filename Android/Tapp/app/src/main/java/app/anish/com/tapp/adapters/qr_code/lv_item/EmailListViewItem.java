package app.anish.com.tapp.adapters.qr_code.lv_item;

import app.anish.com.tapp.dialog_factories.DialogFactory;
import app.anish.com.tapp.dialog_factories.EmailSettingsDialogFactory;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 9/17/17.
 */

public class EmailListViewItem extends QRCodeListViewItem {

    @Override
    protected SettingsInfo getSettingsInfo() {
        return SettingsInfo.EMAIL;
    }

    @Override
    protected DialogFactory getDialogFactory() {
        return new EmailSettingsDialogFactory();
    }
}
