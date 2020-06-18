package com.arcsoft.arcfacedemo.Tool.Data;

public class Attendance {

    public final static String ATTENDANCE_USER_NAME="userName";
    public final static String ATTENDANCE_DATE="date";

    public final static String ATTENDANCE_MORNING_ATTENDANCE="morningAttendance";//早上签到
    public final static String ATTENDANCE_MORNING_NOATTENDANCE="morningNoattendance";//早上签离

    public final static String ATTENDANCE_AFTERNOON_ATTENDANCE="afternoonAttendance";
    public final static String ATTENDANCE_AFTERNOON_NOATTENDANCE="afternoonNoattendance";

    public final static String ATTENDANCE_NIGHT_ATTENDANCE="nightAttendance";
    public final static String ATTENDANCE_NIGHT_NOATTENDANCE="nightNoattendance";


    private String userName="";
    private String date="";
    private String amAttendance="";
    private String amNoAttendance="";
    private String pmAttendance="";
    private String pmNoAttendance="";
    private String niAttendance="";
    private String niNoAttendance="";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmAttendance() {
        return amAttendance;
    }

    public void setAmAttendance(String amAttendance) {
        this.amAttendance = amAttendance;
    }

    public String getAmNoAttendance() {
        return amNoAttendance;
    }

    public void setAmNoAttendance(String amNoAttendance) {
        this.amNoAttendance = amNoAttendance;
    }

    public String getPmAttendance() {
        return pmAttendance;
    }

    public void setPmAttendance(String pmAttendance) {
        this.pmAttendance = pmAttendance;
    }

    public String getPmNoAttendance() {
        return pmNoAttendance;
    }

    public void setPmNoAttendance(String pmNoAttendance) {
        this.pmNoAttendance = pmNoAttendance;
    }

    public String getNiAttendance() {
        return niAttendance;
    }

    public void setNiAttendance(String niAttendance) {
        this.niAttendance = niAttendance;
    }

    public String getNiNoAttendance() {
        return niNoAttendance;
    }

    public void setNiNoAttendance(String niNoAttendance) {
        this.niNoAttendance = niNoAttendance;
    }





}
