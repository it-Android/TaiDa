package com.arcsoft.arcfacedemo.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;

import java.util.List;

public class FactoryFragment extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private FragmentTemperature ft;//温度
    private FragmentHumidity fh;//湿度
    private FragmentCarbonDioxide fc;//二氧化碳
    private FragmentNoise fn;//噪声

    public FactoryFragment(FragmentManager fm) {
        super(fm);
        ft=new FragmentTemperature();
        fh=new FragmentHumidity();
        fc=new FragmentCarbonDioxide();
        fn=new FragmentNoise();
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment=null;
        switch (i){
            case OverallSituation.HISTORY_TEMPERATURE:
                fragment=ft;
                break;
            case OverallSituation.HISTORY_HUMIDITY:
                fragment=fh;
                break;
            case OverallSituation.HISTORY_CARBONDIOXIDE:
                fragment=fc;
                break;
            case OverallSituation.HISTORY_NOSIE:
                fragment=fn;
                break;
        }
        return fragment;
    }

    //向fragment传递数据
    public void setData(List<SensorHistoryData> list){
        ft.setData(list);
        fh.setData(list);
        fc.setData(list);
        fn.setData(list);
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
