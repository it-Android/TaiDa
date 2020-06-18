package com.arcsoft.arcfacedemo.Tool.Json.Analysis;



import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;
import com.arcsoft.arcfacedemo.Tool.Data.ServerData;

import org.json.JSONObject;

public class JsonDataAnalysis {

    private final static String TYPE="Type";
    private final static String DATA="Data";

    private final static String TYPE_VALUN="aaaa";
    //解析传感器数据
    public static Sensor getSensor11(String jsonData){
        Sensor sensor=new Sensor();
        try{
            JSONObject object=new JSONObject(jsonData);
            if(object.getString(TYPE).equals(TYPE_VALUN)){
                JSONObject jsonObject=new JSONObject(object.getString(DATA));
                sensor.setDate(jsonObject.getString(Sensor.SENSOR_DATA_DATE));//获取时间
                sensor.setTemperature(Float.parseFloat(jsonObject.getString(Sensor.SENSOR_DATA_TEMPERATURE)));//获取温度
                sensor.setCarbon_dioxide(Float.parseFloat(jsonObject.getString(Sensor.SENSOR_DATA_CARBON_DIOXIDE)));//获取二氧化碳
                sensor.setHumidity(Float.parseFloat(jsonObject.getString(Sensor.SENSOR_DATA_HUMIDITY)));//获取湿度
                sensor.setNoise(Float.parseFloat(jsonObject.getString(Sensor.SENSOR_DATA_NOISE)));//获取噪声
                sensor.setSmoke(Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_SMOKE)));//获取烟雾

                sensor.setFlame(Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_FLAME)));//获取火焰

                sensor.setBulb1( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_1)));//获取灯泡1
                sensor.setBulb2( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_2)));//获取灯泡2
                sensor.setBulb3( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_3)));//获取灯泡3
                sensor.setBulb4( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_4)));//获取灯泡4
                sensor.setBulb5( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_5)));//获取灯泡5
                sensor.setBulb6( Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_BULB_6)));//获取灯泡6
                sensor.setFan(Integer.parseInt(jsonObject.getString(Sensor.SENSOR_DATA_FAN)));//获取风扇
            }

        }catch (Exception e){}

        return sensor;

    }


    public static ServerData getServerData(String jsonData){
        ServerData serverData=new ServerData();

        try{
            JSONObject object=new JSONObject(jsonData);
            if(object.getString(TYPE).equals(TYPE_VALUN)){
                JSONObject jsonObject=new JSONObject(object.getString(DATA));
                serverData.setDate(jsonObject.getString(ServerData.DATE));
                //模拟量
                serverData.setTemperature(Float.parseFloat(jsonObject.getString(ServerData.TEMPERATURE)));
                serverData.setHumidity(Float.parseFloat(jsonObject.getString(ServerData.HUMIDITY)));
                serverData.setIllumination(Float.parseFloat(jsonObject.getString(ServerData.ILLUMINATION)));
                serverData.setCarbon_dioxide(Float.parseFloat(jsonObject.getString(ServerData.CARBON_DIOXIDE)));

                //数字量
                serverData.setFlame(Integer.parseInt(jsonObject.getString(ServerData.FLAME)));
                serverData.setFace(Integer.parseInt(jsonObject.getString(ServerData.FACE)));

                //数字量
                serverData.setSmoke(Integer.parseInt(jsonObject.getString(ServerData.SMOKE)));
                serverData.setIntelligence(Integer.parseInt(jsonObject.getString(ServerData.INTELLIGENCE)));
                serverData.setLamp(Integer.parseInt(jsonObject.getString(ServerData.LAMP)));
                serverData.setAllLamp(Integer.parseInt(jsonObject.getString(ServerData.ALLLAMP)));
                serverData.setThreshold(Integer.parseInt(jsonObject.getString(ServerData.THRESHOLE)));

                serverData.setFan(Integer.parseInt(jsonObject.getString(ServerData.FAN)));

            }
            return serverData;



        }catch (Exception e){

        }
        return serverData;

    }



}
