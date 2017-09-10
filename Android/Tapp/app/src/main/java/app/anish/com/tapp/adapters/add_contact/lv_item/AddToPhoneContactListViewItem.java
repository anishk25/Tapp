package app.anish.com.tapp.adapters.add_contact.lv_item;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import app.anish.com.tapp.R;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.ContactInfo;

/**
 * @author akhattar
 */

public class AddToPhoneContactListViewItem implements AddContactListViewItem {

    private static final String LOG_TAG = AddToPhoneContactListViewItem.class.getSimpleName();

    private Map<String, String> contactInfoData;

    public AddToPhoneContactListViewItem(Map<String, String> contactInfoData) {
        this.contactInfoData = contactInfoData;
        if (!contactInfoData.containsKey(SettingsInfo.OWNER_NAME.getInfoPrefKey())
                || !contactInfoData.containsKey(SettingsInfo.PHONE_NUMBER.getInfoPrefKey())) {
            throw new AssertionError("Bundle passed to " + AddToPhoneContactListViewItem.class.toString() +
                    " must contain the keys " + SettingsInfo.OWNER_NAME.getInfoPrefKey() +
                    "," + SettingsInfo.PHONE_NUMBER.getInfoPrefKey());
        }
    }

    @Override
    public View getView(LayoutInflater layoutInflater, ViewGroup rootGroup) {
        View view = layoutInflater.inflate(R.layout.add_contact_lv_item, null);
        ImageView image = (ImageView) view.findViewById(R.id.ivAddContactIcon);
        TextView textView = (TextView) view.findViewById(R.id.tvAddContact);
        image.setImageResource(R.drawable.ic_phone_icon);
        textView.setText(R.string.phone_add_text);
        return view;
    }

    @Override
    public void performAddAction(Context context) {
        ArrayList<ContentProviderOperation> operations = null;

        String phoneNumber = contactInfoData.get(SettingsInfo.PHONE_NUMBER.getInfoPrefKey());
        String contactId = ContactInfo.getContactId(context, phoneNumber);
        boolean isUpdate = true;

        if (contactId != null) {
            operations = getUpdateContactOperations(contactId);
        } else {
            operations = getAddContactOperations();
            isUpdate = false;
        }
        performBatchOperation(context, operations, isUpdate);
    }


    private void performBatchOperation(Context context, ArrayList<ContentProviderOperation> operations, boolean isUpdate) {

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
            String mainMsg = isUpdate ? "updated" : "added";
            Toast.makeText(context, "Successfully " + mainMsg + " contact from QR code", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            String mainMsg = isUpdate ? "updating" : "adding";
            String msg = "Error " + mainMsg + " contact from QR code";
            Log.d(LOG_TAG, msg, e);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    private ArrayList<ContentProviderOperation> getAddContactOperations() {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        operations.add(getAddInititilizationOperation());

        String contactName = contactInfoData.get(SettingsInfo.OWNER_NAME.getInfoPrefKey());
        operations.add(getAddNameOperation(contactName));

        String phoneNumber = contactInfoData.get(SettingsInfo.PHONE_NUMBER.getInfoPrefKey());
        operations.add(getAddPhoneNumberOperation(phoneNumber));

        if (contactInfoData.containsKey(SettingsInfo.EMAIL.getInfoPrefKey())) {
            String email = contactInfoData.get(SettingsInfo.EMAIL.getInfoPrefKey());
            operations.add(getAddEmailOperation(email));
        }

        return operations;
    }

    private ArrayList<ContentProviderOperation> getUpdateContactOperations(String contactId) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        String contactName = contactInfoData.get(SettingsInfo.OWNER_NAME.getInfoPrefKey());
        operations.add(getUpdateNameOperation(contactId, contactName));


        if (contactInfoData.containsKey(SettingsInfo.EMAIL.getInfoPrefKey())) {
            String email = contactInfoData.get(SettingsInfo.EMAIL.getInfoPrefKey());
            operations.add(getUpdateEmailOperation(contactId, email));
        }

        return operations;
    }

    private ContentProviderOperation getAddInititilizationOperation() {
        return ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build();
    }

    private ContentProviderOperation getAddPhoneNumberOperation(String phoneNumber) {
        ContentProviderOperation.Builder builder = getAddContactBaseBuilder();
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        builder.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        return builder.build();
    }

    private ContentProviderOperation getAddEmailOperation(String email) {
        ContentProviderOperation.Builder builder = getAddContactBaseBuilder();
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
        addEmailToOperationBuilder(builder, email);
        return builder.build();
    }

    private ContentProviderOperation getAddNameOperation(String name) {
        ContentProviderOperation.Builder builder = getAddContactBaseBuilder();
        builder.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        addNameToOperationBuilder(builder, name);
        return builder.build();
    }


    private ContentProviderOperation getUpdateNameOperation(String contactId, String name) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?",
                new String [] {contactId,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE });
        addNameToOperationBuilder(builder, name);
        return builder.build();
    }

    private ContentProviderOperation getUpdateEmailOperation(String contactId, String email) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI);
        builder.withSelection(ContactsContract.Data.CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?",
                new String [] {contactId,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE });
        addEmailToOperationBuilder(builder, email);
        return builder.build();
    }


    private ContentProviderOperation.Builder getAddContactBaseBuilder() {
        return ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID, 0);
    }

    private void addNameToOperationBuilder(ContentProviderOperation.Builder builder, String name) {
        String [] nameSplit = name.split(" ");
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, nameSplit[0]);

        if (nameSplit.length == 2) {
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, nameSplit[1]);
        } else {
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, nameSplit[1]);
            builder.withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, nameSplit[2]);
        }
    }

    private void addEmailToOperationBuilder(ContentProviderOperation.Builder builder, String email) {
        builder.withValue(ContactsContract.CommonDataKinds.Email.DATA, email);
        builder.withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
    }
}
