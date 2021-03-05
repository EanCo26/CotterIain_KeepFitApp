package me.uos.cotteriain_keepfitapp.General;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import me.uos.cotteriain_keepfitapp.HistoryActivity;
import me.uos.cotteriain_keepfitapp.MainActivity;
import me.uos.cotteriain_keepfitapp.R;

public class MyNotification {
    private static final int PROGRESS_ID = R.integer.notification_id;
    private static final int HISTORY_ID = R.integer.notification_id;

    private static final int PENDING_ID = R.integer.pending_id;

    private static final String PROGRESS_CHANNEL = "keepfit_channel_id";
    private static final String HISTORY_CHANNEL = "keepfit_channel_id_history";

    private static final String LIFECYCLE_CALLBACKS = "keepfit_callbacks";

    public MyNotification(Context context, String msg) {
        progressNotification(context, msg);
    }

    public MyNotification(Context context) {
        historyNotification(context);
    }

    private static void progressNotification(Context context, String msg){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(PROGRESS_CHANNEL, context.getString(R.string.progress_channel), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb = notificationBuilder(context, msg, PROGRESS_CHANNEL, MainActivity.class);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            nb.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(PROGRESS_ID, nb.build());
    }

    private static void historyNotification(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(HISTORY_CHANNEL, context.getString(R.string.history_channel), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder nb = notificationBuilder(context, "History was Updated", HISTORY_CHANNEL, HistoryActivity.class);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            nb.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(HISTORY_ID, nb.build());
    }

    private static NotificationCompat.Builder notificationBuilder(Context context, String msg, String channel, Class<? extends Activity> activityClass){
        return new NotificationCompat.Builder(context, channel)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setSmallIcon(R.drawable.activity_icon)
                .setContentTitle("Keep Fit")
                .setContentText(msg)
                .setContentIntent(openActivity(context, activityClass));
    }

    private static PendingIntent openActivity(Context context, Class<? extends Activity> activityClass){
        Intent startActivity = new Intent(context, activityClass);
        return PendingIntent.getActivity(context, PENDING_ID, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
