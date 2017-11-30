package app.anish.com.tapp.database;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 11/30/17.
 */

public class PeopleMetEngine {

    private static PersonMet scannedPerson;


    public static void initScannedPerson(JSONObject jsonObject) throws JSONException {
        scannedPerson = PersonMet.getPersonMetFromScan(jsonObject);
    }

    public static void saveScannedPerson() {
        if (scannedPerson != null) {
            PeopleMetDaoHolder.getDaoInstance().createPerson(scannedPerson);
            scannedPerson = null;
        }
    }

}
