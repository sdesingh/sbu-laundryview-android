package com.cptmango.sbu_laundryview.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cptmango.sbu_laundryview.R;
import com.cptmango.sbu_laundryview.activities.HomeScreen;

public class NotifyUser extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 1000, 250, 1000, 250, 1000, 250, 1000, 250, 1000, 250};

        int machineNumber = intent.getIntExtra("machineNumber", 0);
        String roomName = intent.getStringExtra("roomName");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomeScreen.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "main");
        builder.setContentTitle("Machine " + machineNumber + " will be ready soon!");
        builder.setContentText("Your laundry will be complete in two minutes.");
//        Drawable icon = (Drawable) context.getDrawable(R.drawable.icon_water);
//        icon.setTint(context.getResources().getColor(R.color.White));
        //@TODO Change icon for the Notification.
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setColor(context.getResources().getColor(R.color.White));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE |  Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        Log.i("LOG", "Creating user notification.");
        notificationManager.notify(machineNumber, notification);

    }
}
