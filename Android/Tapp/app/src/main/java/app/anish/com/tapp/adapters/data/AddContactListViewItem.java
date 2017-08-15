package app.anish.com.tapp.adapters.data;

import android.view.View;

/**
 * An interface representing the data in the list view
 * item for adding a Contact
 *
 * @author akhattar
 */

public interface AddContactListViewItem {

     View getView();
     void doAddContactAction();
}
