package com.baconworx.smsflash.receivers;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.SparseArray;
import com.baconworx.smsflash.activities.FlashDisplay;
import com.baconworx.smsflash.classes.DisplayMessageData;
import com.baconworx.smsflash.classes.Trigger;
import com.baconworx.smsflash.db.ConfigDatabase;
import com.baconworx.smsflash.db.Filter;

import java.util.ArrayList;
import java.util.List;

public class MessageReceiver extends BroadcastReceiver {
    private static List<Trigger> triggers = new ArrayList<Trigger>();


    public MessageReceiver() {
        super();
    }
    public static void SetTriggers(List<Trigger> triggers) { MessageReceiver.triggers = triggers; }
    public static void SetTriggersFromDb(Context context) {
        ConfigDatabase configDatabase = new ConfigDatabase(context);
        configDatabase.open();

        SparseArray<Filter> filters = configDatabase.getFiltersFlat();
        List<Trigger> triggers = new ArrayList<Trigger>();

        for (int i = 0; i < filters.size(); i++) {
            triggers.add(filters.get(filters.keyAt(i)).makeTrigger());
        }
        SetTriggers(triggers);

        configDatabase.close();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        DisplayMessageData displayMessageData = null;
        StringBuilder msgBuilder = new StringBuilder();

        Intent displayMessageIntent = new Intent(context, FlashDisplay.class);
        displayMessageIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String messageBody;

            for (Object pdu : pdus) {
                try {
                    msg = SmsMessage.createFromPdu((byte[]) pdu);
                    msgBuilder.append(msg.getDisplayMessageBody());
                } catch (Exception ignored) {
                    // msg unparsable
                }
            }

            int threadId = 0;
            if (msg != null) {
                /* find SMS id */
                ContentResolver contentResolver = context.getContentResolver();
                final String[] projection = new String[]{"thread_id"};
                Uri uri = Uri.parse("content://sms");
                Cursor query = contentResolver.query(
                        uri,
                        projection,
                        "address=?",
                        new String[]{msg.getOriginatingAddress()},
                        null);

                query.moveToNext();
                threadId = query.getInt(0);

                for (Trigger trigger : triggers) {
                    messageBody = msgBuilder.toString();
                    displayMessageData = trigger.match(msg.getDisplayOriginatingAddress(), messageBody);

                    if (displayMessageData != null)
                        break;
                }
            }

            if (displayMessageData != null) {
                displayMessageIntent.putExtra("caption",
                        displayMessageData.getCaption());
                displayMessageIntent.putExtra("text",
                        displayMessageData.getDisplayText());
                displayMessageIntent.putExtra("backgroundColor",
                        displayMessageData.getBackgroundColor());
                displayMessageIntent.putExtra("timeout",
                        displayMessageData.getTimeout());
                displayMessageIntent.putExtra("threadId",
                        threadId);
                context.startActivity(displayMessageIntent);
            }
        }
    }
}
