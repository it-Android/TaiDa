package com.arcsoft.arcfacedemo.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BackstageSockectServices extends Service {
    public final static String CONNECTION_FAILED="连接失败！";
    public final static String SUCCESSFULL_CONNECTION="客户端连接成功！";
    private Thread thread;
    private Socket socket;
    private BufferedReader bufferedReader=null;//缓存
    private InputStream inputStream;
    private OutputStream outputStream;//声明输出流对象
    private Boolean threadStart=true,success=false;//保存线程是否执行死循环
    @Override
    public IBinder onBind(Intent intent) {
        Intent intentRunnable=intent;
        String IP=intentRunnable.getStringExtra(OverallSituation.USER_IP);
        int Port=Integer.parseInt(intentRunnable.getStringExtra(OverallSituation.USER_PORT));
        threadInit(IP,Port);
        return new MyServicesBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent intentRunnable=intent;
        String IP=intentRunnable.getStringExtra(OverallSituation.USER_IP);
        int Port=Integer.parseInt(intentRunnable.getStringExtra(OverallSituation.USER_PORT));
        threadInit(IP,Port);
        return super.onStartCommand(intent, flags, startId);
    }

    public void threadInit(final String ip, final int port){
        thread=new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            socket=new Socket();
                            socket.connect(new InetSocketAddress(ip, port),500);//设置连接请求超时时间1s
                            //socket.setSoTimeout(30000);//设置读操作超时时间30 s
                            //取得输入流、输出流
                            inputStream=socket.getInputStream();
                            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
                            outputStream = socket.getOutputStream();
                            Log.w("Service","连接成功");
                            success=true;
                            socketInputByte();
                        } catch (IOException e) {

                            //连接Socket失败或者超时调用这里
                            //将字符串转化为byte数组，然后将数组前面加多一位
                            byte []oldB=CONNECTION_FAILED.getBytes();
                            byte []byD=new byte[oldB.length+1];
                            byD[0]=0x01;
                            for(int i=0;i<oldB.length;i++){
                                byD[i+1]=oldB[i];
                            }

                            broadcastReceiverData(byD);
                            Log.e("Service监听",CONNECTION_FAILED);
                            success=false;
                            threadStart=false;
                        }
                    }
                }
        );
        if(!thread.isAlive()){
            thread.start();
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("ASDFGHJK:","dfshdfksdjgdkgdlf");
    }



    /**
     * 发送数据
     */
    public Boolean socketOutput(String st,byte bs) {
        try {
            //输出流写入发送编辑框的信息并指定类型UTF-8，注意要加换行
            byte oldData[] = st.getBytes("UTF-8");
            byte newData[] = new byte[oldData.length + 1];
            newData[0] = bs;
            System.arraycopy(oldData, 0, newData, 1, oldData.length);
            outputStream.write(newData);
            //输出流发送至服务器
            outputStream.flush();
            return true;
        } catch (Exception e) {
            Log.e("Service日志", "无法连接服务器");
            return false;
        }
    }


    //收数据
    private void socketInputByte(){
        try {
            int len=0;
            //死循环重复接收输入流数据并进行处理,threadStart关闭线程
            while (threadStart) {
                byte[]byteData=new byte[1024*1024*4];
               while ((len=inputStream.read(byteData))>0){
                   byte [] bydata=new byte[len];
                   System.arraycopy(byteData,0,bydata,0,len);
                   broadcastReceiverData(bydata);
               }
                socket.sendUrgentData(0x03);//发送过去判断是否断开连接，或者断网
            }
            stopSelf();
        }catch (Exception e){
            byte[] bydata={0x03,0x03,0x03,0x03,0x03,0x03};
            broadcastReceiverData(bydata);
        }
    }

    public void setThreadStart(Boolean threadStart) {
        this.threadStart = threadStart;
    }

    /**
     * 开启广播更新UI界面
     **/
    private void broadcastReceiverData(byte []data){
        //发送广播
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putByteArray("data",data);
        intent.putExtras(bundle);
        intent.setAction("com.example.jq.taida3.Service.BackstageSockectServices");
        sendBroadcast(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        threadStart=false;
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadStart=false;
    }

    public class MyServicesBinder extends Binder {
        public BackstageSockectServices getBinder(){
            return BackstageSockectServices.this;
        }
    }

}
