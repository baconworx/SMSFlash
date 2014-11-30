package com.baconworx.smsflash.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.SparseArray;

public class ConfigDatabase {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    /* if test set to true, will delete database and insert some test data */
    private boolean test = false;

    public ConfigDatabase(Context context) {
        this(context, false);
    }

    public ConfigDatabase(Context context, boolean test) {
        dbHelper = new DatabaseHelper(context);
        this.test = test;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        if (test) setupTest();
    }

    public void addFilter(Filter filter) {
        int newFilterId = (int) database.insert("filter", null, filter.toContentValues());
        filter.setId(newFilterId);
    }

    public void addFilterset(Filterset filterset) {
        int newFilterIdset = (int) database.insert("filterset", null, filterset.toContentValues());
        filterset.setId(newFilterIdset);
    }

    public SparseArray<Filter> getFilters() {
        return getFilters(null);
    }

    public SparseArray<Filter> getFilters(Integer filterset) {
        Cursor cursor;

        if (filterset == null) {
            cursor = database.query("filter", null, "filterset is null", null,
                    null, null, null);
        } else {
            cursor = database.query("filter", null, "filterset = ?",
                    new String[]{filterset.toString()}, null, null, null);
        }

        return getFiltersFromCursor(cursor);
    }

    public SparseArray<Filter> getFiltersFlat() {
        Cursor cursor;

        cursor = database.query("filter", null, null, null, null, null, null);
        return getFiltersFromCursor(cursor);
    }

    private SparseArray<Filter> getFiltersFromCursor(Cursor cursor) {
        SparseArray<Filter> filters = new SparseArray<Filter>();
        Filter current;
        while (cursor.moveToNext()) {
            current = Filter.fromCursor(cursor);
            filters.append(cursor.getInt(cursor.getColumnIndex("id")), current);
        }

        return filters;
    }

    public SparseArray<Filterset> getFiltersets() {
        Cursor cursor;
        cursor = database.rawQuery("select * from filterset", null);

        SparseArray<Filterset> filtersets = new SparseArray<Filterset>();
        Filterset current;
        while (cursor.moveToNext()) {
            current = Filterset.fromCursor(cursor);
            filtersets.append(cursor.getInt(cursor.getColumnIndex("id")), current);
        }

        return filtersets;
    }

    public void close() {
        dbHelper.close();
    }

    private void setupTest() {
        database.delete("filter", null, null);
        database.delete("filterset", null, null);

        Filter filterErste;

        filterErste = new Filter();
        filterErste.setCaption("TAC-SMS");
        filterErste.setPattern("^.*TAC:\\s([0-9]{4})$");
        filterErste.setReplacement("$1");
        filterErste.setName("Erste Bank");
        filterErste.setColor(Color.CYAN);

        Filter filterRaika;

        filterRaika = new Filter();
        filterRaika.setCaption("smsTAN");
        filterRaika.setPattern("^.*smsTAN\\s(?:is|lautet)\\s([a-z0-9]{7})\\s.*$");
        filterRaika.setReplacement("$1");
        filterRaika.setName("Raika");
        filterRaika.setColor(Color.CYAN);

        Filterset filterset = new Filterset("Banken");
        addFilterset(filterset);

        filterErste.setFiltersetId(filterset.getId());
        addFilter(filterErste);
        filterRaika.setFiltersetId(filterset.getId());
        addFilter(filterRaika);

        filterErste.setFiltersetId(null);
        addFilter(filterErste);
        filterRaika.setFiltersetId(null);
        addFilter(filterRaika);
    }

    public Filter getFilter(Integer filterId) {
        Filter filter = null;

        Cursor cursor = database.query("filter", null, "id = ?", new String[]{filterId.toString()}, null, null, null);
        if (cursor.moveToNext()) {
            filter = Filter.fromCursor(cursor);
        }

        return filter;
    }

    public void updateFilter(Filter filter) {
        database.update("filter", filter.toContentValues(), "id = ?", new String[]{filter.getId().toString()});
    }
    public void deleteFilter(int selectedFilterId) {
        database.delete("filter", "id = ?", new String[]{Integer.toString(selectedFilterId)});
    }
    public void deleteFilterset(Integer selectedFiltersetId) {
        if (selectedFiltersetId != null) {
            // clean all containing filters
            SparseArray<Filter> filters = getFilters(selectedFiltersetId);
            for (int i = 0, filtersSize = filters.size(); i < filtersSize; i++)
                deleteFilter(filters.get(filters.keyAt(i)).getId());

            // delete filterset
            database.delete("filterset", "id = ?", new String[]{selectedFiltersetId.toString()});
        }
    }
    public Filterset getFilterset(int filtersetId) {
        Cursor cursor;
        cursor = database.query("filterset", null, "id = ?", new String[]{Integer.toString(filtersetId)}, null, null, null);

        Filterset filterset = null;
        if (cursor.moveToNext()) filterset = Filterset.fromCursor(cursor);

        return filterset;
    }
    public void updateFilterset(Filterset filterset) {
        database.update("filterset", filterset.toContentValues(), "id = ?", new String[]{filterset.getId().toString()});
    }
}
