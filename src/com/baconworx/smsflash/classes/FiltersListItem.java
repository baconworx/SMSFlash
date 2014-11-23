package com.baconworx.smsflash.classes;

import com.baconworx.smsflash.db.Filter;

public class FiltersListItem {
    private int id;
    private boolean isGroup;
    private String text;

    public FiltersListItem(int id, String text, boolean isGroup) {
        this.id = id;
        this.isGroup = isGroup;
        this.text = text;
    }
    public static FiltersListItem fromFilter(int id, Filter filter) {
        FiltersListItem item = new FiltersListItem(id, filter.getName(), filter.getFiltersetId() == null);
        return item;
    }
    public boolean isGroup() {
        return isGroup;
    }
    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}
