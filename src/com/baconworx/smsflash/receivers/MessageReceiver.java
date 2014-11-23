package com.baconworx.smsflash.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.baconworx.smsflash.activities.FlashDisplay;
import com.baconworx.smsflash.classes.DisplayMessageData;
import com.baconworx.smsflash.classes.Trigger;

import java.util.ArrayList;
import java.util.List;

public class MessageReceiver extends BroadcastReceiver {
    private static List<Trigger> triggers = new ArrayList<Trigger>();

    public MessageReceiver() {
        super();
    }
    public static void SetTriggers(List<Trigger> triggers) { MessageReceiver.triggers = triggers; }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        DisplayMessageData displayMessageData = null;
        StringBuilder msgBuilder = new StringBuilder();

        Intent displayMessageIntent = new Intent(context, FlashDisplay.class);
        displayMessageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String messageBody;

            for (int i = 0; i < pdus.length; i++) {
                msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
                msgBuilder.append(msg.getDisplayMessageBody());
            }

            for (Trigger trigger : triggers) {
                messageBody = msgBuilder.toString();
                displayMessageData = trigger.match(
                        msg.getDisplayOriginatingAddress(), messageBody);

                if (displayMessageData != null)
                    break;
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
                context.startActivity(displayMessageIntent);
            }
        }
    }
}
