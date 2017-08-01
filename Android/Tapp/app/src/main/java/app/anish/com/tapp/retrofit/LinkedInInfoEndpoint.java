package app.anish.com.tapp.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Endpoint used with Retrofit to obtain user profile data
 */

public interface LinkedInInfoEndpoint {

    @GET("~:(id,first-name,last-name)?format=json")
    Call<LinkedInBasicProfileData> getBasicProfileData(@Header("Authorization") String accessToken);
}
