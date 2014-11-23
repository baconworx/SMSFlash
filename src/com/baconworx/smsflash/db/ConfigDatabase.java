package com.baconworx.smsflash.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.SparseArray;
import android.widget.Toast;

public class ConfigDatabase {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Context context;

    /* if test set to true, will delete database and insert some test data */
    private boolean test = false;


    public ConfigDatabase(Context context) {
        this(context, false);
    }

    public ConfigDatabase(Context context, boolean test) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
        this.test = test;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

        if (test) setupTest();
    }

    public void addFilter(Filter filter) {
        ContentValues values = new ContentValues();

        values.put("name", filter.getName());
        values.put("caption", filter.getCaption());
        values.put("pattern", filter.getPattern());
        values.put("replacement", filter.getReplacement());
        values.put("color", filter.getColor());
        values.put("sourceNumber", filter.getTimeout());
        values.put("filterset", filter.getFiltersetId());

        database.insert("filter", null, values);
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

        SparseArray<Filter> filters = new SparseArray<Filter>();
        Filter current;
        while (cursor.moveToNext()) {
            try {
                current = Filter.fromCursor(cursor);
                filters.append(cursor.getInt(cursor.getColumnIndex("id")), current);
            } catch (NullPointerException ex) {
            }

        }

        return filters;
    }

    public void close() {
        dbHelper.close();
    }

    private void setupTest() {
        database.delete("filter", null, null);

        Filter filter;

        filter = new Filter();
        filter.setCaption("TAC-SMS");
        filter.setPattern("^.*TAC:\\s([0-9]{4})$");
        filter.setReplacement("$1");
        filter.setName("Erste Bank");
        filter.setColor(Color.CYAN);
        filter.setTimeout(5000);
        addFilter(filter);

        filter = new Filter();
        filter.setCaption("smsTAN");
        filter.setPattern("^.*smsTAN\\s(?:is|lautet)\\s([a-z0-9]{7})\\s.*$");
        filter.setReplacement("$1");
        filter.setName("Raika");
        filter.setColor(Color.CYAN);
        filter.setTimeout(5000);
        addFilter(filter);
    }

    public Filter getFilter(Integer filterId) {
        Filter filter = null;

        Cursor cursor = database.query("filter", null, "id = ?", new String[]{filterId.toString()}, null, null, null);
        if (cursor.moveToNext()) {
            filter = Filter.fromCursor(cursor);
        }

        return filter;
    }

    public void updateFilter(int filterId, Filter filter) {
        Toast.makeText(context, Integer.toString(filterId), Toast.LENGTH_LONG).show();
        database.update("filter", filter.toContentValues(), "id = ?", new String[]{Integer.toString(filterId)});
    }
}
