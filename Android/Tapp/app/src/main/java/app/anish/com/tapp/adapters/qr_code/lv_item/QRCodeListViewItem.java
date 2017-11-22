package app.anish.com.tapp.adapters.qr_code.lv_item;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.ListViewItem;
import app.anish.com.tapp.dialog_factories.DialogFactory;
import app.anish.com.tapp.shared_prefs.SettingsInfo;
import app.anish.com.tapp.shared_prefs.TappSharedPreferences;

/**
 * Created by anish_khattar25 on 9/17/17.
 */

public abstract class QRCodeListViewItem implements ListViewItem {

    private static TappSharedPreferences sharedPreferences = TappSharedPreferences.getInstance();
    private Dialog generatedDialog;
    private ViewHolder viewHolder;

    @Override
    public View getView(Context context, ViewGroup rootViewGroup) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.qr_settings_lv_item, rootViewGroup, false);
        viewHolder = createViewHolder(view);
        checkAndDisableShareOption(view);
        setupCheckBoxListener(context);
        return view;
    }

    @Override
    public void performClickAction(Context context) {
        if (generatedDialog == null) {
            generatedDialog = getDialogFactory().getDialog(context);
            setupDismissListenerOnDialog();
        }
        generatedDialog.show();
    }


    public void updateData() {
        SettingsInfo settingsInfo = getSettingsInfo();
        String title = settingsInfo.getTitle();
        String details = getDetailDispString(settingsInfo);
        boolean share = sharedPreferences.getBoolean(settingsInfo.getShareInfoPrefKey());

        viewHolder.tvTitle.setText(title);
        viewHolder.tvDetails.setText(details);
        viewHolder.shareCheckBox.setChecked(share);
    }

    private String getDetailDispString(SettingsInfo settingsInfo) {
        String prefix = settingsInfo.getInfoPrefix();
        String details = sharedPreferences.getString(settingsInfo.getInfoPrefKey());
        return details == null ? "Not Set" : prefix + details;
    }

    private ViewHolder createViewHolder(View rootView) {
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvTitle = (TextView) rootView.findViewById(R.id.tvQRSettingsItemTitle);
        viewHolder.tvDetails = (TextView) rootView.findViewById(R.id.tvQRSettingsItemDetail);
        viewHolder.shareCheckBox = (CheckBox) rootView.findViewById(R.id.shareCheckBox);
        viewHolder.shareLabel = (TextView) rootView.findViewById(R.id.tvShareLabel);
        return viewHolder;
    }

    private void checkAndDisableShareOption(View rootView) {
        if(getSettingsInfo().isMandatoryShare()) {


            viewHolder.shareCheckBox.setVisibility(View.GONE);
            viewHolder.shareLabel.setVisibility(View.GONE);
        }
    }

    private void setupDismissListenerOnDialog() {
        generatedDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                String dispString = getDetailDispString(getSettingsInfo());
                viewHolder.tvDetails.setText(dispString);
            }
        });
    }

    private void setupCheckBoxListener(final Context context) {
        viewHolder.shareCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked && sharedPreferences.getString(getSettingsInfo().getInfoPrefKey()) == null) {
                    viewHolder.shareCheckBox.setChecked(false);
                    Toast.makeText(context, "Property must be set before enabling share", Toast.LENGTH_LONG).show();
                } else {
                    sharedPreferences.saveBoolean(getSettingsInfo().getShareInfoPrefKey(), isChecked);
                }
            }
        });
    }

    protected abstract SettingsInfo getSettingsInfo();
    protected abstract DialogFactory getDialogFactory();

    private class ViewHolder {
        private TextView tvTitle;
        private TextView tvDetails;
        private TextView shareLabel;
        private CheckBox shareCheckBox;
    }
}
