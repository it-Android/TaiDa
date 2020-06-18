package com.arcsoft.arcfacedemo.faceserver;

/**
 * Created by Administrator on 2019/5/18.
 */

public class TimeInfo {

    private String PersonName;
    private boolean state;
    private String time ;
    public TimeInfo(String PersonName, boolean state, String time )
    {
        this.PersonName =PersonName;
        this.state = state;
        this.time = time;
    }


    public String getPersonName() {
        return PersonName;
    }

    public void setPersonName(String personName) {
        PersonName = personName;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
