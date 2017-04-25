package app.anish.com.tapp.data;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public class OwnerNameSettingsDialogFactory extends SingleEditTextSettingsDialogFactory {
    @Override
    protected String getTitle() {
        return "Name";
    }

    @Override
    protected SharedPrefsKey getKey() {
        return SharedPrefsKey.OWNER_NAME;
    }
}
