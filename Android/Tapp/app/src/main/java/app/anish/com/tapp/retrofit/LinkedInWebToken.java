package app.anish.com.tapp.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * POJO class to represent a LinkedIn token
 * @author akhattar
 */

public class LinkedInWebToken {

    @SerializedName("access_token")
    private String tokenValue;

    @SerializedName("expires_in")
    private int expirationInSec;

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public int getExpirationInSec() {
        return expirationInSec;
    }

    public void setExpirationInSec(int expirationInSec) {
        this.expirationInSec = expirationInSec;
    }
}
