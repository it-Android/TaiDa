package com.arcsoft.arcfacedemo.Tool.Data;

public class SetUpData {
    public final static String SETUPDATADATA_ADMIN_USER_NAME="admin_UserName";//管理员账号
    public final static String SETUPDATADATA_TEMPERATURE="temperature";//温度
    public final static String SETUPDATADATA_HUMIDITY="humidity";//湿度
    public final static String SETUPDATADATA_CAERBON_DIOXIDE="carbon_dioxide";//二氧化碳
    public final static String SETUPDATADATA_ILLUMINATIO_MAX="illumination_max";//光照的最大
    public final static String SETUPDATADATA_ILLUMINATIO_MIN="illumination_min";//光照的最小
    public final static String SETUPDATADATA_CAMERA_IP="Camera_IP";//摄像头IP
    public final static String SETUPDATADATA_CAMERA_PORT="Camera_Port";//摄像头端口
    public final static String SETUPDATADATA_CAMERA_ADMIN="Camera_admin";//摄像头账号
    public final static String SETUPDATADATA_CAMERA_PASSWORD="Camera_password";//摄像头账号密码
    public final static String SETUPDATADATA_INTELLIGENCE="Intelligence";//智能控制



    private String admin_UserName="";
    private float temperature=0;
    private float humidity=0;
    private float carbon_dioxide=0;
    private float illumination_max=0;
    private float illumination_min=0;
    private String camera_IP="";
    private int camera_Port=0;
    private String camera_admin="";
    private String camera_password="";
    private int intelligence=-1;


    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public String getAdmin_UserName() {
        return admin_UserName;
    }

    public void setAdmin_UserName(String admin_UserName) {
        this.admin_UserName = admin_UserName;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getCarbon_dioxide() {
        return carbon_dioxide;
    }

    public void setCarbon_dioxide(float carbon_dioxide) {
        this.carbon_dioxide = carbon_dioxide;
    }

    public float getIllumination_max() {
        return illumination_max;
    }

    public void setIllumination_max(float illumination_max) {
        this.illumination_max = illumination_max;
    }

    public float getIllumination_min() {
        return illumination_min;
    }

    public void setIllumination_min(float illumination_min) {
        this.illumination_min = illumination_min;
    }

    public String getCamera_IP() {
        return camera_IP;
    }

    public void setCamera_IP(String camera_IP) {
        this.camera_IP = camera_IP;
    }

    public int getCamera_Port() {
        return camera_Port;
    }

    public void setCamera_Port(int camera_Port) {
        this.camera_Port = camera_Port;
    }

    public String getCamera_admin() {
        return camera_admin;
    }

    public void setCamera_admin(String camera_admin) {
        this.camera_admin = camera_admin;
    }

    public String getCamera_password() {
        return camera_password;
    }

    public void setCamera_password(String camera_password) {
        this.camera_password = camera_password;
    }
}
