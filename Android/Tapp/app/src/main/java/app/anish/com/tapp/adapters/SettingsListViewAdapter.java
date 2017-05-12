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

import java.util.ArrayList;

import app.anish.com.tapp.R;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.utils.Constants;
import app.anish.com.tapp.utils.SharedPrefsUtils;

/**
 * Created by akhattar on 5/5/17.
 */

public class SettingsListViewAdapter extends ArrayAdapter<SettingsInfo> {

    public SettingsListViewAdapter(@NonNull ArrayList<SettingsInfo> data, Context context) {
        super(context, R.layout.cv_settings_base_content, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SettingsInfo settingsInfo = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.cv_settings_base_content, parent, false);
            viewHolder = createViewHolder(convertView);
            convertView.setTag(R.integer.view_holder_tag, viewHolder);
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
        viewHolder.tvTitle = (TextView) rootView.findViewById(R.id.tvSettingsItemTitle);
        viewHolder.tvDetails = (TextView) rootView.findViewById(R.id.tvSettingsItemDetail);
        viewHolder.shareCheckBox = (CheckBox) rootView.findViewById(R.id.shareCheckBox);
        return viewHolder;
    }

    private void setDataForViewsInViewHolder(ViewHolder viewHolder, SettingsInfo settingsInfo) {
        String title = settingsInfo.getTitle();
        String details = getDetailDispString(settingsInfo);
        boolean share = SharedPrefsUtils.getBoolean(getContext(),
                Constants.SETTINGS_SHARED_PREFS_KEY, settingsInfo.getShareInfoPrefKey());

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
                    dialog = settingsInfo.getDialogFactory().getDialog(getContext());
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

    private void setupCheckboxListener(CheckBox checkBox, final SettingsInfo settingsInfo) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefsUtils.saveBoolean(getContext(), Constants.SETTINGS_SHARED_PREFS_KEY,
                        settingsInfo.getShareInfoPrefKey(), isChecked);
            }
        });
    }

    private String getDetailDispString(SettingsInfo settingsInfo) {
        String prefix = settingsInfo.getInfoPrefix();
        String details = SharedPrefsUtils.getString(getContext(), Constants.SETTINGS_SHARED_PREFS_KEY,
                settingsInfo.getInfoPrefKey());
        return details == null ? "Not Set" : prefix + details;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvDetails;
        CheckBox shareCheckBox;
    }
}