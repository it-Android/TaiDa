package com.arcsoft.arcfacedemo.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arcsoft.arcfacedemo.Notificatio.NotificationUtils;
import com.arcsoft.arcfacedemo.Service.FaceDownloadService;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.ServerData;
import com.arcsoft.arcfacedemo.Tool.Json.Analysis.JsonDataAnalysis;
import com.arcsoft.arcfacedemo.activity.ControlActivity;

public class UpDateReceiver extends BroadcastReceiver {

    private Handler handler;
    private Boolean isFlame=false,isSmoke=false;
    public UpDateReceiver(Handler handler) {
        this.handler = handler;
    }

    private NotificationUtils notificationUtils;//通知
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent ine=new Intent(context,ControlActivity.class);//传递通知，点击通知后跳转到 ControlActivity 界面
        notificationUtils=new NotificationUtils(context,ine);//初始化
        try {//获取srvice 传过来的数据
            Bundle bundle=intent.getExtras();
            byte []serverData=bundle.getByteArray("data");
            sorting(serverData);//分捡
        } catch (Exception e) {
        }
    }


    //分捡，判断类型
    private void sorting(byte[] serverData){
        Message msg=new Message();
        try{
            switch (serverData[0]){
                case (byte) 0x01://是字符串
                    String serverStr=new String(serverData,1,serverData.length-1,"UTF-8");
                    msg.what=HandlerNumberRecord.SERVERDATA_0;//字符串用，0
                    msg.obj=serverStr;
                    handler.sendMessage(msg);
                    try { abnormal(serverStr); }catch (Exception e){}
                    break;
                case (byte)0x02://是文件
                    msg.what=HandlerNumberRecord.SERVERDATA_2;//文件用，2
                    msg.obj=serverData;
                    handler.sendMessage(msg);
                    break;
                case (byte)0x03:
                    msg.what=HandlerNumberRecord.DISCONNECT_10000;//连接断开，10000
                    msg.obj=true;
                    handler.sendMessage(msg);
                    Log.w("UpDateReceiver数据监听","连接已断开");
                    break;
                    default:break;
            }

        }catch (Exception e){ }

    }

    //判断知否有异常
    private void abnormal(String serverStr){
        ServerData serverData=JsonDataAnalysis.getServerData(serverStr);

        //异常为1，正常为0
        if(serverData.getFlame()==1){
            if(!isFlame){
                notificationUtils.sendNotification("异常","发现火焰异常！",1);
            }
            isFlame=true;
        }else{
            isFlame=false;
        }
        if(serverData.getSmoke()==1){
            if(!isSmoke){
                notificationUtils.sendNotification("异常","发现烟雾异常！",2);
            }
            isSmoke=true;
        }else  if(serverData.getSmoke()==0){
            isSmoke=false;
        }

    }



}