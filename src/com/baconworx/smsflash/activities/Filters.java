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

import java.util.ArrayList;
import java.util.List;

public class Filters extends Activity {
    List<FiltersListItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);

        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);

        listItems = new ArrayList<FiltersListItem>();

        FiltersListAdapter adapter = new FiltersListAdapter(this, listItems);
        filtersListView.setAdapter(adapter);

        updateList();

        filtersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent editFilterIntent = new Intent(Filters.this, EditFilter.class);
                editFilterIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                int filterId = ((FiltersListAdapter.ViewHolderItem) view.getTag()).getId();
                editFilterIntent.putExtra("filterId", filterId);
                startActivity(editFilterIntent);
            }
        });
    }

    private void updateList() {
        ConfigDatabase configDatabase = new ConfigDatabase(this, true);
        configDatabase.open();

        int key;
        Filter filter;
        SparseArray<Filter> filters = configDatabase.getFilters(null);

        for (int i = 0; i < filters.size(); i++) {
            key = filters.keyAt(i);
            filter = filters.get(key);
            listItems.add(FiltersListItem.fromFilter(key, filter));
        }

        configDatabase.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateList();
        super.onActivityResult(requestCode, resultCode, data);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
