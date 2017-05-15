package app.anish.com.tapp.shared_prefs;

/**
 * Created by akhattar on 5/5/17.
 */

public enum SecuredSharedPrefs implements SharePrefKeyInfo{
    FACEBOOK_ID {
        public String getInfoPrefKey() {
            return "FACEBOOK_ID";
        }

        public String getShareInfoPrefKey() {
            return SettingsInfo.FACEBOOK_NAME.getShareInfoPrefKey();
        }
    },
    LINKEDIN_ID {
        public String getInfoPrefKey() {
            return "LINKEDIN_ID";
        }

        public String getShareInfoPrefKey() {
            return SettingsInfo.LINKEDIN_NAME.getShareInfoPrefKey();
        }
    }
}
