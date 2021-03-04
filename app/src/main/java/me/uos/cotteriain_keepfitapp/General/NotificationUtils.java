package me.uos.cotteriain_keepfitapp.General;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import me.uos.cotteriain_keepfitapp.MainActivity;
import me.uos.cotteriain_keepfitapp.R;

public class NotificationUtils {
    private static final int NOTIFICATION_ID = R.integer.notification_id;
    private static final int PENDING_ID = R.integer.pending_id;
    private static final String NOTIFICATION_CHANNEL = "keepfit_channel_id";
    private static final String LIFECYCLE_CALLBACKS = "keepfit_callbacks";

    public NotificationUtils(Context context, int percent) {
        updateUserOnProgress(context, percent);
    }

    private static void updateUserOnProgress(Context context, int percent){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL,context.getString(R.string.notification_channel), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setColor(ContextCompat.getColor(context, R.color.red))
                .setSmallIcon(R.drawable.activity_icon)
                .setContentTitle("Keep Fit")
                .setContentText("Have reached " + percent + "% of your goal!")
                .setContentIntent(openActivity(context));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent openActivity(Context context){
        Intent startActivity = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, PENDING_ID, startActivity, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
