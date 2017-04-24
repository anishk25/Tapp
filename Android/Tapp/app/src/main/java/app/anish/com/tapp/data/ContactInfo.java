package app.anish.com.tapp.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Patterns;

import java.util.regex.Pattern;

import app.anish.com.tapp.exceptions.EmailNotFoundException;
import app.anish.com.tapp.exceptions.OwnerNameNotFoundException;
import app.anish.com.tapp.exceptions.PhoneNumberNotFoundException;
import app.anish.com.tapp.utils.StringUtils;

/**
 * Class for obtaining the users contact information
 * (phone number, name, email, etc.)
 * Created by anish_khattar25 on 4/23/17.
 */

public final class ContactInfo {

    private static final String[] SELF_PROJECTION = new String[] {ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
    private static final int OWNER_NAME_CURSOR_INDEX = 1;

    public static String getCurrentPhoneNumber(Context context) throws PhoneNumberNotFoundException {
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        if (StringUtils.stringIsEmpty(phoneNumber)) {
            throw new PhoneNumberNotFoundException("No phone number found");
        }
        return phoneNumber;
    }

    public static String getOwnerName(Context context) throws OwnerNameNotFoundException{
        Cursor cursor = context.getContentResolver().query(ContactsContract.Profile.CONTENT_URI, SELF_PROJECTION, null, null, null);
        cursor.moveToFirst();

        String ownerName =  cursor.getString(OWNER_NAME_CURSOR_INDEX);

        if (StringUtils.stringIsEmpty(ownerName)) {
            throw new OwnerNameNotFoundException("Owner name not found");
        }

        return ownerName;
    }


    public static String getOwnerEmail(Context context) throws EmailNotFoundException{
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context).getAccounts();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }

        throw new EmailNotFoundException("User email not found!");
    }

}
