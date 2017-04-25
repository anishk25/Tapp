package app.anish.com.tapp.data;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class FacebookSettingsDialogFactory extends LoginBasedSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initUI(View rootView, Context context) {

    }

    @Override
    protected void saveData(Context context) {

    }
}
