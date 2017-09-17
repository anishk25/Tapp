package app.anish.com.tapp.adapters;

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
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;

/**
 * Created by akhattar on 5/5/17.
 */

public class QRCodeSettingsListViewAdapter extends ArrayAdapter<SettingsInfo> {

    private Context mContext;
    private static TappSharedPreferences sharedPreferences = TappSharedPreferences.getInstance();

    public QRCodeSettingsListViewAdapter(@NonNull ArrayList<SettingsInfo> data, Context context) {
        super(context, R.layout.qr_settings_lv_item, data);
        mContext = getContext();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SettingsInfo settingsInfo = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.qr_settings_lv_item, parent, false);
            viewHolder = createViewHolder(convertView);
            convertView.setTag(R.integer.view_holder_tag, viewHolder);
            checkAndDisableShareOption(settingsInfo, viewHolder);
            setupOnClickListenerForListItemView(convertView, viewHolder, settingsInfo);
            setupCheckboxListener(viewHolder.shareCheckBox, settingsInfo);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.integer.view_holder_tag);
        }

        setDataForViewsInViewHolder(viewHolder, settingsInfo);
        return convertView;
    }

    private ViewHolder createViewHolder(View rootView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvTitle = (TextView) rootView.findViewById(R.id.tvQRSettingsItemTitle);
        viewHolder.tvDetails = (TextView) rootView.findViewById(R.id.tvQRSettingsItemDetail);
        viewHolder.shareCheckBox = (CheckBox) rootView.findViewById(R.id.shareCheckBox);
        viewHolder.shareLabel = (TextView) rootView.findViewById(R.id.tvShareLabel);
        return viewHolder;
    }

    private void setDataForViewsInViewHolder(ViewHolder viewHolder, SettingsInfo settingsInfo) {
        String title = settingsInfo.getTitle();
        String details = getDetailDispString(settingsInfo);
        boolean share = sharedPreferences.getBoolean(settingsInfo.getShareInfoPrefKey());

        viewHolder.tvTitle.setText(title);
        viewHolder.tvDetails.setText(details);
        viewHolder.shareCheckBox.setChecked(share);
    }

    private void setupOnClickListenerForListItemView(final View convertView, final ViewHolder viewHolder, final SettingsInfo settingsInfo) {

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog;

                if (convertView.getTag(R.integer.dialog_tag) != null) {
                    dialog = (Dialog) convertView.getTag(R.integer.dialog_tag);
                } else {
                    dialog = settingsInfo.getDialogFactory().getDialog(mContext);
                    setupDismissListenerOnDialog(dialog, viewHolder, settingsInfo);
                    convertView.setTag(R.integer.dialog_tag, dialog);
                }

                dialog.show();
            }
        });
    }

    private void setupDismissListenerOnDialog(final Dialog dialog, final ViewHolder viewHolder, final SettingsInfo settingsInfo) {
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                String dispString = getDetailDispString(settingsInfo);
                viewHolder.tvDetails.setText(dispString);
            }
        });
    }

    private void setupCheckboxListener(final CheckBox checkBox, final SettingsInfo settingsInfo) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // check if data is present in shared pref
                if(isChecked && sharedPreferences.getString(settingsInfo.getInfoPrefKey()) == null) {
                    checkBox.setChecked(false);
                    Toast.makeText(mContext, "Property must be set before enabling share", Toast.LENGTH_LONG).show();
                } else {
                    sharedPreferences.saveBoolean(settingsInfo.getShareInfoPrefKey(), isChecked);
                }
            }
        });
    }

    private String getDetailDispString(SettingsInfo settingsInfo) {
        String prefix = settingsInfo.getInfoPrefix();
        String details = sharedPreferences.getString(settingsInfo.getInfoPrefKey());
        return details == null ? "Not Set" : prefix + details;
    }

    private void checkAndDisableShareOption(SettingsInfo settingsInfo, ViewHolder viewHolder) {
        if(settingsInfo.isMandatoryShare()) {
            viewHolder.shareCheckBox.setVisibility(View.GONE);
            viewHolder.shareLabel.setVisibility(View.GONE);
        }
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDetails;
        TextView shareLabel;
        CheckBox shareCheckBox;
    }
}
