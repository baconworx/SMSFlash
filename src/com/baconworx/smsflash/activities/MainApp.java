package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.classes.Trigger;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;
import com.baconworx.smsflash.receivers.MessageReceiver;

import java.util.ArrayList;
import java.util.List;

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

        ConfigDatabase configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        SparseArray<Filter> filters = configDatabase.getFilters();
        List<Trigger> triggers = new ArrayList<Trigger>();

        for (int i = 0; i < filters.size(); i++) {
            triggers.add(filters.get(filters.keyAt(i)).makeTrigger());
        }
        MessageReceiver.SetTriggers(triggers);

        configDatabase.close();
    }

    protected void onStart(Intent intent, int startId) {
        super.onStart();


    }
}
