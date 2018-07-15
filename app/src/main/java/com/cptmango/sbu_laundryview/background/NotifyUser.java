package com.cptmango.sbu_laundryview.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.support.v4.app.NotificationCompat;

import com.cptmango.sbu_laundryview.R;

import static com.cptmango.sbu_laundryview.R.color.White;

public class NotifyUser extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        final long[] DEFAULT_VIBRATE_PATTERN = {0, 1000, 250, 1000, 250, 1000, 250, 1000, 250, 1000, 250};

        System.out.println("THIS WAS CALLED!");
        int machineNumber = intent.getIntExtra("machineNumber", 0);
        String roomName = intent.getStringExtra("roomName");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "main");
        builder.setContentTitle("Machine " + machineNumber + " is now ready!");
        builder.setContentText("Please retrieve your laundry as soon as possible.");
//        Drawable icon = (Drawable) context.getDrawable(R.drawable.icon_water);
//        icon.setTint(context.getResources().getColor(R.color.White));

        builder.setSmallIcon(R.drawable.icon_water);
        builder.setColor(context.getResources().getColor(R.color.White));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setVibrate(DEFAULT_VIBRATE_PATTERN);
        Notification notification = builder.build();
        notification.defaults = Notification.FLAG_ONLY_ALERT_ONCE |  Notification.DEFAULT_LIGHTS;

        notificationManager.notify(machineNumber, notification);


    }
}
