package com.arcsoft.arcfacedemo.faceserver;


import java.util.Map;

public class CompareResult {
    private String user_Name;
    private String user_num;
    private String user_stateTime;
    private boolean user_state ;
    private float similar;
    private int TimeCompare;
    private String Time_success;
    private int trackId; // track ID
   private Map<String,Object>p;
    public CompareResult(String userName, float similar, String user_num , String user_stateTime, boolean user_state , int TimeCompare, String Time_success) {
        this.user_Name = userName;
        this.user_num = user_num;
        this.user_stateTime = user_stateTime;
        this.user_state = user_state;
        this.TimeCompare = TimeCompare;
        this.similar = similar;
        this.Time_success = Time_success;
    }




    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public String getUser_num() {
        return user_num;
    }

    public void setUser_num(String user_num) {
        this.user_num = user_num;
    }

    public String getUser_stateTime() {
        return user_stateTime;
    }

    public void setUser_stateTime(String user_stateTime) {
        this.user_stateTime = user_stateTime;
    }


    public boolean isUser_state() {
        return user_state;
    }

    public void setUser_state(boolean user_state) {
        this.user_state = user_state;
    }

    public int getTimeCompare() {
        return TimeCompare;
    }

    public void setTimeCompare(int timeCompare) {
        TimeCompare = timeCompare;
    }

    public String getTime_success() {
        return Time_success;
    }

    public void setTime_success(String time_success) {
        Time_success = time_success;
    }

    public Map<String ,Object> getMap(){ return p;}
}
