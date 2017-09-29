package app.anish.com.tapp.adapters.add_contact.builder;

import org.json.JSONObject;

import app.anish.com.tapp.adapters.ListViewItem;

/**
 * Builder for {@link ListViewItem}
 * @author akhattar
 */

public interface AddContactListViewFactory {
    ListViewItem getContactListViewItem(JSONObject jsonObject);
}
