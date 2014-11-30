package com.baconworx.smsflash.classes;

public class DisplayMessageData {
    private String caption;
    private int backgroundColor;
    private String displayText;

    public DisplayMessageData(String caption, int backgroundColor, String displayText) {
        this.caption = caption;
        this.backgroundColor = backgroundColor;
        this.displayText = displayText;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) { this.caption = caption; }
    public int getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }
    public String getDisplayText() { return displayText; }
    public void setDisplayText(String displayText) { this.displayText = displayText; }
}
