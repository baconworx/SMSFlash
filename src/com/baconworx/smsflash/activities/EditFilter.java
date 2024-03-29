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
    Filter filter;
    private ConfigDatabase configDatabase;
    private int filterId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_filter);

        configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        Bundle extras = getIntent().getExtras();
        filterId = extras != null ? extras.getInt("filterId", -1) : -1;

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
        }
    }

    @Override
    public void onBackPressed() {
        filter.setName(((TextView) findViewById(R.id.editTextName)).getText().toString());
        filter.setCaption(((TextView) findViewById(R.id.editTextCaption)).getText().toString());
        filter.setPattern(((TextView) findViewById(R.id.editTextPattern)).getText().toString());
        filter.setReplacement(((TextView) findViewById(R.id.editTextReplacement)).getText().toString());
        filter.setSourceNumber(((TextView) findViewById(R.id.editTextSourceNumber)).getText().toString());

        // set filterset for new filter, if set
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int filtersetId = extras.getInt("filterset", -1);

            if (filtersetId != -1) filter.setFiltersetId(filtersetId);
        }

        if (filterId > -1) {
            configDatabase.updateFilter(filter);
        } else {
            configDatabase.addFilter(filter);
        }

        configDatabase.close();

        finish();
        setResult(0);

        super.onBackPressed();
    }
}