package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filterset;

/**
 * Created by eun on 26.11.2014.
 */
public class EditFilterset extends Activity {
    Filterset filterset;
    private ConfigDatabase configDatabase;
    private int filtersetId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_filterset);

        configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        Bundle extras = getIntent().getExtras();
        filtersetId = extras != null ? extras.getInt("filterset", -1) : -1;

        if (filtersetId > -1) {
            filterset = configDatabase.getFilterset(filtersetId);
            ((TextView) findViewById(R.id.editTextName)).setText(filterset.getName());
        } else {
            filterset = new Filterset();
        }
    }

    @Override
    public void onBackPressed() {
        filterset.setName(((TextView) findViewById(R.id.editTextName)).getText().toString());


        if (filtersetId > -1) {
            configDatabase.updateFilterset(filterset);
        } else {
            configDatabase.addFilterset(filterset);
        }

        configDatabase.close();

        finish();
        setResult(0);

        super.onBackPressed();
    }
}