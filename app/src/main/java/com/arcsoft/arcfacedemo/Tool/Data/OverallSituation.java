package com.arcsoft.arcfacedemo.Tool.Data;


//全局常量数据
public class OverallSituation {

    public final static int HISTORY_TEMPERATURE=0;//历史温度
    public final static int HISTORY_HUMIDITY=1;//历史湿度
    public final static int HISTORY_CARBONDIOXIDE=2;//历史二氧化碳
    public final static int HISTORY_NOSIE=3;//历史噪声


    public final static String LOGIN_DATA_NAME="UserLogin";//用户基本数据保存的XML文件的名称
    public final static String USER_FIRSTLOGIN="firstlogin";//保存是否第一次登陆
    public final static String USER_NAME="UserNumber";//保存用户账号名称
    public final static String USER_PASSWORD="PassWord";//用户密码
    public final static String USER_IP="IP";//IP地址
    public final static String USER_PORT="Port";//端口号;
    public final static String USER_CHECKREMEMBER="CheckRemember";//记住密码
    public final static String USER_ISADMIN="isAdmin";//上次的身份
    public final static String USER_LAST_LOGIN_TIME="LastLogonTime";//上次登录时间
    public final static String USER_ISENFINE="isEngine";//上次登录时间


    public final static String SERVER_DB_NAME="delta";//服务器数据库名字
    public final static String SERVER_DB_USER_NAME="sa";//服务器账号
    public final static String SERVER_DB_USER_PSSSWORD="123456";//服务器账号密码
}
