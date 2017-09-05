package app.anish.com.tapp.adapters.add_contact.matcher;

import app.anish.com.tapp.shared_prefs.SecuredSharedPrefs;

/**
 * Created by anish_khattar25 on 8/18/17.
 */

public class LinkedinContactMatcher extends ContactTypeMatcher {

    private static final String [] keys = {SecuredSharedPrefs.LINKEDIN_ID.getInfoPrefKey()};

    @Override
    protected String[] getMetaDataKeys() {
        return keys;
    }

    @Override
    protected boolean allKeysMandatory() {
        return true;
    }

}
