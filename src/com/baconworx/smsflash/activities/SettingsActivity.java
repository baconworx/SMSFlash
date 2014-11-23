package com.baconworx.smsflash.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.baconworx.smsflash.R;

public class SettingsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
