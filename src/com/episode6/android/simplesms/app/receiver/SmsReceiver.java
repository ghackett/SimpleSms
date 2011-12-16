package com.episode6.android.simplesms.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.episode6.android.simplesms.app.service.NotificationService;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Intent i = new Intent(context, NotificationService.class);
            i.putExtras(bundle);
            context.startService(i);
        }
    }

}
