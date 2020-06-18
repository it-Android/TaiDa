package com.arcsoft.arcfacedemo.Notificatio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.arcsoft.arcfacedemo.R;


public class NotificationUtils extends ContextWrapper {
 
    private NotificationManager manager;
    public String id = "DelTa";
    public String name = "佛山职业技术学院";



    int argb = 0xffff0000;  // led灯光颜色
    int onMs = 400;         // led亮灯持续时间
    int offMs = 200;        // led熄灯持续时间
    private Intent intent;

    private PendingIntent pendingIntent ;

    public NotificationUtils(Context context, Intent intent){
        super(context);
        this.intent=intent;
        pendingIntent= PendingIntent.getActivity(context,0,intent,0);
//        this.id = "DelTa";
//        this.name="佛山职业技术学院";
    }

    public NotificationUtils(String  id, String name, Context context, Intent intent){
        super(context);
        this.intent=intent;
        this.id=id;
        this.name=name;
        pendingIntent= PendingIntent.getActivity(context,0,intent,0);
    }

 
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel(){
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setBypassDnd(true);
        channel.setShowBadge(true);
        getManager().createNotificationChannel(channel);
    }
    private NotificationManager getManager(){
        if (manager == null){
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getChannelNotification(String title, String content){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        return new Notification.Builder(getApplicationContext(), id)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())//显示通知的具体时间
                .setSmallIcon(R.mipmap.icon)//小图标，
                .setLargeIcon(bm)//下拉后观看到的图标
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public NotificationCompat.Builder getNotification_25(String title, String content){
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        return new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())//显示通知的具体时间
                .setPriority(NotificationCompat.PRIORITY_MAX)//通知的重要
                .setLargeIcon(bm)//下拉后观看到的图标
                .setSmallIcon(R.mipmap.icon)//小图标，
                .setContentIntent(pendingIntent)
                .setLights(argb, onMs, offMs)
                .setAutoCancel(true);
    }

    public void sendNotification(String title, String content,int idNum){
        if (Build.VERSION.SDK_INT>=26){
            createNotificationChannel();
            Notification notification = getChannelNotification
                    (title, content).build();
            getManager().notify(idNum,notification);
        }else{
            Notification notification = getNotification_25(title, content).build();
            getManager().notify(idNum,notification);
        }

        //震动
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        long[] patter = {1000, 1000, 1000,1000,1000,1000,1000};
        vibrator.vibrate(patter, -1);

    }

}