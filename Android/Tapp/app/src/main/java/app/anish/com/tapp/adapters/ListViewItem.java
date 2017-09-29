package app.anish.com.tapp.adapters;

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

public interface ListViewItem {
     /**
      * Get the view for the add contact list
      * @param context
      * @param rootViewGroup
      * @return contact View
      */
     View getView(Context context, ViewGroup rootViewGroup);

     /**
      * Performs the add action when user clicks on this particular
      * item
      */
     void performClickAction(Context context);
}
