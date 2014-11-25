package com.baconworx.smsflash.classes;

import com.baconworx.smsflash.db.Filter;
import com.baconworx.smsflash.db.Filterset;

public class FiltersListItem {
    private int id;
    private boolean isGroup;
    private String text;
    private boolean selected;

    public FiltersListItem(int id, String text, boolean isGroup) {
        this.id = id;
        this.isGroup = isGroup;
        this.text = text;
    }

    public static FiltersListItem fromFilter(Filter filter) {
        FiltersListItem item = new FiltersListItem(filter.getId(), filter.getName(), false);
        return item;
    }

    public static FiltersListItem fromFilterset(Filterset filter) {
        FiltersListItem item = new FiltersListItem(filter.getId(), filter.getName(), true);
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
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public void setSelected() { this.selected = !this.selected; }
}
