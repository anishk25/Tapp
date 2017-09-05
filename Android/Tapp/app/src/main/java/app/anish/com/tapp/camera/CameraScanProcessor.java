package app.anish.com.tapp.camera;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.add_contact.AddContactListViewAdapter;
import app.anish.com.tapp.adapters.add_contact.builder.AddContactListViewFactory;
import app.anish.com.tapp.adapters.add_contact.builder.FacebookContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.LinkedInContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.builder.PhoneContactListViewItemFactory;
import app.anish.com.tapp.adapters.add_contact.lv_item.AddContactListViewItem;

/**
 * Class for processing Camera Data
 * Created by anish_khattar25 on 4/19/17.
 */

@SuppressWarnings("deprecation")
public abstract class CameraScanProcessor {

    private static final AddContactListViewFactory [] factories =
            {
               new PhoneContactListViewItemFactory(),
               new FacebookContactListViewItemFactory(),
               new LinkedInContactListViewItemFactory()
            };

    protected final Context mContext;


    protected CameraScanProcessor(Context context) {
        this.mContext = context;
    }

    public abstract void processCameraDataAndOpenContactsDialog(byte [] data);

    protected void showAddContactDialog(String qrCodeData) {
        try {
            JSONObject jsonObject = new JSONObject(qrCodeData);
            showAddContactDialog(jsonObject);
        } catch (JSONException e) {
            Toast.makeText(mContext, "Error : Malformed QR code data", Toast.LENGTH_LONG).show();
        }
    }

    private void showAddContactDialog(JSONObject jsonObject) {
        View dialogView = getAddContactDialogView(jsonObject);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);

        dialogBuilder
                .setView(dialogView)
                .setTitle(R.string.add_contact_dialog_title)
                .setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });
        dialogBuilder.create().show();
    }

    private View getAddContactDialogView(JSONObject jsonObject) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.add_contact_list_view, null);
        ListView listView = (ListView) view.findViewById(R.id.lvAddContact);

        ArrayList<AddContactListViewItem> listViewItems = new ArrayList<>();
        for (AddContactListViewFactory factory : factories) {
            AddContactListViewItem item = factory.getContactListViewItem(jsonObject);
            if (item != null) {
                listViewItems.add(item);
            }
        }

        AddContactListViewAdapter adapter = new AddContactListViewAdapter(mContext, listViewItems);
        listView.setAdapter(adapter);
        return view;
    }
}
