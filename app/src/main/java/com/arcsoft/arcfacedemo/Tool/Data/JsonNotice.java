package com.arcsoft.arcfacedemo.Tool.Data;

public class JsonNotice {
    public final static String JSONNOITCE_FACE="Face";//人脸数据更新
    public final static String JSONNOITCE_THRESHOLD="Threshold";//阈值数据更新
    public final static String JSONNOITCE_LAMP="Lamp";//开灯
    public final static String JSONNOITCE_INTELLIGENCE_LAMP="Intelligence";//智能控制
    public final static String JSONNOITCE_ALL_LAMP="AllLamp";//全部灯控制

    private int face=0;
    private int threshold=0;
    private int lamp=0;
    private int intelligence=0;
    private int allLamp=0;

    public int getAllLamp() {
        return allLamp;
    }

    public void setAllLamp(int allLamp) {
        this.allLamp = allLamp;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getLamp() {
        return lamp;
    }

    public void setLamp(int lamp) {
        this.lamp = lamp;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }


}
