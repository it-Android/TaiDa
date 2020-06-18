package com.arcsoft.arcfacedemo.Tool.Data;

public class SensorHistoryData {
    public final static String DATE="date";//时间
    public final static String TEMPERATURE="temperature";//
    public final static String CARBON_DIOXIDE="carbon_dioxide";//二氧化碳
    public final static String HUMIDITY="humidity";//温度
    public final static String SENSOR_DATA_SMOKE="smoke";//烟雾
    public final static String FLAME="flame";//火焰
    public final static String ILLUMINATION="illumination";//光照



    private String date="";
    private float temperature=0;
    private float carbon_dioxide=0;
    private float humidity=0;

    private float illumination=0;
    private float flame=0;
    private float smoke=0;

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

    public float getCarbon_dioxide() {
        return carbon_dioxide;
    }

    public void setCarbon_dioxide(float carbon_dioxide) {
        this.carbon_dioxide = carbon_dioxide;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getIllumination() {
        return illumination;
    }

    public void setIllumination(float illumination) {
        this.illumination = illumination;
    }

    public float getFlame() {
        return flame;
    }

    public void setFlame(float flame) {
        this.flame = flame;
    }

    public float getSmoke() {
        return smoke;
    }

    public void setSmoke(float smoke) {
        this.smoke = smoke;
    }



}
