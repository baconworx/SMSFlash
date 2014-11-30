package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.os.Bundle;
import com.baconworx.smsflash.receivers.MessageReceiver;
import com.baconworx.smsflash.settings.MainSettings;

public class MainApp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start the service, if not running
        com.baconworx.smsflash.services.ReceiverService.Start(this);

        // get the trigger from db and listen for matches
        MessageReceiver.SetTriggersFromDb(this);

        // Load the preferences from an XML resource
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainSettings()).commit();
    }
}
