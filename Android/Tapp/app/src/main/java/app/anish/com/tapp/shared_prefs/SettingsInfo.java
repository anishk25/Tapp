package app.anish.com.tapp.shared_prefs;

import app.anish.com.tapp.dialog_factories.DialogFactory;
import app.anish.com.tapp.dialog_factories.EmailSettingsDialogFactory;
import app.anish.com.tapp.dialog_factories.FacebookSettingsDialogFactory;
import app.anish.com.tapp.dialog_factories.LinkedinSettingsDialogFactory;
import app.anish.com.tapp.dialog_factories.OwnerNameSettingsDialogFactory;
import app.anish.com.tapp.dialog_factories.PhoneNumberSettingsDialogFactory;

/**
 * Created by akhattar on 5/5/17.
 */

public enum SettingsInfo implements SharePrefKeyInfo {
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

        public boolean isMandatoryShare() {
            return true;
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


    public abstract String getTitle();
    public abstract DialogFactory getDialogFactory();
    public boolean isMandatoryShare() {
        return false;
    }


    public String getInfoPrefix() {
        return "";
    }


}
