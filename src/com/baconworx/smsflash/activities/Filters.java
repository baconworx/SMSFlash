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
    private static final int EDIT_FILTERSET_REQUEST = 1;
    private static final int FILTERS_ACTIVITY_REQUEST = 2;

    List<FiltersListItem> listItems = new ArrayList<FiltersListItem>();
    private ArrayList<Integer> selectedFilters = null;
    private ArrayList<Integer> selectedFiltersets = null;

    private ActionMode.Callback mActionModeCallback;

    // adds filterset id to the extras of a given intent
    public static void SetFiltersetExtra(Intent intent, Bundle oldExtras) {
        if (oldExtras != null) {
            int filtersetId = oldExtras.getInt("filterset", -1);
            if (filtersetId != -1) intent.putExtra("filterset", filtersetId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filters);

        final ListView filtersListView = (ListView) findViewById(R.id.filtersListView);

        final FiltersListAdapter adapter = new FiltersListAdapter(this, listItems);
        filtersListView.setAdapter(adapter);

        updateList();

        // called on single click on the list, or after long-click returned false
        filtersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                FiltersListItem clickedItem = adapter.getItem(position);

                if (selectedFilters != null) {
                    // omfg selection mode!!! add clicked item to selection

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
                        // open new Filters activity if Group, displaying the filters belonging to this filterset

                        Intent openFilterIntent = new Intent(Filters.this, Filters.class);
                        int filtersetId = clickedItem.getId();
                        openFilterIntent.putExtra("filterset", filtersetId);
                        startActivityForResult(openFilterIntent, FILTERS_ACTIVITY_REQUEST);
                    } else {
                        // if not a filterset, spawns EditFilter activity

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
                        // trashbin clicked

                        ConfigDatabase configDatabase = new ConfigDatabase(Filters.this);
                        configDatabase.open();

                        // remove all selected filters, then filtersets
                        for (int selectedFilterId : selectedFilters)
                            configDatabase.deleteFilter(selectedFilterId);

                        for (int selectedFiltersetId : selectedFiltersets)
                            configDatabase.deleteFilterset(selectedFiltersetId);

                        configDatabase.close();

                        // selection mode abolished
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
        // after returning from an activity, we update our List and update the triggers

        switch (requestCode) {
            case EDIT_FILTER_REQUEST:
            case EDIT_FILTERSET_REQUEST:
            case FILTERS_ACTIVITY_REQUEST:
                updateList();
                MessageReceiver.SetTriggersFromDb(this);

                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // tell the list to repopulate
    private void refreshList() {
        ListView filtersListView = (ListView) findViewById(R.id.filtersListView);
        ((FiltersListAdapter) filtersListView.getAdapter()).notifyDataSetChanged();
    }

    // refetch from db
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

            for (int i = 0, filtersetsSize = filtersets.size(); i < filtersetsSize; i++) {
                key = filtersets.keyAt(i);
                filterset = filtersets.get(key);
                listItems.add(FiltersListItem.fromFilterset(filterset));
            }
        }

        // and add the filters (setless or for selected set)
        SparseArray<Filter> filters = configDatabase.getFilters(filtersetId);

        for (int i = 0, filtersSize = filters.size(); i < filtersSize; i++) {
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

        // if we're in a filterset, hide the "add filterset"-option
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer filtersetId = extras.getInt("filterset", -1);
            if (filtersetId != -1) menu.findItem(R.id.action_new_filterset).setVisible(false);
            else menu.findItem(R.id.action_edit_filterset).setVisible(false);

            // also, add the filterset name
            ConfigDatabase configDatabase = new ConfigDatabase(this);
            configDatabase.open();

            Filterset filterset = configDatabase.getFilterset(filtersetId);
            configDatabase.close();

            setTitle(filterset.getName());
        } else {
            menu.findItem(R.id.action_edit_filterset).setVisible(false);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Bundle extras = getIntent().getExtras();

        Intent intent; // can be used by any option (start yourself!)
        switch (id) {
            case R.id.action_new_filter:
                intent = new Intent(this, EditFilter.class);

                // set filterset for new filter, if set
                SetFiltersetExtra(intent, extras);

                startActivityForResult(intent, EDIT_FILTER_REQUEST);
                break;

            case R.id.action_new_filterset:
            case R.id.action_edit_filterset:
                intent = new Intent(this, EditFilterset.class);

                // set filterset for new filter, if set
                SetFiltersetExtra(intent, extras);

                startActivityForResult(intent, EDIT_FILTERSET_REQUEST);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
