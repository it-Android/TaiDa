package com.arcsoft.arcfacedemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.arcsoft.arcfacedemo.Fragment.FactoryFragment;
import com.arcsoft.arcfacedemo.MyView.ViewPagerSlide;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;
import com.arcsoft.arcfacedemo.Tool.Data.SensorHistoryData;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,ViewPager.OnPageChangeListener {
    private Toolbar toolbar;
    private ViewPagerSlide viewPager;
    private FactoryFragment ff;
    private RadioGroup radioGroup;
    private RadioButton rbTemperature,rbHumidity,rbCarbonDioxide,rbNoise;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    //初始化
    private void init(){
        rbTemperature=(RadioButton)findViewById(R.id.history_Temperature);
        rbHumidity=(RadioButton)findViewById(R.id.history_Humidity);
        rbCarbonDioxide=(RadioButton)findViewById(R.id.history_CarbonDioxide);
        rbNoise=(RadioButton)findViewById(R.id.history_Noise);
        viewPager=(ViewPagerSlide) findViewById(R.id.history_vpContainer);
        viewPager.setOffscreenPageLimit(4);//预加载
        ff=new FactoryFragment(getSupportFragmentManager());
        viewPager.setAdapter(ff);//向viewPager里面添加碎片
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(HistoryActivity.this);
        radioGroup=(RadioGroup)findViewById(R.id.history_rgContainer);
        radioGroup.setOnCheckedChangeListener(HistoryActivity.this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HandlerNumberRecord.DATA_ANALYSIS_777:
                        List<SensorHistoryData> list=(List<SensorHistoryData>)msg.obj;
                        ff.setData(list);
                        break;
                }
            }
        };
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,Context.MODE_PRIVATE);
        DataAnalysis analysis=new DataAnalysis(handler);
        analysis.getSensorHistoryData(sp.getString(OverallSituation.USER_IP,""),"20");
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.history_Temperature:
                viewPager.setCurrentItem(OverallSituation.HISTORY_TEMPERATURE);
                break;
            case R.id.history_Humidity:
                viewPager.setCurrentItem(OverallSituation.HISTORY_HUMIDITY);
                break;
            case R.id.history_CarbonDioxide:
                viewPager.setCurrentItem(OverallSituation.HISTORY_CARBONDIOXIDE);
                break;
            case R.id.history_Noise:
                viewPager.setCurrentItem(OverallSituation.HISTORY_NOSIE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {

    }

    //ViewPager
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (viewPager.getCurrentItem()) {
                case OverallSituation.HISTORY_TEMPERATURE:
                    rbTemperature.setChecked(true);
                    break;
                case OverallSituation.HISTORY_HUMIDITY:
                    rbHumidity.setChecked(true);
                    break;
                case OverallSituation.HISTORY_CARBONDIOXIDE:
                    rbCarbonDioxide.setChecked(true);
                    break;
                case OverallSituation.HISTORY_NOSIE:
                    rbNoise.setChecked(true);
                    break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
