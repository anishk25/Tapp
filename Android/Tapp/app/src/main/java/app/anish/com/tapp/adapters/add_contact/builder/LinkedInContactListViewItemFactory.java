package app.anish.com.tapp.adapters.add_contact.builder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;
import app.anish.com.tapp.adapters.add_contact.lv_item.AddToLinkedInContactListViewItem;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Factory implementation for constructing
 * {@link app.anish.com.tapp.adapters.add_contact.lv_item.AddToLinkedInContactListViewItem}
 * objects
 * @author akhattar
 */

public class LinkedInContactListViewItemFactory implements AddContactListViewFactory {

    private static final String LOG_TAG = LinkedInContactListViewItemFactory.class.getSimpleName();

    @Override
    public AddContactListViewItem getContactListViewItem(JSONObject jsonObject) {
        if (jsonObject.has(SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey())) {
            try {
                String linkedInId = jsonObject.getString(SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey());
                String name = jsonObject.getString(SettingsInfo.OWNER_NAME.getInfoPrefKey());
                return new AddToLinkedInContactListViewItem(linkedInId, name);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error AddToLinkedInContactListViewItem", e);
            }
        }

        return null;
    }
}
