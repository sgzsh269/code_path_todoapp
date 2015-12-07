package com.example.android.todoapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.cancelAlarm(context);
        MainActivity.scheduleAlarm(context);
    }
}
