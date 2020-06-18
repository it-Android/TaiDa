package com.arcsoft.arcfacedemo.Tool.Data;

public class Sensor {
    public final static String SENSOR_DATA_DATE="Date";//传感器数据的时间
    public final static String SENSOR_DATA_TEMPERATURE="Temperature";//温度
    public final static String SENSOR_DATA_CARBON_DIOXIDE="Carbon_dioxide";//二氧化碳
    public final static String SENSOR_DATA_HUMIDITY="Humidity";//温度
    public final static String SENSOR_DATA_NOISE="Noise";//噪声
    public final static String SENSOR_DATA_SMOKE="Smoke";//烟雾

    public final static String SENSOR_DATA_FLAME="Flame";//火焰
    public final static String SENSOR_DATA_BULB_1="Bulb1";//灯泡1
    public final static String SENSOR_DATA_BULB_2="Bulb2";//灯泡2
    public final static String SENSOR_DATA_BULB_3="Bulb3";//灯泡3
    public final static String SENSOR_DATA_BULB_4="Bulb4";//灯泡4
    public final static String SENSOR_DATA_BULB_5="Bulb5";//灯泡5
    public final static String SENSOR_DATA_BULB_6="Bulb6";//灯泡
    public final static String SENSOR_DATA_FAN="Fun";//风扇

    private String date="";
    private float Temperature=0;
    private float Carbon_dioxide=0;
    private float Humidity=0;
    private float Noise=0;

    private int Smoke=0;
    private int Flame=0;
    private int Bulb1=0;
    private int Bulb2=0;
    private int Bulb3=0;
    private int Bulb4=0;
    private int Bulb5=0;
    private int Bulb6=0;
    private int Fan=0;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTemperature() {
        return Temperature;
    }

    public void setTemperature(float temperature) {
        Temperature = temperature;
    }

    public float getCarbon_dioxide() {
        return Carbon_dioxide;
    }

    public void setCarbon_dioxide(float carbon_dioxide) {
        Carbon_dioxide = carbon_dioxide;
    }

    public float getHumidity() {
        return Humidity;
    }

    public void setHumidity(float humidity) {
        Humidity = humidity;
    }

    public float getNoise() {
        return Noise;
    }

    public void setNoise(float noise) {
        Noise = noise;
    }

    public int getSmoke() {
        return Smoke;
    }

    public void setSmoke(int smoke) {
        Smoke = smoke;
    }

    public int getFlame() {
        return Flame;
    }

    public void setFlame(int flame) {
        Flame = flame;
    }

    public int getBulb1() {
        return Bulb1;
    }

    public void setBulb1(int bulb1) {
        Bulb1 = bulb1;
    }

    public int getBulb2() {
        return Bulb2;
    }

    public void setBulb2(int bulb2) {
        Bulb2 = bulb2;
    }

    public int getBulb3() {
        return Bulb3;
    }

    public void setBulb3(int bulb3) {
        Bulb3 = bulb3;
    }

    public int getBulb4() {
        return Bulb4;
    }

    public void setBulb4(int bulb4) {
        Bulb4 = bulb4;
    }

    public int getBulb5() {
        return Bulb5;
    }

    public void setBulb5(int bulb5) {
        Bulb5 = bulb5;
    }

    public int getBulb6() {
        return Bulb6;
    }

    public void setBulb6(int bulb6) {
        Bulb6 = bulb6;
    }

    public int getFan() {
        return Fan;
    }

    public void setFan(int fan) {
        Fan = fan;
    }
}
