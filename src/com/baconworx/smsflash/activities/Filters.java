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
import com.baconworx.smsflash.receivers.MessageReceiver;

import java.util.ArrayList;
import java.util.List;

public class Filters extends Activity {
    private static final int EDIT_FILTER_REQUEST = 0;
    List<FiltersListItem> listItems = new ArrayList<FiltersListItem>();
    private ArrayList<Integer> selectedItems = null;

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
                if (selectedItems != null) {
                    FiltersListItem clickedItem = adapter.getItem(position);
                    clickedItem.setSelected();
                    if (clickedItem.isSelected()) selectedItems.add(clickedItem.getId());
                    else selectedItems.remove(selectedItems.indexOf(clickedItem.getId()));
                    refreshList();
                } else {
                    Intent editFilterIntent = new Intent(Filters.this, EditFilter.class);
                    int filterId = ((FiltersListAdapter.ViewHolderItem) view.getTag()).getId();
                    editFilterIntent.putExtra("filterId", filterId);
                    startActivityForResult(editFilterIntent, EDIT_FILTER_REQUEST);
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

                        for (int selectedFilterId : selectedItems)
                            configDatabase.deleteFilter(selectedFilterId);

                        configDatabase.close();
                        selectedItems = null;

                        updateList();
                        MessageReceiver.SetTriggersFromDb(Filters.this);

                        mode.finish();
                        return true;
                    default:
                        mode.finish();
                        return false;
                }
            }

            // Called when the user exits the action mode
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                selectedItems = null;
                clearSelection();
            }
        };

        filtersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedItems != null) {
                    return false;
                }

                selectedItems = new ArrayList<Integer>();

                // Start the CAB using the ActionMode.Callback defined above
                FiltersListItem selectedFilter = (FiltersListItem) parent.getItemAtPosition(position);
                if (selectedFilter != null) {
                    startActionMode(mActionModeCallback);
                    return false; // pass to onClick for selection of long-clicked item
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
                startActivityForResult(openEditFilterIntent, 0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
