package app.anish.com.tapp.adapters.add_contact.lv_item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An interface representing the data in the list view
 * item for adding a Contact
 *
 * @author akhattar
 */

public interface AddContactListViewItem {
     /**
      * Get the view for the add contact list
      * @param layoutInflater
      * @param rootViewGroup
      * @return contact View
      */
     View getView(LayoutInflater layoutInflater, ViewGroup rootViewGroup);

     /**
      * Performs the add action when user clicks on this particular
      * item
      */
     void performAddAction(Context context);
}
