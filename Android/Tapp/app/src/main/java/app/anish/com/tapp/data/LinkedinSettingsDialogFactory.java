package app.anish.com.tapp.data;

import android.content.Context;
import android.view.View;

/**
 * Created by anish_khattar25 on 4/23/17.
 */

public class LinkedinSettingsDialogFactory extends LoginBasedSettingsDialogFactory {

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }
}
