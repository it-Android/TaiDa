package com.arcsoft.arcfacedemo.Tool.Data;

public class Staff {
    public final static String STAFF_NAME="name";//姓名
    public final static String STAFF_USER_SEX="userSex";//性别
    public final static String STAFF_USER_NAME="userName";//登录名
    public final static String STAFF_USER_PASSWORD="userPassword";//登录密码
    public final static String STAFF_USER_AGE="age";//年龄
    public final static String STAFF_USER_PHONE="phone";//手机号码
    public final static String STAFF_USER_E_MAIL="e_mail";//邮箱
    public final static String STAFF_USER_IMAGE="userImage";//用户照片
    public final static String STAFF_USER_JOB="userJob";//职位
    public final static String STAFF_USER_FACE="face";//人脸模板
    public final static String STAFF_USER_FACE_IMAGE="faceimage";//人脸图片

    private String Name="";
    private String UserSex="";
    private String UserName="";
    private String UserPassWord="";

    private int Age;
    private String Phone="";
    private String E_Mail="";
    private byte[] UserImage=null;
    private String UserJob="";
    private String Face="";
    private String FaceImage="";

    public String getFaceImage() {
        return FaceImage;
    }

    public void setFaceImage(String faceImage) {
        FaceImage = faceImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getUserSex() {
        return UserSex;
    }

    public void setUserSex(String userSex) {
        UserSex = userSex;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserPassWord() {
        return UserPassWord;
    }

    public void setUserPassWord(String userPassWord) {
        UserPassWord = userPassWord;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getE_Mail() {
        return E_Mail;
    }

    public void setE_Mail(String e_Mail) {
        E_Mail = e_Mail;
    }

    public byte[] getUserImage() {
        return UserImage;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getFace() {
        return Face;
    }

    public void setFace(String face) {
        Face = face;
    }
    public void setUserImage(byte[] userImage) {
        UserImage = userImage;
    }

    public String getUserJob() {
        return UserJob;
    }

    public void setUserJob(String userJob) {
        UserJob = userJob;
    }
}
