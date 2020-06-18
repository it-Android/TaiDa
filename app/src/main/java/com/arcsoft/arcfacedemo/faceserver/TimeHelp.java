package com.arcsoft.arcfacedemo.faceserver;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2019/5/11.
 */

public class TimeHelp {

    private String Time="00:00--23:00";

    Calendar currentDate;
    Calendar max;
    Calendar min;

    SimpleDateFormat df; //日期格式

    private  int Success = 1;
    private  int Agine_Success= 2;
    private  int lose_time = 3;

    TimeHelp timeHelp ;

    public TimeHelp()
    {

    }
    public TimeHelp(String Time)
    {
        if(timeHelp == null)
        {
            synchronized (TimeHelp.class)
            {
                if(timeHelp == null)
                {
                    timeHelp = new TimeHelp();
                }
            }
        }
        this.Time = Time;
        df = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");
        currentDate = Calendar.getInstance();
        min= Calendar.getInstance();
        String[]strs=Time.split("--");
        String[]strs1 =strs[0].split(":");
        String[]strs2 =strs[1].split(":");
        //min=currentDate; 最小时间
        min.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
        min.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
        min.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strs1[0]));
        min.set(Calendar.MINUTE, Integer.parseInt(strs1[1]));
        min.set(Calendar.SECOND, 0);
        min.set(Calendar.MILLISECOND, 0);
        System.out.println(df.format(min.getTime()));
        max= Calendar.getInstance();
        // max=currentDate; 最大时间
        max.set(Calendar.YEAR, currentDate.get(Calendar.YEAR));
        max.set(Calendar.MONTH, currentDate.get(Calendar.MONTH));
        max.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strs2[0]));
        max.set(Calendar.MINUTE, Integer.parseInt(strs2[1]));
        max.set(Calendar.SECOND, 0);
        max.set(Calendar.MILLISECOND, 0);
        System.out.println(df.format(max.getTime()));

    }
    public String getCurrentTime()
    {
        Log.i("MSG",df.format(new Date()));
        return  df.format(new Date());
    }
    public long getCurrentCalendar()
    {
        currentDate = Calendar.getInstance();
        Log.i("MSG",df.format(currentDate.getTime())) ;
        return  currentDate.getTimeInMillis();

    }

    public int CompareTime(boolean Employ_flag)
    {
        if(!Employ_flag)
        {
            long current = getCurrentCalendar();

            if( current >=min.getTimeInMillis() && current <=max.getTimeInMillis()){
               Log.i("MSG","今天登录成功");
               return 1;
            } else if( current <=min.getTimeInMillis() || current >=max.getTimeInMillis() )
            {
                System.out.println("未在指定时间签到，未登录成功");
                return 3;
            }
        }else
        {
            return 2;
        }
        return 3;
    }



   public void timeset() {
   }

}
