package app.anish.com.tapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by anish_khattar25 on 4/24/17.
 */

public abstract class AbstractSettingsDialogFactory implements DialogFactory {

    protected abstract String getTitle();
    protected abstract int getLayoutId();
}
