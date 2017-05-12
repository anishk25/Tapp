package app.anish.com.tapp.shared_prefs;

import app.anish.com.tapp.data.DialogFactory;
import app.anish.com.tapp.data.EmailSettingsDialogFactory;
import app.anish.com.tapp.data.FacebookSettingsDialogFactory;
import app.anish.com.tapp.data.LinkedinSettingsDialogFactory;
import app.anish.com.tapp.data.OwnerNameSettingsDialogFactory;
import app.anish.com.tapp.data.PhoneNumberSettingsDialogFactory;

/**
 * Created by akhattar on 5/5/17.
 */

public enum SettingsInfo {
    OWNER_NAME {
        public String getInfoPrefKey() {
            return "OWNER_NAME";
        }

        public String getShareInfoPrefKey() { return "SHARE_OWNER_NAME"; }

        public String getTitle() {
            return "Owner Name";
        }

        public DialogFactory getDialogFactory() {
            return new OwnerNameSettingsDialogFactory();
        }
    },
    EMAIL {
        public String getInfoPrefKey() {
            return "EMAIL";
        }

        public String getShareInfoPrefKey() { return "SHARE_EMAIL"; }

        public String getTitle() {
            return "Email";
        }

        public DialogFactory getDialogFactory() {
            return new EmailSettingsDialogFactory();
        }
    },
    PHONE_NUMBER {
        public String getInfoPrefKey() {
            return "PHONE_NUMBER";
        }

        public String getShareInfoPrefKey() {return "SHARE_PHONE_NUMBER"; }

        public String getTitle() {
            return "Phone Number";
        }

        public DialogFactory getDialogFactory() {
            return new PhoneNumberSettingsDialogFactory();
        }
    },
    FACEBOOK_NAME {
        public String getInfoPrefKey() {
            return "FACEBOOK_NAME";
        }

        public String getShareInfoPrefKey() {return "SHARE_FACEBOOK_NAME"; };

        public String getTitle() {
            return "Facebook";
        }

        public DialogFactory getDialogFactory() {
            return new FacebookSettingsDialogFactory();
        }

        @Override
        public String getInfoPrefix() {
            return "Logged in as: ";
        }
    },
    LINKEDIN_NAME {
        public String getInfoPrefKey() {
            return "LINKEDIN_NAME";
        }

        public String getShareInfoPrefKey() {return "SHARE_LINKEDIN_NAME"; }

        public String getTitle() {
            return "LinkedIn";
        }

        public DialogFactory getDialogFactory() {
            return new LinkedinSettingsDialogFactory();
        }

        @Override
        public String getInfoPrefix() {
            return "Logged in as: ";
        }
    };


    public abstract String getInfoPrefKey();
    public abstract String getShareInfoPrefKey();
    public abstract String getTitle();
    public abstract DialogFactory getDialogFactory();

    public String getInfoPrefix() {
        return "";
    }


}
