package com.arcsoft.arcfacedemo.Tool.System;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

public class Downloadfiles {

    private Handler handler;
    public static String ROOT_PATH;//获得绝对路劲
    //文件名字
    public static final String SAVE_IMG_DIR = "register" + File.separator + "imgs";
    private static final String SAVE_FEATURE_DIR = "register" + File.separator + "features";
    private   Context context;

    public Downloadfiles(Context context) {
        this.context = context;
    }

    //下载文件
    public void writeFile(String Ip){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 666:
                        List<Map<String,Object>> list=(List<Map<String,Object>>)msg.obj;//获取返回的人脸模板，图片数据
                        for(Map<String, Object> mapData:list){
                            writeFileFaceImage((String)mapData.get(Staff.STAFF_USER_NAME),(byte[])mapData.get(Staff.STAFF_USER_FACE));//写入人脸模板
                            writeFileFace((String)mapData.get(Staff.STAFF_USER_NAME),(byte[])mapData.get(Staff.STAFF_USER_FACE_IMAGE));//写入人脸图片
                        }
                        break;
                }
            }
        };

        DataAnalysis dataAnalysis=new DataAnalysis(handler);
        dataAnalysis.getArryaFase(Ip);

        isFileFace();//初始化文件夹（人脸模板）
        isFileFaceImage();//初始化文件夹（人脸图片）
    }


    //写入人脸模板
    private void writeFileFace(String fileName, byte[] bytes) {
        if(bytes==null)return;
        if (ROOT_PATH == null) {
            //获取当前安装包的绝对路径
            ROOT_PATH = context.getFilesDir().getAbsolutePath();
        }

        //特征存储的文件夹
        File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR+File.separator+fileName);

        try {
            FileOutputStream out = new FileOutputStream(featureFileDir);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //写入人脸图片
    private void writeFileFaceImage(String fileName,byte[] bytes) {
        if(bytes==null)return;//
        if (ROOT_PATH == null) {
            //获取当前安装包的绝对路径
            ROOT_PATH = context.getFilesDir().getAbsolutePath();
        }
        //特征人脸照片的文件夹
        File imgDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR+File.separator+fileName+".jpg");

        try {
            FileOutputStream out = new FileOutputStream(imgDir);//指定写到哪个路径中
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes)); //将字节流写入文件中
            fileChannel.force(true);//强制刷新
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //创建人脸照片文件imgs
    public void isFileFace(){
        File imgFileDir =new File(context.getFilesDir().getAbsolutePath()+ File.separator + SAVE_IMG_DIR);
        if(!imgFileDir.exists()){
            imgFileDir.mkdirs();
        }
    }

    //创建人脸信息文件features
    public void isFileFaceImage(){
        File featureDir =new File( context.getFilesDir().getAbsolutePath()+ File.separator + SAVE_FEATURE_DIR);
        if(!featureDir.exists()){
            featureDir.mkdirs();
        }
    }





}
