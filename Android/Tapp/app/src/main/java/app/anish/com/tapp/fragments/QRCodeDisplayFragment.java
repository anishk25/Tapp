package app.anish.com.tapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;

import app.anish.com.tapp.R;

/**
 * Created by akhattar on 4/11/17.
 */

public class QRCodeDisplayFragment extends Fragment {


    private CallbackManager mCallBackManager;
    private ProfileTracker profileTracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qr_code_display, container, false);
        return rootView;
    }


}
