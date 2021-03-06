package com.gin.xjh.shin_music.broadcastreceiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.gin.xjh.shin_music.R;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class PushReceiver extends BroadcastReceiver {

    private Notification.Builder builder = null;
    private Notification notification;
    private final int NOTIFICATION_ID = 233;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
                String msg = intent.getStringExtra("msg");
                JSONObject jsonObject = new JSONObject(msg);
                String title = jsonObject.getString("title");
                String content = jsonObject.getString("content");
                builder = new Notification.Builder(context);
                builder.setContentTitle(title)
                        .setContentText(content)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.notification_icon);//设置下拉图标

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder.setCategory(Notification.CATEGORY_PROGRESS)
                            .setVisibility(Notification.VISIBILITY_PUBLIC);
                }

                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelID = "update";
                    String channelName = "SHIN_Music_Update";
                    NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

                    manager.createNotificationChannel(channel);

                    builder.setChannelId(channelID);
                    notification = builder.build();
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    notification = builder.getNotification();
                } else {
                    notification = builder.build();
                }
                manager.notify(NOTIFICATION_ID, notification);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
