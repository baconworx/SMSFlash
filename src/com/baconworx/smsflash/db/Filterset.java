package com.baconworx.smsflash.db;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by eun on 26.11.2014.
 */
public class Filterset {
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;

    private Integer id;
    private String name;

    public Filterset() {}
    public Filterset(String name) { this.name = name; }
    public static Filterset fromCursor(Cursor cursor) {
        Filterset filterset = new Filterset();

        filterset.id = cursor.getInt(COL_ID);
        filterset.name = cursor.getString(COL_NAME);

        return filterset;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);

        return contentValues;
    }
}
