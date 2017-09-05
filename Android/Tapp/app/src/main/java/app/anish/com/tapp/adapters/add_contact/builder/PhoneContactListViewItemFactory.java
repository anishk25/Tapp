package app.anish.com.tapp.adapters.add_contact.builder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;
import app.anish.com.tapp.adapters.add_contact.lv_item.AddToPhoneContactListViewItem;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * factory for creating
 * {@link app.anish.com.tapp.adapters.add_contact.lv_item.AddToPhoneContactListViewItem}
 * objects
 * @author akhattar
 */

public class PhoneContactListViewItemFactory implements AddContactListViewFactory {

    private static final String LOG_TAG = PhoneContactListViewItemFactory.class.getSimpleName();
    private static final SettingsInfo [] JSON_KEYS = {SettingsInfo.OWNER_NAME,
            SettingsInfo.PHONE_NUMBER, SettingsInfo.EMAIL};

    @Override
    public AddContactListViewItem getContactListViewItem(JSONObject jsonObject) {

        Map<String, String> dataMap = new HashMap<>();

        for (SettingsInfo settingsInfo : JSON_KEYS) {
            try {
                String dataVal = jsonObject.getString(settingsInfo.getInfoPrefKey());
                dataMap.put(settingsInfo.getInfoPrefKey(), dataVal);
            } catch (JSONException e) {
                Log.d(LOG_TAG, "jsonObject for PhoneContactListViewItemFactory doesn't contain "
                        + settingsInfo.getInfoPrefKey());
            }
        }

        // need at least two values in the map, the name will always be there
        // nut another contact type (email, phone number)
        if (dataMap.size() > 1) {
            return new AddToPhoneContactListViewItem(dataMap);
        }

        return null;
    }
}
