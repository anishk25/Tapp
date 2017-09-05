package app.anish.com.tapp.adapters.add_contact.builder;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;
import app.anish.com.tapp.adapters.add_contact.lv_item.AddToFacebookContactListViewItem;
import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Factory for constructing
 * {@link app.anish.com.tapp.adapters.add_contact.lv_item.AddToFacebookContactListViewItem}
 * objects
 * @author akhattar
 */

public class FacebookContactListViewItemFactory implements AddContactListViewFactory {

    private static final String LOG_TAG = FacebookContactListViewItemFactory.class.getSimpleName();

    @Override
    public AddContactListViewItem getContactListViewItem(JSONObject jsonObject) {
        if (jsonObject.has(SecuredSharedPrefs.FACEBOOK_ID.getInfoPrefKey())) {
            try {
                String facebookId = jsonObject.getString(SecuredSharedPrefs.FACEBOOK_ID.getInfoPrefKey());
                String name = jsonObject.getString(SettingsInfo.OWNER_NAME.getInfoPrefKey());
                return new AddToFacebookContactListViewItem(facebookId, name);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error getting AddToFacebookContactListViewItem", e);
            }
        }
        return null;
    }
}
