package app.anish.com.tapp.retrofit;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Endpoint used with Retrofit for LinkedIn Web Authorization
 */

public interface LinkedInAuthEndpoint {

    @POST("accessToken")
    Call<LinkedInWebToken> getToken(@Query("grant_type") String grantType, @Query("code") String authorizationCode,
                                    @Query("redirect_uri") String redirectUri, @Query("client_id") String apiKey,
                                    @Query("client_secret") String secretKey);

}
