package com.arcsoft.arcfacedemo.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.System.Downloadfiles;

public class FaceDownloadService extends Service {

    private Downloadfiles downloadfiles;
    @Override
    public void onCreate() {
        super.onCreate();
        downloadfiles=new Downloadfiles(FaceDownloadService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        downloadfiles.writeFile(sp.getString(OverallSituation.USER_IP,""));//写入数据
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}
