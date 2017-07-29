package app.anish.com.tapp.callbacks;

import com.linkedin.platform.AccessToken;

/**
 * Callback interface used for listening to the dismissal
 * of {@link}
 * Created by anish_khattar25 on 7/27/17.
 */

public interface OnLinkedinDialogDismissListener {
    void onDismiss(AccessToken linkedinAccessToken);
}
