package com.baconworx.smsflash.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.baconworx.smsflash.receivers.MessageReceiver;

public class ReceiverService extends Service {
    public static void Start(Context context) {
        Intent serviceIntent = new Intent(context, ReceiverService.class);
        context.startService(serviceIntent);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        MessageReceiver messageReceiver = new MessageReceiver();
        IntentFilter smsReceivedFilter = new IntentFilter();
        smsReceivedFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

        registerReceiver(messageReceiver, smsReceivedFilter);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
