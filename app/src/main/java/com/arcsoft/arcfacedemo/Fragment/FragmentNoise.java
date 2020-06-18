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

public class FragmentNoise extends Fragment {
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
            dynamic.addEntry(historyData.getIllumination(),historyData.getDate());
        }
    }
    private void init(){
        lcTemperature=view.findViewById(R.id.histort_fm_temperature);
        dynamic=new DynamicLineChartManager(lcTemperature,"光照/ln",Color.GREEN,5,null);
        dynamic.setYAxis(30000,0,10);
    }

}
