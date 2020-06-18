package com.arcsoft.arcfacedemo.Tool.System;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {

    //获取系统日期
    public static String getDate(){
        //SimpleDateFormat formatter   = new SimpleDateFormat ("yyyy年MM月dd日   HH:mm:ss ");
        SimpleDateFormat formatter   = new SimpleDateFormat ("yyyy-MM-dd");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //获取系统时间
    public static String getTime(){
        SimpleDateFormat formatter   = new SimpleDateFormat ("HH:mm:ss ");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
    public static String  getNowDate(){
        SimpleDateFormat formatter   = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //获取系统时间
    public static String getTimeNumber(){
        SimpleDateFormat formatter   = new SimpleDateFormat ("yyyyMMdd");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //获取系统时间
    public static String getTimeNumbers(){
        SimpleDateFormat formatter   = new SimpleDateFormat ("HHmmss");
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    //将字节数组转换为16进制的字符串
    public static String bytes2HexString(byte[] data) {
        if (data == null) {
            throw new NullPointerException();
        }
        StringBuilder buffer = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                buffer.append('0');
            }
            buffer.append(hex);
        }
        return buffer.toString();
    }

    //将16进制的字符串转换为字符数组
    public static byte[] HexStringbyte(String s) {
        if (s==null)return null;
        s=s.replace("0x","");
        byte [] b  = new byte[s.length()/2];
        if(s.length()>0)
        {
            for(int i = 0 ; i < s.length()/2 ; i++)
            {
                int i1 = Integer.parseInt(s.substring(i*2,i*2+2),16);
                byte i2 = (byte)(i1&0xff);
                b[i] = i2;
            }
        }
        return   b;
    }

}
