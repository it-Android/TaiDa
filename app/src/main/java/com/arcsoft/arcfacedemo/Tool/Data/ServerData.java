package com.arcsoft.arcfacedemo.Tool.Data;

public class ServerData {

    public final static String DATE="Date";
    public final static String TEMPERATURE="Temperature";
    public final static String HUMIDITY="Humidity";
    public final static String CARBON_DIOXIDE="Carbon_dioxide";
    public final static String ILLUMINATION="Illumination";
    public final static String SMOKE="Smoke";
    public final static String FLAME="Flame";
    public final static String INTELLIGENCE="Intelligence";
    public final static String THRESHOLE="Threshold";
    public final static String FACE="Face";
    public final static String LAMP="Lamp";

    public final static String ALLLAMP="AllLamp";
    public final static String FAN="Fan";



    private String date="";
    private float temperature=0;
    private float humidity=0;
    private float carbon_dioxide=0;
    private float illumination=0;
    private int smoke=0;
    private int flame=0;
    private int intelligence=0;
    private int threshold=0;
    private int lamp=0;
    private int face=0;
    private int allLamp=0;
    private int fan=0;




    public int getFan() {
        return fan;
    }

    public void setFan(int fan) {
        this.fan = fan;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public float getIllumination() {
        return illumination;
    }

    public void setIllumination(float illumination) {
        this.illumination = illumination;
    }

    public int getSmoke() {
        return smoke;
    }

    public void setSmoke(int smoke) {
        this.smoke = smoke;
    }

    public int getFlame() {
        return flame;
    }

    public void setFlame(int flame) {
        this.flame = flame;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
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

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getAllLamp() {
        return allLamp;
    }

    public void setAllLamp(int allLamp) {
        this.allLamp = allLamp;
    }



}
