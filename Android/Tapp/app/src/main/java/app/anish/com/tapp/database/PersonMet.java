package app.anish.com.tapp.database;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 11/28/17.
 */



public class PersonMet {

    private final String name;
    private final String phoneNumber;
    private final String email;
    private final Date dateMet;
    private final String facebookId;
    private final String linkedInId;
    private final Bitmap personImage;

    public PersonMet(String name, String phoneNumber, String email, Date dateMet, String facebookId, String linkedInId, Bitmap personImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateMet = dateMet;
        this.facebookId = facebookId;
        this.linkedInId = linkedInId;
        this.personImage = personImage;
    }

    public static PersonMet getPersonMetFromScan(JSONObject jsonObject) throws JSONException{
        String name = getJSONString(jsonObject, SettingsInfo.OWNER_NAME.getInfoPrefKey());
        String phone = getJSONString(jsonObject, SettingsInfo.PHONE_NUMBER.getInfoPrefKey());
        String email = getJSONString(jsonObject, SettingsInfo.EMAIL.getInfoPrefKey());
        String facebookId = getJSONString(jsonObject, SecuredSharedPrefs.FACEBOOK_ID.getInfoPrefKey());
        String linkedInId = getJSONString(jsonObject, SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey());
        Date date = new Date();
        // TODO: retrieve image bitmap if linkedIn Id or facebookId is present
        return new PersonMet(name, phone, email, date, facebookId, linkedInId, null);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateMet() {
        return dateMet;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getLinkedInId() {
        return linkedInId;
    }

    public String toString() {
        return "name:" + name + ", phoneNumber:" + phoneNumber + ", email:" + email +
                ", dateMet:" + dateMet + ", facebookId:" + facebookId + ", linkedInId:" + linkedInId;
    }

    private static String getJSONString(JSONObject jsonObject, String key) throws JSONException {
        return jsonObject.has(key) ? jsonObject.getString(key) : null;
    }


}
