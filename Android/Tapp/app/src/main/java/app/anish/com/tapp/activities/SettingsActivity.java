package app.anish.com.tapp.activities;import android.app.AlertDialog;import android.content.Context;import android.content.DialogInterface;import android.content.SharedPreferences;import android.content.pm.PackageManager;import android.os.Build;import android.support.v7.app.ActionBar;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.widget.ListView;import android.widget.Toast;import java.util.ArrayList;import java.util.Arrays;import java.util.jar.Manifest;import app.anish.com.tapp.R;import app.anish.com.tapp.adapters.SettingsListViewAdapter;import app.anish.com.tapp.data.ContactInfo;import app.anish.com.tapp.shared_prefs.SettingsInfo;import app.anish.com.tapp.utils.Constants;import app.anish.com.tapp.utils.SharedPrefsUtils;public class SettingsActivity extends AppCompatActivity {    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.settings_layout);        setupActionBar();        initUI();    }    private void initUI() {        ListView listView = (ListView) findViewById(R.id.lvSettings);        ArrayList<SettingsInfo> data = new ArrayList<>(Arrays.asList(SettingsInfo.values()));        SettingsListViewAdapter adapter = new SettingsListViewAdapter(data, this);        listView.setAdapter(adapter);    }    private void setupActionBar() {        ActionBar actionBar = getSupportActionBar();        if (actionBar != null) {            actionBar.setDisplayHomeAsUpEnabled(true);        }    }}