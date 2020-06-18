package com.arcsoft.arcfacedemo.Tool.SQL.Modify;

import android.os.Handler;
import android.os.Message;

import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.SqlConnect;


public class DataModify {
    private Handler handler;

    public DataModify(Handler handler) {
        this.handler = handler;
    }

    //添加员工
    public void addStaff(final String Ip, final Staff staff){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Boolean ok=sqlConnect.addStaff(staff);
                Message msg=new Message();
                msg.what=888;
                msg.obj=ok;
                //handler.sendMessage(msg);
            }
        }).start();
    }

    //设置员工信息
    public void setStaff(final String Ip, final String userName, final Staff staff){

        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Boolean ok=sqlConnect.setStaff(userName,staff);
                Message msg=new Message();
                msg.what=888;
                msg.obj=ok;
                handler.sendMessage(msg);

            }
        }).start();

    }

    //设置考勤和修改考勤信息(传入数据库Ip，要修改的员工号，修改那天的、修改的数据（没数据为空就可以）)
    public void setAttendance(final String Ip, final String userName, final String date, final Attendance attendance){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Boolean ok=sqlConnect.setAttendance(userName,attendance,date);//账号，数据，时间
                Message msg=new Message();
                msg.what=888;
                msg.obj=ok;
                handler.sendMessage(msg);
            }
        }).start();

    }


    //写入人脸模板
    public void setfase(final String Ip, final String userName, final byte[] faceImae, final byte[] face, final String sex){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Boolean ok=sqlConnect.setFace(userName,faceImae,face,sex);
                if(handler==null)return;
                Message msg=new Message();
                msg.what=888;
                msg.obj=ok;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //写入设置数据
    public void setSetUpData(final String Ip, final SetUpData setUpData){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Boolean ok=sqlConnect.setSetUpData(setUpData);
                Message msg=new Message();
                msg.what=88842;
                msg.obj=ok;
                handler.sendMessage(msg);
            }
        }).start();
        

    }


}
