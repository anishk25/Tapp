package app.anish.com.tapp.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by anish_khattar25 on 7/1/17.
 */

public class ContextUtils {

    public String getPackageHashKey(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(info.signatures[0].toByteArray());
            return Base64.encodeToString(md.digest(), Base64.DEFAULT);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("HASH ERROR!", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("HASH ERROR!", e.getMessage(), e);
        }
        return null;
    }
}
