package com.baconworx.smsflash.classes;

import android.telephony.PhoneNumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trigger {
    private Pattern pattern;
    private String replacement;
    private int backgroundColor;
    private String caption;
    private String sourceNumber;
    private int timeout;
    private boolean caseSensitive = false;

    public Trigger() { }

    public Trigger(String regex, String replacement, String caption,
                   int backgroundColor, String sourceNumber) {
        if (caseSensitive) this.pattern = Pattern.compile(regex);
        else this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        this.replacement = replacement;
        this.caption = caption;
        this.backgroundColor = backgroundColor;
        this.sourceNumber = sourceNumber;
        this.timeout = 0;
    }
    public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }
    public String getRegex() {
        return pattern.pattern();
    }

    public void setRegex(String regex) {
        if (caseSensitive) this.pattern = Pattern.compile(regex);
        else this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    }

    public String getReplacement() {
        return replacement;
    }
    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
    public int getBackgroundColor() {
        return backgroundColor;
    }
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) { this.timeout = timeout; }
    public String getSourceNumber() {
        return sourceNumber;
    }
    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }
    public DisplayMessageData match(String sourceNumber, String message) {
        DisplayMessageData displayMessageData = null;

        Matcher matcher = pattern.matcher(message);
        if (matcher.find() && (this.sourceNumber == null || PhoneNumberUtils.compare(this.sourceNumber, sourceNumber))) {
            String displayText = matcher.replaceAll(replacement);
            displayMessageData = new DisplayMessageData(caption, backgroundColor, displayText, timeout);
        }
        return displayMessageData;
    }
}
