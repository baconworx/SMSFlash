package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.receivers.MessageReceiver;

public class MainApp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        Button button = (Button) this.findViewById(R.id.settingsButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainApp.this, SettingsActivity.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);
            }
        });

        button = (Button) this.findViewById(R.id.filtersButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainApp.this, Filters.class);
                settingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);
            }
        });

        button = (Button) this.findViewById(R.id.testConfigButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigDatabase configDatabase = new ConfigDatabase(MainApp.this, true);
                configDatabase.open();
                configDatabase.close();
            }
        });

        MessageReceiver.SetTriggersFromDb(this);
    }
}
