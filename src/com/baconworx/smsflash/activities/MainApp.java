package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.os.Bundle;
import com.baconworx.smsflash.settings.MainSettings;

public class MainApp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.baconworx.smsflash.services.ReceiverService.Start(this);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettings()).commit();
    }
}
