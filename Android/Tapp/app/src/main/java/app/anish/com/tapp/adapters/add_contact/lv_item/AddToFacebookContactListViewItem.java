package app.anish.com.tapp.adapters.add_contact.lv_item;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.anish.com.tapp.R;
import app.anish.com.tapp.utils.PackageUtils;

/**
 * Created by akhattar on 8/16/17.
 */

public class AddToFacebookContactListViewItem implements AddContactListViewItem {


    private final String facebookId;
    private final String contactName;

    public AddToFacebookContactListViewItem(String facebookId, String contactName) {
        this.facebookId = facebookId;
        this.contactName = contactName;
    }

    @Override
    public View getView(LayoutInflater layoutInflater, ViewGroup rootGroup) {
        View view = layoutInflater.inflate(R.layout.add_contact_lv_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.ivAddContactIcon);
        TextView textView = (TextView) view.findViewById(R.id.tvAddContact);
        image.setImageResource(R.drawable.ic_facebook_icon);
        textView.setText(R.string.facebook_add_text);
        return view;
    }

    @Override
    public void performAddAction(final Context context) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context)
                .setMessage("Do you want to visit " + contactName + "'s facebook page?")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchFacebookContactPage(context);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing in cancel
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void launchFacebookContactPage(Context context) {
        Intent userProfileIntent;
        if (PackageUtils.isPackageInstalled(context, PackageUtils.FACEBOOK_PACKAGE_NAME)) {
            // show user profile through facebook app
            userProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/{" + facebookId + "}"));
            userProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            // show user profile through browser
            userProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/" + facebookId));
        }
        context.startActivity(userProfileIntent);
    }
}
