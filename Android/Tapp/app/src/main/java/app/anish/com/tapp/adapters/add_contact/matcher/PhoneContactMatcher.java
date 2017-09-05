package app.anish.com.tapp.adapters.add_contact.matcher;

import app.anish.com.tapp.shared_prefs.SettingsInfo;

/**
 * Created by anish_khattar25 on 8/18/17.
 */

public class PhoneContactMatcher extends ContactTypeMatcher {

    private static final String [] keys = {SettingsInfo.EMAIL.getInfoPrefKey(), SettingsInfo.PHONE_NUMBER.getInfoPrefKey()};

    @Override
    protected String[] getMetaDataKeys() {
        return keys;
    }

    @Override
    protected boolean allKeysMandatory() {
        return false;
    }




}
