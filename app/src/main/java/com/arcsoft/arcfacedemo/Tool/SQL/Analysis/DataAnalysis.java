package com.arcsoft.arcfacedemo.Tool.SQL.Analysis;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.arcsoft.arcfacedemo.Tool.Data.Admin;
import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.SqlConnect;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAnalysis {
    private Handler handler;

    public DataAnalysis(Handler handler) {
        this.handler = handler;
    }

    /**
     * 登录验证  1111
     * @param Ip
     * @param user
     * @param pws
     * @param num
     */
    public void validateLogon1(final String Ip, final String user, final String pws, final String num) {
        new Thread(new Runnable() {
            Boolean ok=false;
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//数据库密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                switch (num){
                    case "admin":
                        Admin admin=sqlConnect.getAdmin(user);
                        String nn=admin.getPassWord();
                        if(admin.getPassWord().equals(pws)){
                            ok= true;
                        }
                        break;
                    case "staff":
                        Staff staff=sqlConnect.getStaff(user,false);
                        if(staff.getUserPassWord().equals(pws))ok= true;
                        break;
                    default:ok= false;
                }
                Message mag=new Message();
                mag.what=HandlerNumberRecord.DATA_ANALYSIS_1111;//发送验证通过命令
                mag.obj=ok;
                handler.sendMessage(mag);
            }
        }).start();
    }


    /**
     * 获取某个员工的全部信息，（基本和考勤） 20
     * @param Ip
     * @param user
     */
    public void staffData(final String Ip,final String user,final Boolean isImage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                            OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                            OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                            OverallSituation.SERVER_DB_NAME);//数据库名称
                    Staff staff=sqlConnect.getStaff(user,isImage);//获取用户的基本信息
                    List<Attendance> listAtt=sqlConnect.getAttendance(user);//获取用户的考勤信息
                    Message msg=new Message();
                    msg.what=HandlerNumberRecord.DATA_ANALYSIS_20;
                    Map<String,Object> map=new HashMap<>();
                    map.put("staff",staff);
                    map.put("listAtt",listAtt);
                    msg.obj=map;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Log.w("StaffActivity监控","dataInit()方法");
                }
            }
        }).start();
    }

    public void staffData(final String Ip,final String user){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                            OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                            OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                            OverallSituation.SERVER_DB_NAME);//数据库名称
                    Staff staff=sqlConnect.getStaff(user,false);//获取用户的基本信息
                    Message msg=new Message();
                    msg.what=300;
                    msg.obj=staff;
                    handler.sendMessage(msg);
                }catch (Exception e){
                    Log.w("StaffActivity监控","dataInit()方法");
                }
            }
        }).start();
    }

    //获取某个员工某天的考勤记录
    public void getAttendance(final String Ip, final String userName, final String date){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                            OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                            OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                            OverallSituation.SERVER_DB_NAME);//数据库名称
                    Attendance attendance=sqlConnect.getAttendance(userName,date);
                    Message msg=new Message();
                    msg.what=19;
                    msg.obj=attendance;
                    handler.handleMessage(msg);
                }catch (Exception e){

                }
            }
        }).start();
    }

    /**
     * 获取全部员工的基本信息 21
     * @param Ip
     */
    public void getStaffArryaData(final String Ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                List<Staff> list=sqlConnect.getArryaStaff();
                Message msg=new Message();
                msg.what=HandlerNumberRecord.DATA_ANALYSIS_21;
                msg.obj=list;
                handler.sendMessage(msg);

            }
        }).start();


    }

    //获取管理员信息 22
    public void getAdminData(final String Ip, final String useName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                Admin admin=sqlConnect.getAdmin(useName);
                Message msg=new Message();
                msg.what=HandlerNumberRecord.DATA_ANALYSIS_22;
                msg.obj=admin;
                handler.sendMessage(msg);


            }
        }).start();
    }

    //获取员工的人脸模板信息  666
    public void getArryaFase(final String Ip){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                List<Map<String,Object>> list=sqlConnect.getArryaFace();
                Message msg=new Message();
                msg.what=HandlerNumberRecord.DATA_ANALYSIS_666;
                msg.obj=list;
                handler.sendMessage(msg);
            }
        }).start();
    }

    //获取传感器的历史记录  777
    public void getArryaSensor(final String Ip,final String time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                List<Sensor>list=sqlConnect.getArryaSensor(time);
                Message msg=new Message();
                msg.what=HandlerNumberRecord.DATA_ANALYSIS_777;
                msg.obj=list;
                handler.sendMessage(msg);
            }
        }).start();

    }

    //获取设置数据
    public void getSetUpData(final String Ip,final String userName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                SetUpData setUpData;
                setUpData=sqlConnect.getSetUpData(userName);
                Message msg=new Message();
                msg.what=42;
                msg.obj=setUpData;
                handler.sendMessage(msg);

            }
        }).start();
    }

    //获取历史传感器数据
    public void getSensorHistoryData(final String Ip,final String date){

        new Thread(new Runnable() {
            @Override
            public void run() {
                SqlConnect sqlConnect=new SqlConnect(Ip,//ip
                        OverallSituation.SERVER_DB_USER_NAME,//数据库账号
                        OverallSituation.SERVER_DB_USER_PSSSWORD,//密码
                        OverallSituation.SERVER_DB_NAME);//数据库名称
                List<SensorHistoryData> list=sqlConnect.getSensorHistoryData(date);
                Message msg=new Message();
                msg.what=777;
                msg.obj=list;
                handler.sendMessage(msg);


            }
        }).start();




    }


}
