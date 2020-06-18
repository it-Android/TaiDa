package com.arcsoft.arcfacedemo.Tool.Json.Modify;

import com.arcsoft.arcfacedemo.Tool.Data.JsonNotice;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDataModify {


    public String jsonNoticeToString(JsonNotice data){

        Map<String,Object> mainMap=new HashMap<>();
        Map<String,String> map=new HashMap<>();
        map.put(JsonNotice.JSONNOITCE_FACE,""+data.getFace());
        map.put(JsonNotice.JSONNOITCE_THRESHOLD,""+data.getThreshold());
        map.put(JsonNotice.JSONNOITCE_LAMP,""+data.getLamp());
        map.put(JsonNotice.JSONNOITCE_ALL_LAMP,""+data.getAllLamp());
        map.put(JsonNotice.JSONNOITCE_INTELLIGENCE_LAMP,""+data.getIntelligence());
        mainMap.put("Type","upData");
        mainMap.put("Data",map);
        return new JSONObject(mainMap).toString();

    }





}
