package app.anish.com.tapp.adapters.add_contact;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.ListViewItem;

/**
 * List View adapter for add contact items
 * @author akhattar
 */

public class AddContactListViewAdapter extends ArrayAdapter<ListViewItem> {


    public AddContactListViewAdapter (Context context, ArrayList<ListViewItem> data) {
        super(context, R.layout.add_contact_lv_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListViewItem item = getItem(position);
        View view = item.getView(getContext(), parent);
        setupViewOnClickListener(view, item);
        return view;
    }

    private void setupViewOnClickListener(final View view, final ListViewItem item) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.performClickAction(getContext());
            }
        });
    }


}
