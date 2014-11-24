package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.classes.FiltersListAdapter;
import com.baconworx.smsflash.classes.FiltersListItem;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;
import com.baconworx.smsflash.receivers.MessageReceiver;

import java.util.ArrayList;
import java.util.List;

public class Filters extends Activity {
    private static final int EDIT_FILTER_REQUEST = 0;
    List<FiltersListItem> listItems = new ArrayList<FiltersListItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);

        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);

        FiltersListAdapter adapter = new FiltersListAdapter(this, listItems);
        filtersListView.setAdapter(adapter);

        updateList();

        filtersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent editFilterIntent = new Intent(Filters.this, EditFilter.class);
                int filterId = ((FiltersListAdapter.ViewHolderItem) view.getTag()).getId();
                editFilterIntent.putExtra("filterId", filterId);
                startActivityForResult(editFilterIntent, EDIT_FILTER_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case EDIT_FILTER_REQUEST:
                updateList();
                MessageReceiver.SetTriggersFromDb(this);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateList() {
        ConfigDatabase configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        int key;
        Filter filter;
        SparseArray<Filter> filters = configDatabase.getFilters(null);

        listItems.clear();
        for (int i = 0; i < filters.size(); i++) {
            key = filters.keyAt(i);
            filter = filters.get(key);
            listItems.add(FiltersListItem.fromFilter(key, filter));
        }

        configDatabase.close();

        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);
        ((FiltersListAdapter) filtersListView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}
