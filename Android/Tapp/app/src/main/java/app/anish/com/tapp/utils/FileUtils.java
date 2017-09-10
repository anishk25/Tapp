package app.anish.com.tapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Class providing utility methods for handling files
 * internally stored to Android
 * Created by anish_khattar25 on 9/9/17.
 */

public final class FileUtils {

    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    public static void saveImageToInternalStore(Bitmap bitmap, String fileName, Context context) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            boolean saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            if (!saved) {
                throw new Exception("Error compressing bitmap to output stream");
            }
            fos.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error storing bitmap to internal storage", e);
        }
    }

    public static Bitmap getImageFromInternalStorage(String fileName, Context context) {
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            if (bitmap == null) {
                throw new Exception("Error decoding bitmap");
            }
            return bitmap;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error reading image from file " + fileName, e);
        }
        return null;
    }
}
