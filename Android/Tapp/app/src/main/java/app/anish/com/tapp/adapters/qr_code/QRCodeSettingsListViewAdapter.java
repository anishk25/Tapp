package app.anish.com.tapp.adapters.qr_code;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.qr_code.lv_item.QRCodeListViewItem;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;

/**
 * Created by akhattar on 5/5/17.
 */

public class QRCodeSettingsListViewAdapter extends ArrayAdapter<QRCodeListViewItem> {

    public QRCodeSettingsListViewAdapter(@NonNull ArrayList<QRCodeListViewItem> data, Context context) {
        super(context, R.layout.qr_settings_lv_item, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QRCodeListViewItem item = getItem(position);
        if (item != null) {
            if (convertView == null) {
                convertView = item.getView(getContext(), parent);
                setupOnClickListener(convertView, item);
            }
            item.updateData();
        }
        return convertView;
    }


    private void setupOnClickListener(final View view, final QRCodeListViewItem item) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.performClickAction(getContext());
            }
        });
    }
}
