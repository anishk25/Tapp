package app.anish.com.tapp.adapters.add_contact.matcher;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anish_khattar25 on 8/18/17.
 */

public abstract class ContactTypeMatcher {

    public boolean matchesCondition(JSONObject jsonObject) {
        for (String key : getMetaDataKeys()) {
            boolean containsKey = jsonObject.has(key);
            if (!allKeysMandatory() && containsKey) {
                return true;
            } else if (!containsKey) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String>  getContactMetaData(JSONObject jsonObject) {
        if (!matchesCondition(jsonObject)) {
            throw new RuntimeException("match condition not met");
        }

        Map<String, String> result = new HashMap<>();

        for (String key : getMetaDataKeys()) {
            try {
                result.put(key, jsonObject.getString(key));
            } catch (JSONException e) {
                // this exception will never happen since we already checked the keys in the hash map
            }
        }

        return result;
    }

    protected abstract String [] getMetaDataKeys();
    protected abstract boolean allKeysMandatory();





}
