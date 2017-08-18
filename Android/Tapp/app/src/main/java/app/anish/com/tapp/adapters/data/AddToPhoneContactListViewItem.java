package app.anish.com.tapp.adapters.data;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.anish.com.tapp.R;

/**
 * Created by akhattar on 8/16/17.
 */

public class AddToPhoneContactListViewItem implements AddContactListViewItem {

    public static String EMAIL_BUNDLE_KEY = "BUNDLE_EMAIL";
    public static String PHONE_NUMBER_BUNDLE_KEY = "BUNDLE_PHONE_NUMBER";
    public static String NAME_BUNDLE_KEY = "BUNDLE_NAME";

    private Bundle contactInfoBundle;

    public AddToPhoneContactListViewItem(Bundle contactInfoBundle) {
        this.contactInfoBundle = contactInfoBundle;
    }


    @Override
    public View getView(LayoutInflater layoutInflater, ViewGroup rootGroup) {
        View view = layoutInflater.inflate(R.layout.add_contact_lv_item, rootGroup);
        ImageView image = (ImageView) view.findViewById(R.id.ivAddContactIcon);
        TextView textView = (TextView) view.findViewById(R.id.tvAddContact);
        image.setImageResource(R.drawable.ic_linkedin_icon);
        textView.setText(R.string.linked_in_add_text);
        return view;
    }

    @Override
    public void performAddAction(Context context) {

    }
}
