package com.arcsoft.arcfacedemo.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arcsoft.arcfacedemo.MyView.DynamicLineChartManager;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;
import com.github.mikephil.charting.charts.LineChart;

import java.util.List;

public class FragmentCarbonDioxide extends Fragment {
    private View view;
    private LineChart lcTemperature;
    private DynamicLineChartManager dynamic;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_temperature,container,false);
        init();
        return view;
    }
    public void setData(List<SensorHistoryData> list){
        for(SensorHistoryData historyData:list){
            dynamic.addEntry(historyData.getCarbon_dioxide(),historyData.getDate());
        }
    }
    private void init(){
        lcTemperature=view.findViewById(R.id.histort_fm_temperature);
        dynamic=new DynamicLineChartManager(lcTemperature,"二氧化碳/vpn",Color.BLUE,5,null);
        dynamic.setYAxis(2000,0,10);

    }
}
