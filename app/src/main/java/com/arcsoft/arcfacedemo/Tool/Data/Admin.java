package com.arcsoft.arcfacedemo.Tool.Data;

public class Admin {
    public final static String ADMIN_ID="adminID";
    public final static String ADMIN_USER_NAME="adminUserName";//管理员账号
    public final static String ADMIN_PASSWORD="adminPassword";//管理员密码
    public final static String ADMIN_IMAGE="adminImage";//管理员图片


    private int ID=0;
    private String userName="";
    private String passWord="";
    private byte[] image=null;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }




}
