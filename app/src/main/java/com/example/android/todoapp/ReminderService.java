package com.example.android.todoapp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.example.android.todoapp.data.TodoAppDBHelper;
import com.example.android.todoapp.data.TodoItem;

import java.util.ArrayList;

public class ReminderService extends IntentService {

    private TodoAppDBHelper mTodoAppDBHelper;

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mTodoAppDBHelper = TodoAppDBHelper.getInstance(this);

        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        generateReminders();
    }

    private void createNotification(long dbId, String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.OPEN_ADD_EDIT_FRAGMENT);
        intent.putExtra("dbId", dbId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_todo_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify((int) dbId, mBuilder.build());
    }

    private void generateReminders() {
        ArrayList<TodoItem> todoItems = mTodoAppDBHelper.getReminderItems();
        for (TodoItem todoItem : todoItems) {
            String title = "to do: " + todoItem.getPriority() + ", in " + todoItem.getTimeleft();
            createNotification(todoItem.getDbId(), title, todoItem.getDesc());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


