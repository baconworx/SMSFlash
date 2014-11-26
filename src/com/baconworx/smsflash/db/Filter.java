package com.baconworx.smsflash.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;
import com.baconworx.smsflash.classes.Trigger;

public class Filter {
    private static final int COL_ID = 0;
    private static final int COL_NAME = 1;
    private static final int COL_CAPTION = 2;
    private static final int COL_PATTERN = 3;
    private static final int COL_REPLACEMENT = 4;
    private static final int COL_COLOR = 5;
    private static final int COL_SOURCENO = 6;
    private static final int COL_TIMEOUT = 7;
    private static final int COL_FILTERSET = 8;
    private Integer id;
    private String name;
    private String caption;
    private String pattern;
    private String replacement;
    private int color;
    private String sourceNumber;
    private int timeout;
    private Integer filtersetId;

    public static Filter fromCursor(Cursor cursor) {
        Filter filter = new Filter();
        filter.id = cursor.getInt(COL_ID);
        filter.name = cursor.getString(COL_NAME);
        filter.caption = cursor.getString(COL_CAPTION);
        filter.pattern = cursor.getString(COL_PATTERN);
        filter.replacement = cursor.getString(COL_REPLACEMENT);
        filter.color = cursor.getInt(COL_COLOR);
        filter.sourceNumber = cursor.getString(COL_SOURCENO);
        filter.timeout = cursor.getInt(COL_TIMEOUT);

        filter.filtersetId = cursor.getInt(COL_FILTERSET);
        filter.filtersetId = (filter.filtersetId == 0 ? null : filter.filtersetId);

        return filter;
    }

    public Integer getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public String getReplacement() { return replacement; }
    public void setReplacement(String replacement) { this.replacement = replacement; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public String getSourceNumber() { return sourceNumber; }
    public void setSourceNumber(String sourceNumber) { this.sourceNumber = TextUtils.isEmpty(sourceNumber) ? null : sourceNumber; }
    public int getTimeout() { return timeout; }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public Integer getFiltersetId() { return filtersetId; }
    public void setFiltersetId(Integer filtersetId) { this.filtersetId = filtersetId; }
    public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("caption", caption);
        contentValues.put("pattern", pattern);
        contentValues.put("replacement", replacement);
        contentValues.put("color", color);
        contentValues.put("sourceNumber", sourceNumber);
        contentValues.put("timeout", timeout);
        contentValues.put("filterset", filtersetId);

        return contentValues;
    }

    public Trigger makeTrigger() {
        Trigger trigger = new Trigger(pattern, replacement, caption, color, sourceNumber);
        trigger.setTimeout(timeout);
        return trigger;
    }
}
