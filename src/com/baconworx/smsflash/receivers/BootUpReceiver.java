package com.baconworx.smsflash.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.baconworx.smsflash.services.ReceiverService;

public class BootUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ReceiverService.Start(context);
    }
}
