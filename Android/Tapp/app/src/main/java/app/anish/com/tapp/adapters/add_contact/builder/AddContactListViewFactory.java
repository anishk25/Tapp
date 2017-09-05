package app.anish.com.tapp.adapters.add_contact.builder;

import org.json.JSONObject;

import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;

/**
 * Builder for {@link app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem}
 * @author akhattar
 */

public interface AddContactListViewFactory {
    AddContactListViewItem getContactListViewItem(JSONObject jsonObject);
}
