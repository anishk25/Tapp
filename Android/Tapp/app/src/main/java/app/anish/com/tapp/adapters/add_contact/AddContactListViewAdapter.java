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
import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;

/**
 * List View adapter for add contact items
 * @author akhattar
 */

public class AddContactListViewAdapter extends ArrayAdapter<AddContactListViewItem> {


    public AddContactListViewAdapter (Context context, ArrayList<AddContactListViewItem> data) {
        super(context, R.layout.add_contact_lv_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        AddContactListViewItem item = getItem(position);
        View view = item.getView(layoutInflater, parent);
        setupViewOnClickListener(view, item);
        return view;
    }

    private void setupViewOnClickListener(final View view, final AddContactListViewItem item) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.performAddAction(getContext());
            }
        });
    }


}
