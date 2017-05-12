package app.anish.com.tapp.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import app.anish.com.tapp.R;
import app.anish.com.tapp.adapters.SettingsListViewAdapter;
import app.anish.com.tapp.shared_prefs.SettingsInfo;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        setupActionBar();
        initUI();

    }

    private void initUI() {
        ListView listView = (ListView) findViewById(R.id.lvSettings);
        ArrayList<SettingsInfo> data = new ArrayList<>(Arrays.asList(SettingsInfo.values()));
        SettingsListViewAdapter adapter = new SettingsListViewAdapter(data, this);
        listView.setAdapter(adapter);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
