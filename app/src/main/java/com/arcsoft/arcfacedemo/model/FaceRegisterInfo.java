package com.arcsoft.arcfacedemo.model;

public class FaceRegisterInfo {
    private byte[] featureData;//人脸特征
    private String name;//姓名
    private String num;//工号
    private String sex;//性别
    private String zhiwei;//职位
    private String Register_time;//时间
    private boolean state;//

    public FaceRegisterInfo(byte[] faceFeature, String name, String num, String sex, String zhiwei, String Register_time, boolean state) {
        this.featureData = faceFeature;
        this.name = name;
        this.num = num;
        this.sex = sex;
        this.zhiwei = zhiwei;
        this.Register_time = Register_time;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getFeatureData() {
        return featureData;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getZhiwei() {
        return zhiwei;
    }

    public void setZhiwei(String zhiwei) {
        this.zhiwei = zhiwei;
    }

    public String getRegister_time() {
        return Register_time;
    }

    public void setRegister_time(String register_time) {
        Register_time = register_time;
    }


    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
