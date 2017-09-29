package app.anish.com.tapp.adapters.add_contact.lv_item;

import android.app.AlertDialog;
import android.app.Dialog;
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
import app.anish.com.tapp.adapters.ListViewItem;
import app.anish.com.tapp.utils.PackageUtils;

/**
 * Created by akhattar on 8/16/17.
 */

public class AddToLinkedInContactListViewItem implements ListViewItem {

    private final String linkedInId;
    private final String contactName;

    public AddToLinkedInContactListViewItem(String linkedInId, String contactName) {
        this.linkedInId = linkedInId;
        this.contactName = contactName;
    }

    @Override
    public View getView(Context context, ViewGroup rootGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_contact_lv_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.ivAddContactIcon);
        TextView textView = (TextView) view.findViewById(R.id.tvAddContact);
        image.setImageResource(R.drawable.ic_linkedin_icon);
        textView.setText(R.string.linked_in_add_text);
        return view;
    }

    @Override
    public void performClickAction(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("Do you want to visit " + contactName + " 's LinkedIn page?")
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        launchLinkedInContactPage(context);
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
    }

    private void launchLinkedInContactPage(Context context) {
        Intent userProfileIntent;
        if (PackageUtils.isPackageInstalled(context, PackageUtils.LINKEDIN_PACKAGE_NAME)) {
            userProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://profile/" + linkedInId));
            userProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            userProfileIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + linkedInId));
        }
        context.startActivity(userProfileIntent);
    }
}
