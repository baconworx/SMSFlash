package com.baconworx.smsflash.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.baconworx.smsflash.R;
import com.baconworx.smsflash.classes.FiltersListAdapter;
import com.baconworx.smsflash.classes.FiltersListItem;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;
import com.baconworx.smsflash.db.Filterset;
import com.baconworx.smsflash.receivers.MessageReceiver;

import java.util.ArrayList;
import java.util.List;

public class Filters extends Activity {
    private static final int EDIT_FILTER_REQUEST = 0;
    List<FiltersListItem> listItems = new ArrayList<FiltersListItem>();
    private ArrayList<Integer> selectedFilters = null;
    private ArrayList<Integer> selectedFiltersets = null;

    private ActionMode.Callback mActionModeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);

        final ListView filtersListView = (ListView) findViewById(R.id.filtersListView);

        final FiltersListAdapter adapter = new FiltersListAdapter(this, listItems);
        filtersListView.setAdapter(adapter);

        updateList();

        filtersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FiltersListItem clickedItem = adapter.getItem(position);

                if (selectedFilters != null) {
                    clickedItem.setSelected();

                    if (clickedItem.isSelected()) {
                        if (clickedItem.isGroup())
                            selectedFiltersets.add(clickedItem.getId());
                        else
                            selectedFilters.add(clickedItem.getId());
                    } else {
                        if (clickedItem.isGroup())
                            selectedFiltersets.remove(selectedFiltersets.indexOf(clickedItem.getId()));
                        else
                            selectedFilters.remove(selectedFilters.indexOf(clickedItem.getId()));
                    }
                    refreshList();
                } else {
                    if (clickedItem.isGroup()) {
                        Intent openFilterIntent = new Intent(Filters.this, Filters.class);
                        int filtersetId = clickedItem.getId();
                        openFilterIntent.putExtra("filterset", filtersetId);
                        startActivity(openFilterIntent);
                    } else {
                        Intent editFilterIntent = new Intent(Filters.this, EditFilter.class);
                        int filterId = clickedItem.getId();
                        editFilterIntent.putExtra("filterId", filterId);
                        startActivityForResult(editFilterIntent, EDIT_FILTER_REQUEST);
                    }
                }
            }
        });

        mActionModeCallback = new ActionMode.Callback() {
            // Called when the action mode is created; startActionMode() was called
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate a menu resource providing context menu items
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.filters_context, menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            // Called when the user selects a contextual menu item
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.filters_context_delete:
                        ConfigDatabase configDatabase = new ConfigDatabase(Filters.this);
                        configDatabase.open();

                        for (int selectedFilterId : selectedFilters)
                            configDatabase.deleteFilter(selectedFilterId);

                        for (int selectedFiltersetId : selectedFiltersets)
                            configDatabase.deleteFilterset(selectedFiltersetId);

                        configDatabase.close();
                        selectedFilters = null;
                        selectedFiltersets = null;

                        updateList();
                        MessageReceiver.SetTriggersFromDb(Filters.this);

                        // no breakerino! pass on!! >>>>>>>>>>>>
                    default:
                        mode.finish();
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedFilters = null;
                clearSelection();
            }
        }
        ;

        filtersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedFilters != null) {
                    return false; // our good boy onItemClick will handle this
                }

                selectedFilters = new ArrayList<Integer>();
                selectedFiltersets = new ArrayList<Integer>();

                // start the CAB using the ActionMode.Callback defined above
                FiltersListItem selectedFilter = (FiltersListItem) parent.getItemAtPosition(position);
                if (selectedFilter != null) {
                    startActionMode(mActionModeCallback);
                    return false; // pass to our friend onItemClick for selection of long-clicked item
                }

                return false;
            }
        });
    }

    private void clearSelection() {
        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);
        ((FiltersListAdapter) filtersListView.getAdapter()).clearSelection();
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

    private void refreshList() {
        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);
        ((FiltersListAdapter) filtersListView.getAdapter()).notifyDataSetChanged();
    }

    private void updateList() {
        // clear list
        listItems.clear();


        ConfigDatabase configDatabase = new ConfigDatabase(this);
        configDatabase.open();

        // if filterset was set for this intent, we only get those, otherwise all
        Bundle extras = getIntent().getExtras();
        Integer filtersetId = null;
        if (extras != null) {
            filtersetId = extras.getInt("filterset", -1);
            filtersetId = filtersetId != -1 ? filtersetId : null;
        }

        int key;
        Filter filter;

        // if we're at top view, we add the filtersets too
        Filterset filterset;
        if (filtersetId == null) {
            SparseArray<Filterset> filtersets = configDatabase.getFiltersets();

            for (int i = 0; i < filtersets.size(); i++) {
                key = filtersets.keyAt(i);
                filterset = filtersets.get(key);
                listItems.add(FiltersListItem.fromFilterset(filterset));
            }
        }

        // and add the filters (setless or for selected set)
        SparseArray<Filter> filters = configDatabase.getFilters(filtersetId);

        for (int i = 0; i < filters.size(); i++) {
            key = filters.keyAt(i);
            filter = filters.get(key);
            listItems.add(FiltersListItem.fromFilter(filter));
        }


        configDatabase.close();

        refreshList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_filter:
                Intent openEditFilterIntent = new Intent(this, EditFilter.class);

                // set filterset for new filter, if set
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    int filtersetId = extras.getInt("filterset", -1);

                    if (filtersetId != -1) openEditFilterIntent.putExtra("filterset", filtersetId);
                }

                startActivityForResult(openEditFilterIntent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
