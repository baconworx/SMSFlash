package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;

public class EditFilter extends Activity {
    private final int DEFAULT_COLOR = Color.RED;
    private final int DEFAULT_TIMEOUT = 5000;
    Filter filter;
    private ConfigDatabase configDatabase;
    private int filterId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_filter);

        configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        filterId = getIntent().getExtras().getInt("filterId", -1);

        if (filterId > -1) {

            filter = configDatabase.getFilter(filterId);

            ((TextView) findViewById(R.id.editTextName)).setText(filter.getName());
            ((TextView) findViewById(R.id.editTextCaption)).setText(filter.getCaption());
            ((TextView) findViewById(R.id.editTextPattern)).setText(filter.getPattern());
            ((TextView) findViewById(R.id.editTextReplacement)).setText(filter.getReplacement());
            ((TextView) findViewById(R.id.editTextSourceNumber)).setText(filter.getSourceNumber());
        } else {
            filter = new Filter();
            filter.setColor(DEFAULT_COLOR);
            filter.setTimeout(DEFAULT_TIMEOUT);
        }
    }

    @Override
    public void onBackPressed() {
        filter.setName(((TextView) findViewById(R.id.editTextName)).getText().toString());
        filter.setCaption(((TextView) findViewById(R.id.editTextCaption)).getText().toString());
        filter.setPattern(((TextView) findViewById(R.id.editTextPattern)).getText().toString());
        filter.setReplacement(((TextView) findViewById(R.id.editTextReplacement)).getText().toString());
        filter.setSourceNumber(((TextView) findViewById(R.id.editTextSourceNumber)).getText().toString());

        if (filterId > -1) {
            configDatabase.updateFilter(filterId, filter);
        } else {
            configDatabase.addFilter(filter);
        }

        configDatabase.close();

        finish();
        setResult(0);

        super.onBackPressed();
    }
}