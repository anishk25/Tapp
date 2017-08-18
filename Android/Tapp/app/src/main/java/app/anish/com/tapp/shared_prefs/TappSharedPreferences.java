package app.anish.com.tapp.shared_prefs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

/**
 * Singleton class to represent Shared Preferences in the app
 * @author akhattar
 */

public final class TappSharedPreferences extends Observable {

    private static TappSharedPreferences instance;
    private  SharedPreferences mSharedPreferences;
    private  SharedPreferences.Editor mEditor;
    private boolean mBulkUpdate = false;
    private LinkedList<String> bulkEditKeyQueue = new LinkedList<>();


    private TappSharedPreferences (Context context) {
        mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new TappSharedPreferences(context);
        }
    }

    public static TappSharedPreferences getInstance() {
        if (instance == null) {
            throw new RuntimeException("Init must be called before getInstance()");
        }
        return instance;
    }

    public void saveString(String key, String value) {
        if (!value.equals(getString(key))) {
            doEdit(key);
            mEditor.putString(key, value);
            doCommit(key);
        }
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, null);
    }


    public void saveBoolean(String key, boolean value) {
        if (getBoolean(key) != value) {
            doEdit(key);
            mEditor.putBoolean(key, value);
            doCommit(key);
        }
    }

    public void deleteKey(String key) {
        doEdit(key);
        mEditor.remove(key);
        doCommit(key);
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }


    public void edit() {
        mBulkUpdate = true;
        mEditor = mSharedPreferences.edit();
    }

    public void commit() {
        if (mEditor == null || !mBulkUpdate) {
            throw new RuntimeException("Only call commit() after calling edit()");
        }
        mBulkUpdate = false;
        mEditor.commit();
        mEditor = null;

        while (!bulkEditKeyQueue.isEmpty()) {
            notifyPrefsObservers(bulkEditKeyQueue.poll());
        }
    }

    private void doEdit(String prefsKey) {
        if (!mBulkUpdate && mEditor == null) {
            mEditor = mSharedPreferences.edit();
        } else if (mBulkUpdate) {
            bulkEditKeyQueue.add(prefsKey);
        }
    }

    private void doCommit(String prefsKey) {
        if (!mBulkUpdate && mEditor != null) {
            mEditor.commit();
            mEditor = null;
            notifyPrefsObservers(prefsKey);
        }
    }

    private void notifyPrefsObservers(String sharedPrefKey) {
        setChanged();
        notifyObservers(sharedPrefKey);
    }


}
