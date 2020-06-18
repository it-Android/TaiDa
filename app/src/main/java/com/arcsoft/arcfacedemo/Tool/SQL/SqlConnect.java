package com.arcsoft.arcfacedemo.Tool.SQL;


import android.util.Log;

import com.arcsoft.arcfacedemo.Tool.Data.Admin;
import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlConnect {
    private String ip;
    private String num;
    private String user;
    private String pwd;
    private String db;
    public SqlConnect(String ip, String user, String pwd, String db) {
        this.ip = ip;
        this.user = user;
        this.pwd = pwd;
        this.db = db;
        this.num="1433";
    }
    public SqlConnect(String ip, String num, String user, String pwd, String db) {
        this.ip = ip;
        this.num = num;
        this.user = user;
        this.pwd = pwd;
        this.db = db;
    }

    private Connection getSQLConnection(String ip, String user, String pwd, String db) {
        Connection con = null;
        try
        {
            /**************************要加jar包，不然会报错***************************************/
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + ip + ":"+num+"/" + db , user, pwd);
        } catch (Exception e){
            Log.w("SqlConnect","sql连接出错！1、检查是否导入jtds-1.2.7.jar包 2、不能再主线程获取");
        }
        return con;
    }

    //返回全部的管理员
    //String sql = "select * from admin_Table";
    public List<Admin> getArryaAdmin(){
        String sql = "select * from admin_Table";
        List<Admin> list=new ArrayList<>();
        try
        {
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next())
            {
                Admin admin=new Admin();
                admin.setID(rs.getInt(Admin.ADMIN_ID));
                admin.setUserName(rs.getString(Admin.ADMIN_USER_NAME));
                admin.setPassWord(rs.getString(Admin.ADMIN_PASSWORD));
                admin.setImage(rs.getBytes(Admin.ADMIN_IMAGE));
                list.add(admin);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e)
        {

        }
        return list;
    }
    //查但一个管理员的信息
    public Admin getAdmin(String userName){
        Admin admin=new Admin();
        String sql = "select * from admin_Table where "+Admin.ADMIN_USER_NAME+"=\'"+userName+"\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next())
            {
                admin.setID(rs.getInt(Admin.ADMIN_ID));
                admin.setUserName(rs.getString(Admin.ADMIN_USER_NAME));
                admin.setPassWord(rs.getString(Admin.ADMIN_PASSWORD));
                admin.setImage(rs.getBytes(Admin.ADMIN_IMAGE));
            }
            rs.close();
            stmt.close();
            conn.close();

        }catch (Exception e){}
        return admin;
    }

    //查询全部员工信息
    public List<Staff> getArryaStaff(){
        String sql = "select "
                +Staff.STAFF_NAME+" , "
                +Staff.STAFF_USER_SEX+" , "
                +Staff.STAFF_USER_NAME+" , "
                +Staff.STAFF_USER_PASSWORD+" , "
                +Staff.STAFF_USER_AGE+" , "
                +Staff.STAFF_USER_PHONE+" , "
                +Staff.STAFF_USER_E_MAIL+" , "
                +Staff.STAFF_USER_JOB+" from user_Table";
        List<Staff> list=new ArrayList<>();
        try
        {
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next())
            {
                Staff staff=new Staff();
                staff.setName(rs.getString(Staff.STAFF_NAME));//获取员工名字
                staff.setUserSex(rs.getString(Staff.STAFF_USER_SEX));//获取并设置员工性别
                staff.setUserName((rs.getString(Staff.STAFF_USER_NAME)));//获取并设置用户名
                staff.setUserPassWord(rs.getString(Staff.STAFF_USER_PASSWORD));//获取并设置员工密码
                staff.setAge(rs.getInt(Staff.STAFF_USER_AGE));//获取并设置员工年龄
                staff.setPhone(rs.getString(Staff.STAFF_USER_PHONE));//获取员工手机
                staff.setE_Mail(rs.getString(Staff.STAFF_USER_E_MAIL));//获取并设置员工邮箱
                staff.setUserJob(rs.getString(Staff.STAFF_USER_JOB));//获取并设置员工职位
                list.add(staff);
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e)
        {
            list.add(new Staff());
        }
        return list;
    }

    //获取单个员工
    public Staff getStaff(String userName,Boolean getImg){
        String sql="";
        if(getImg){
            sql= "select * from user_Table where "+Staff.STAFF_USER_NAME+"=\'"+userName+"\'";
        }else{
             sql= "select "
                     +Staff.STAFF_NAME+" , "
                     +Staff.STAFF_USER_SEX+" , "
                     +Staff.STAFF_USER_NAME+" , "
                     +Staff.STAFF_USER_PASSWORD+" , "
                     +Staff.STAFF_USER_AGE+" , "
                     +Staff.STAFF_USER_PHONE+" , "
                     +Staff.STAFF_USER_E_MAIL+" , "
                     +Staff.STAFF_USER_JOB
                     +" from user_Table where "+Staff.STAFF_USER_NAME+"=\'"+userName+"\'";
        }
        Staff staff=new Staff();

        try
        {
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery

            while (rs.next())
            {
                staff.setName(rs.getString(Staff.STAFF_NAME));//获取员工名字
                staff.setUserSex(rs.getString(Staff.STAFF_USER_SEX));//获取并设置员工性别
                staff.setUserName((rs.getString(Staff.STAFF_USER_NAME)));//获取并设置用户名
                staff.setUserPassWord(rs.getString(Staff.STAFF_USER_PASSWORD));//获取并设置员工密码
                staff.setAge(rs.getInt(Staff.STAFF_USER_AGE));//获取并设置员工年龄
                staff.setPhone(rs.getString(Staff.STAFF_USER_PHONE));//获取员工手机
                staff.setE_Mail(rs.getString(Staff.STAFF_USER_E_MAIL));//获取并设置员工邮箱
                if(getImg){
                    try {
                        staff.setUserImage(Utilities.HexStringbyte(rs.getString(Staff.STAFF_USER_IMAGE)));//获取并设置员工图片=====HexStringbyte将16进制格式字符串转换为byte[]
                    }catch (Exception e){}
                }else{
                    staff.setUserImage(null);//获取并设置员工图片
                }
                staff.setUserJob(rs.getString(Staff.STAFF_USER_JOB));//获取并设置员工职位
            }
            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e)
        {

        }
        return staff;
    }

    //获取员工的考勤信息
    public List<Attendance> getAttendance(String userName){
        // String sql = "select * from user_Table where "+Staff.STAFF_USER_NAME+"=\'"+userName+"\'";
        String sql = "select * from Attendance_Table where "+Staff.STAFF_USER_NAME+"=\'"+userName+"\'";
        List<Attendance> list=new ArrayList<>();
        try
        {
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next())
            {
                Attendance ad=new Attendance();
                ad.setUserName(rs.getString(Attendance.ATTENDANCE_USER_NAME));//用户名
                ad.setDate(rs.getString(Attendance.ATTENDANCE_DATE));//日期

                ad.setAmAttendance(rs.getString(Attendance.ATTENDANCE_MORNING_ATTENDANCE));//早上
                ad.setAmNoAttendance(rs.getString(Attendance.ATTENDANCE_MORNING_NOATTENDANCE));

                ad.setPmAttendance(rs.getString(Attendance.ATTENDANCE_AFTERNOON_ATTENDANCE));//下午
                ad.setPmNoAttendance(rs.getString(Attendance.ATTENDANCE_AFTERNOON_NOATTENDANCE));

                ad.setNiAttendance(rs.getString(Attendance.ATTENDANCE_NIGHT_ATTENDANCE));//晚上
                ad.setNiNoAttendance(rs.getString(Attendance.ATTENDANCE_NIGHT_NOATTENDANCE));
                list.add(ad);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e)
        {

        }
        return list;
    }

    //获取用户某天的考勤记录
    public Attendance getAttendance(String userName, String date){
        Attendance attendance=new Attendance();
        String sql = "select * from Attendance_Table where "+Attendance.ATTENDANCE_USER_NAME+"=\'"+userName+"\' and "+Attendance.ATTENDANCE_DATE+" like \'"+date+"%\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery

            while (rs.next()){
                attendance.setAmAttendance(rs.getString(Attendance.ATTENDANCE_MORNING_ATTENDANCE));
                attendance.setAmNoAttendance(rs.getString(Attendance.ATTENDANCE_MORNING_NOATTENDANCE));
                attendance.setPmAttendance(rs.getString(Attendance.ATTENDANCE_AFTERNOON_ATTENDANCE));
                attendance.setPmNoAttendance(rs.getString(Attendance.ATTENDANCE_AFTERNOON_NOATTENDANCE));
                attendance.setNiNoAttendance(rs.getString(Attendance.ATTENDANCE_NIGHT_NOATTENDANCE));
                attendance.setNiAttendance(rs.getString(Attendance.ATTENDANCE_NIGHT_ATTENDANCE));
                attendance.setUserName(rs.getString(Attendance.ATTENDANCE_USER_NAME));
                attendance.setDate(rs.getString(Attendance.ATTENDANCE_DATE));
            }

            rs.close();
            stmt.close();
            conn.close();
            return attendance;
        }catch (Exception e){}

        return attendance;
    }

    //添加管理员
    public Boolean addAdmin(Admin admin){
        //String sql="Update user_Table set Password = @name where ID=@userid";
        //insert into user_Table values (2,'码仙2','火星',1002,'女')
        String sql="insert into admin_Table values ("+
                "\'"+admin.getUserName()+"\',"+
                "\'"+admin.getPassWord()+"\',"+
                admin.getImage()
                +")";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }
    //修改管理员信息
    public Boolean setAdmin(String userName,Admin admin){
        List<String> list=new ArrayList<>();
        if(admin.getUserName()!=null&&!admin.getUserName().equals(""))list.add(Admin.ADMIN_USER_NAME+" = "+"\'"+admin.getUserName()+"\'");
        if(admin.getPassWord()!=null&&!admin.getPassWord().equals(""))list.add(Admin.ADMIN_PASSWORD+" = "+"\'"+admin.getPassWord()+"\'");
        //if(!admin.getImage().equals(null))list.add(Admin.ADMIN_IMAGE+" = "+admin.getImage());
        String str="";
        for(int i=0;i<list.size();i++){
            str+=list.get(i);
            if(i<list.size()-1){
                str+=" , ";
            }
        }
        if(list.size()==0)return false;
        String sql="Update admin_Table set "+str+" where "+Admin.ADMIN_USER_NAME+" =\'"+userName+"\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }

    }

    //添加用户信息
    public Boolean addStaff(Staff staff){
        String sql="insert into user_Table (" +
                Staff.STAFF_NAME +","+
                Staff.STAFF_USER_SEX +","+
                Staff.STAFF_USER_NAME +","+
                Staff.STAFF_USER_AGE +","+
                Staff.STAFF_USER_PASSWORD +","+
                Staff.STAFF_USER_E_MAIL +","+
                Staff.STAFF_USER_JOB +","+
                Staff.STAFF_USER_PHONE +","+
                Staff.STAFF_USER_FACE +","+
                Staff.STAFF_USER_FACE_IMAGE +
                ") values ("+
                "\'"+staff.getName()+"\',"+
                "\'"+staff.getUserSex()+"\',"+
                "\'"+staff.getUserName()+"\',"+
                "\'"+staff.getAge()+"\',"+
                "\'"+staff.getUserPassWord()+"\',"+
                "\'"+staff.getE_Mail()+"\',"+
                "\'"+staff.getUserJob()+"\',"+
                "\'"+staff.getPhone()+"\',"+
                "\'"+staff.getFace()+"\',"+
                "\'"+staff.getFaceImage()
                +"\')";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //修改用户信息
    public Boolean setStaff(String userName,Staff staff){
        List<String> list=new ArrayList<>();
        //拼接sql语句
        if(!staff.getName().equals(""))list.add(Staff.STAFF_NAME+" = "+"\'"+staff.getName()+"\'");
        if(!staff.getUserSex().equals(""))list.add(Staff.STAFF_USER_SEX+" = "+"\'"+staff.getUserSex()+"\'");
        if(staff.getAge()!=0)list.add(Staff.STAFF_USER_AGE+" = "+"\'"+staff.getAge()+"\'");
        if(!staff.getUserJob().equals(""))list.add(Staff.STAFF_USER_JOB+" = "+"\'"+staff.getUserJob()+"\'");
        if(!staff.getE_Mail().equals(""))list.add(Staff.STAFF_USER_E_MAIL+" = "+"\'"+staff.getE_Mail()+"\'");
        if(!staff.getPhone().equals(""))list.add(Staff.STAFF_USER_PHONE+" = "+"\'"+staff.getPhone()+"\'");
        if(!staff.getUserName().equals(""))list.add(Staff.STAFF_USER_NAME+" = "+"\'"+staff.getUserName()+"\'");
        if(!staff.getUserPassWord().equals(""))list.add(Staff.STAFF_USER_PASSWORD+" = "+"\'"+staff.getUserPassWord()+"\'");
        if(!staff.getFaceImage().equals(""))list.add(Staff.STAFF_USER_FACE_IMAGE+" = "+"\'"+staff.getFaceImage()+"\'");
        if(!staff.getFace().equals(""))list.add(Staff.STAFF_USER_FACE+" = "+"\'"+staff.getFace()+"\'");

        if(staff.getUserImage()!=null) {
            try {
                String imgData=Utilities.bytes2HexString(staff.getUserImage());//将byte[]转换为16进制格式的字符串
                list.add(Staff.STAFF_USER_IMAGE+" = 0x"+imgData);
            } catch (Exception e) {

            }
        }
        if(list.size()==0)return false;
        String str="";
        for(int i=0;i<list.size();i++){
            str+=list.get(i);
            if(i<list.size()-1){
                str+=" , ";
            }
        }
        String sql="Update user_Table set "+str+" where "+Staff.STAFF_USER_NAME+" =\'"+userName+"\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //修改考勤记录
    public Boolean setAttendance(String userName, Attendance ad, String date){

        List<String> list=new ArrayList<>();
        if(ad.getAmAttendance()!=null&&!ad.getAmAttendance().equals(""))list.add(Attendance.ATTENDANCE_MORNING_ATTENDANCE+" = "+"\'"+ad.getAmAttendance()+"\'");
        if(ad.getAmNoAttendance()!=null&&!ad.getAmNoAttendance().equals(""))list.add(Attendance.ATTENDANCE_MORNING_NOATTENDANCE+" = "+"\'"+ad.getAmNoAttendance()+"\'");

        if(ad.getPmAttendance()!=null&&!ad.getPmAttendance().equals(""))list.add(Attendance.ATTENDANCE_AFTERNOON_ATTENDANCE+" = "+"\'"+ad.getPmAttendance()+"\'");
        if(!ad.getPmNoAttendance().equals(""))list.add(Attendance.ATTENDANCE_AFTERNOON_NOATTENDANCE+" = "+"\'"+ad.getPmNoAttendance()+"\'");

        if(ad.getNiAttendance()!=null&&!ad.getNiAttendance().equals(""))list.add(Attendance.ATTENDANCE_NIGHT_ATTENDANCE+" = "+"\'"+ad.getNiAttendance()+"\'");
        if(ad.getNiNoAttendance()!=null&&!ad.getNiNoAttendance().equals(""))list.add(Attendance.ATTENDANCE_NIGHT_NOATTENDANCE+" = "+"\'"+ad.getNiNoAttendance()+"\'");
        // if(!staff.getUserImage().equals(null))list.add(Admin.ADMIN_PASSWORD+" = "+"\'"+staff.getUserImage()+"\'");

        //if(!admin.getImage().equals(null))list.add(Admin.ADMIN_IMAGE+" = "+admin.getImage());
        String str="";
        if(list.size()==0)return false;
        for(int i=0;i<list.size();i++){
            str+=list.get(i);
            if(i<list.size()-1){
                str+=" , ";
            }
        }

        String sql="Update Attendance_Table set "+str+" where "+Attendance.ATTENDANCE_USER_NAME+" = \'"+userName+"\' and "+Attendance.ATTENDANCE_DATE+" like \'"+date+"%\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //获取全部人的人脸模板
    public List<Map<String,Object>> getArryaFace(){
        String sql= "select "+Staff.STAFF_USER_NAME+","+Staff.STAFF_USER_FACE+","+Staff.STAFF_USER_FACE_IMAGE+" from user_Table";
        List<Map<String,Object>> list=new ArrayList<>();
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next()){
                Map<String,Object> mapData=new HashMap<>();
                mapData.put(Staff.STAFF_USER_NAME,rs.getString(Staff.STAFF_USER_NAME));//将获取到的人脸模板转换为字节数组
                mapData.put(Staff.STAFF_USER_FACE,Utilities.HexStringbyte(rs.getString(Staff.STAFF_USER_FACE)));//将获取到的人脸模板转换为字节数组
                mapData.put(Staff.STAFF_USER_FACE_IMAGE,Utilities.HexStringbyte(rs.getString(Staff.STAFF_USER_FACE_IMAGE)));//将获取到的人脸模板转换为字节数组
                list.add(mapData);
            }

            rs.close();
            stmt.close();
            conn.close();
        }catch (Exception e){
        }
    return list;
    }

    //写入人脸模板
    public Boolean setFace(String userName,byte[] faceImage,byte[] face,String sex){
        if(faceImage==null&&face==null&&sex==null&&sex.equals("")){
            return false;
        }

        String faceData=Utilities.bytes2HexString(face);
        String faceImageData=Utilities.bytes2HexString(faceImage);
        String sql="Update user_Table set "
                +Staff.STAFF_USER_FACE_IMAGE+" = \'"+faceImageData+"\',"
                +Staff.STAFF_USER_FACE+" = \'"+faceData+"\',"
                +Staff.STAFF_USER_SEX+" = \'"+sex+"\'"
                +" where "+Staff.STAFF_USER_NAME+" =\'"+userName+"\'";
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();//插入数据executeUpdate
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //获取传感器的历史记录
    public List<Sensor> getArryaSensor(String time){
        String sql= "select * from doer_Table where "+Sensor.SENSOR_DATA_DATE+" like \'"+time+"%\'";
        List<Sensor> list=new ArrayList<>();
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next()){
                Sensor sensor=new Sensor();
                sensor.setDate(rs.getString(Sensor.SENSOR_DATA_DATE));
                sensor.setTemperature(rs.getFloat(Sensor.SENSOR_DATA_TEMPERATURE));
                sensor.setCarbon_dioxide(rs.getFloat(Sensor.SENSOR_DATA_CARBON_DIOXIDE));
                sensor.setHumidity(rs.getFloat(Sensor.SENSOR_DATA_HUMIDITY));
                sensor.setNoise(rs.getFloat(Sensor.SENSOR_DATA_NOISE));
                sensor.setFan(rs.getInt(Sensor.SENSOR_DATA_FAN));
                sensor.setSmoke(rs.getInt(Sensor.SENSOR_DATA_SMOKE));
                sensor.setFlame(rs.getInt(Sensor.SENSOR_DATA_FLAME));
                sensor.setBulb1(rs.getInt(Sensor.SENSOR_DATA_BULB_1));
                sensor.setBulb2(rs.getInt(Sensor.SENSOR_DATA_BULB_2));
                sensor.setBulb3(rs.getInt(Sensor.SENSOR_DATA_BULB_3));
                sensor.setBulb4(rs.getInt(Sensor.SENSOR_DATA_BULB_4));
                sensor.setBulb5(rs.getInt(Sensor.SENSOR_DATA_BULB_5));
                sensor.setBulb6(rs.getInt(Sensor.SENSOR_DATA_BULB_6));
                list.add(sensor);
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch (Exception e){

        }
        return list;
    }


    //设置 设置的属性
    public Boolean setSetUpData(SetUpData setUpData){

        List<String> list=new ArrayList<>();
        if(setUpData.getAdmin_UserName()==null&&setUpData.getAdmin_UserName().equals(""))return false;
        if(setUpData.getTemperature()!=0) list.add(SetUpData.SETUPDATADATA_TEMPERATURE+" =\'"+setUpData.getTemperature()+"\'");
        if(setUpData.getHumidity()!=0) list.add(SetUpData.SETUPDATADATA_HUMIDITY+" =\'"+setUpData.getHumidity()+"\'");
        if(setUpData.getCarbon_dioxide()!=0) list.add(SetUpData.SETUPDATADATA_CAERBON_DIOXIDE+" =\'"+setUpData.getCarbon_dioxide()+"\'");
        if(setUpData.getIllumination_max()!=0) list.add(SetUpData.SETUPDATADATA_ILLUMINATIO_MAX+" =\'"+setUpData.getIllumination_max()+"\'");
        if(setUpData.getIllumination_min()!=0) list.add(SetUpData.SETUPDATADATA_ILLUMINATIO_MIN+" =\'"+setUpData.getIllumination_min()+"\'");
        if(setUpData.getCamera_IP()!=null&&!setUpData.getCamera_IP().equals("")) list.add(SetUpData.SETUPDATADATA_CAMERA_IP+" =\'"+setUpData.getCamera_IP()+"\'");
        if(setUpData.getCamera_Port()!=0) list.add(SetUpData.SETUPDATADATA_CAMERA_PORT+" =\'"+setUpData.getCamera_Port()+"\'");
        if(setUpData.getCamera_admin()!=null&&!setUpData.getCamera_admin().equals("")) list.add(SetUpData.SETUPDATADATA_CAMERA_ADMIN+" =\'"+setUpData.getCamera_admin()+"\'");
        if(setUpData.getCamera_password()!=null&&!setUpData.getCamera_password().equals("")) list.add(SetUpData.SETUPDATADATA_CAMERA_PASSWORD+" =\'"+setUpData.getCamera_password()+"\'");
        if(setUpData.getIntelligence()!=-1) list.add(SetUpData.SETUPDATADATA_INTELLIGENCE+" =\'"+setUpData.getIntelligence()+"\'");

        if(list.size()==0)return false;
        StringBuffer buffer=new StringBuffer();
        for(int i=0;i<list.size();i++){
            buffer.append(list.get(i));
            if(i<list.size()-1){
                buffer.append(",");
            }
        }
        String userName=setUpData.getAdmin_UserName();
        if(userName==null||userName.equals("")){
            userName="admin";
        }
        String sql="Update setting_Table set "+buffer.toString()+" where "+SetUpData.SETUPDATADATA_ADMIN_USER_NAME+"=\'"+userName+"\'";

        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);//插入数据executeUpdate
            stmt.close();
            conn.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    //获取设置数据
    public SetUpData getSetUpData(String UserName){
        String sql= "select * from setting_Table where "+SetUpData.SETUPDATADATA_ADMIN_USER_NAME+" =\'"+UserName+"\'";
        SetUpData setUpData=new SetUpData();
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next()){
                setUpData.setAdmin_UserName(rs.getString(SetUpData.SETUPDATADATA_ADMIN_USER_NAME));
                setUpData.setTemperature(rs.getFloat(SetUpData.SETUPDATADATA_TEMPERATURE));
                setUpData.setHumidity(rs.getFloat(SetUpData.SETUPDATADATA_HUMIDITY));
                setUpData.setCarbon_dioxide(rs.getFloat(SetUpData.SETUPDATADATA_CAERBON_DIOXIDE));
                setUpData.setIllumination_max(rs.getFloat(SetUpData.SETUPDATADATA_ILLUMINATIO_MAX));
                setUpData.setIllumination_min(rs.getFloat(SetUpData.SETUPDATADATA_ILLUMINATIO_MIN));
                setUpData.setCamera_IP(rs.getString(SetUpData.SETUPDATADATA_CAMERA_IP));
                setUpData.setCamera_Port(rs.getInt(SetUpData.SETUPDATADATA_CAMERA_PORT));
                setUpData.setCamera_admin(rs.getString(SetUpData.SETUPDATADATA_CAMERA_ADMIN));
                setUpData.setCamera_password(rs.getString(SetUpData.SETUPDATADATA_CAMERA_PASSWORD));
                setUpData.setIntelligence(rs.getInt(SetUpData.SETUPDATADATA_INTELLIGENCE));
            }
            rs.close();
            stmt.close();
            conn.close();
            return setUpData;
        }catch (Exception e){
            return setUpData;
        }
    }


    //传感器数据
    public List<SensorHistoryData> getSensorHistoryData(String date){
        String sql= "select * from doer_Table where "+SensorHistoryData.DATE+" like \'"+date+"%\'";
        List<SensorHistoryData> list=new ArrayList<>();
        try{
            Connection conn = getSQLConnection(ip ,user, pwd, db);//连接
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);//输入查询语句executeQuery
            while (rs.next()){
                SensorHistoryData historyData=new SensorHistoryData();
                historyData.setDate(rs.getString(SensorHistoryData.DATE));
                historyData.setTemperature(rs.getFloat(SensorHistoryData.TEMPERATURE));
                historyData.setCarbon_dioxide(rs.getFloat(SensorHistoryData.CARBON_DIOXIDE));

                historyData.setHumidity(rs.getFloat(SensorHistoryData.HUMIDITY));
                historyData.setIllumination(rs.getFloat(SensorHistoryData.ILLUMINATION));

                historyData.setFlame(rs.getFloat(SensorHistoryData.FLAME));
                historyData.setSmoke(rs.getFloat(SensorHistoryData.SENSOR_DATA_SMOKE));
                list.add(historyData);
            }

        return list;

        }catch (Exception e){}
        return list;
    }




}