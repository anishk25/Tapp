package app.anish.com.tapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import app.anish.com.tapp.R;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment {


    private CallbackManager mCallBackManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_display, container, false);
        initUI(rootView);
        return rootView;
    }


    private void initUI(View rootView) {
        mCallBackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) rootView.findViewById(R.id.bFbLogin);

        // read more on permissions here: https://developers.facebook.com/docs/facebook-login/permissions
        //http://stackoverflow.com/questions/29642759/profile-getcurrentprofile-returns-null-after-logging-in-fb-api-v4-0
        loginButton.setReadPermissions("email", "public_profile", "user_about_me");
        loginButton.setFragment(this);

        final TextView tvFbInfo = (TextView) rootView.findViewById(R.id.tvFbInfo);
        loginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("FACEBOOK", "Token is " + AccessToken.getCurrentAccessToken().getToken());
                Log.d("FACEBOOK", "Profile is " + Profile.getCurrentProfile().getId());



                //tvFbInfo.setText(Profile.getCurrentProfile().getId());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }
}
