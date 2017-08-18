package app.anish.com.tapp.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Util methods for Android Services
 * @author akhattar
 */

public final class ServiceUtils {

    /**
     * Checks if a given service is running
     * @param serviceClass
     * @param context
     * @return true if service running, false otherwise
     */
    public static boolean isServiceRunning (Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
