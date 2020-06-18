package com.arcsoft.arcfacedemo.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Service.BackstageSockectServices;
import com.arcsoft.arcfacedemo.Tool.Data.JsonNotice;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.Json.Modify.JsonDataModify;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
import com.arcsoft.arcfacedemo.Tool.SQL.Modify.DataModify;
import com.arcsoft.arcfacedemo.Tool.System.ScreenUtils;

public class ThresholdDialog implements SeekBar.OnSeekBarChangeListener,View.OnClickListener{
    private Context context;
    private Handler handler;
    private AlertDialog dialog;

    private BackstageSockectServices bss;
    private Button btnDetermine,btnCancel;

    private TextView tvCarbonDioxide,tvTemperature,tvHumidity,tvIlluminationMax,tvIlluminationMin;

    private SeekBar sbCarbonDioxide,sbTemperature,sbHumidity,sbIlluminationMax,sbIlluminationMin;

    private ImageView ivCarbonDioxideAdd,ivCarbonDioxideReduce;//二氧化碳
    private ImageView ivTemperatureAdd,ivTemperatureReduce;//温度
    private ImageView ivHumidityAdd,ivHumidityReduce;//湿度
    private ImageView ivIlluminationMaxAdd,ivIlluminationMaxReduce;//光照
    private ImageView ivIlluminationMinAdd,ivIlluminationMinReduce;//光照

    private final static int CARBONDIOXDE_START=100, CARBONDIOXDE_MAX=1000,CARBONDIOXDE_MIN=0;//二氧化碳

    private final static int TEMPERATURE_START=10,TEMPERATURE_MAX=40,TEMPERATURE_MIN=0;//温度

    private final static int HUMIDITY_START=10,HUMIDITY_MAX=90,HUMIDITY_MIN=0;//湿度

    private final static int ILLUMINATION_MAX_START=200,ILLUMINATION_MAX_MAX=400,ILLUMINATION_MAX_MIN=0;//光照最大
    private final static int ILLUMINATION_MIN_START=0,ILLUMINATION_MIN_MAX=200,ILLUMINATION_MIN_MIN=0;//光照最小

    private int carbonDioxideNumber=0,temperatureNumber=0,humidityNumber=0 ,illuminationMax=0,illuminationMin=0;//保存值

    private  SharedPreferences sp;
    private SetUpData oldSetUpData;
    public ThresholdDialog(final Context context, final BackstageSockectServices bss) {
        this.bss=bss;
        this.context = context;
        oldSetUpData=new SetUpData();
        sp=context.getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,Context.MODE_PRIVATE);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 88842:
                        if((Boolean) msg.obj){
                            JsonNotice jsonNotice=new JsonNotice();
                            jsonNotice.setThreshold(1);
                            bss.socketOutput(new JsonDataModify().jsonNoticeToString(jsonNotice),(byte) 0x01);
                            dialog.dismiss();
                            Toast.makeText(context,"修改成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context,"修改失败！",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 42:
                        oldSetUpData=(SetUpData)msg.obj;
                        sbCarbonDioxide.setProgress((int)oldSetUpData.getCarbon_dioxide()-CARBONDIOXDE_START);
                        sbTemperature.setProgress((int)oldSetUpData.getTemperature()-TEMPERATURE_START);
                        sbHumidity.setProgress((int)oldSetUpData.getHumidity()-HUMIDITY_START);
                        sbIlluminationMax.setProgress((int)oldSetUpData.getIllumination_max()-ILLUMINATION_MAX_START);
                        sbIlluminationMin.setProgress((int)oldSetUpData.getIllumination_min()-ILLUMINATION_MIN_START);
                        break;
                }
            }
        };


        DataAnalysis analysis=new DataAnalysis(handler);
        analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),sp.getString(OverallSituation.USER_NAME,""));
        //analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),"admin");



    }

    public void showDialog(){
        View view=LayoutInflater.from(context).inflate(R.layout.threshold_dialog,null,false);
         dialog = new AlertDialog.Builder(context).setView(view).setCancelable(false).create();
        //dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);


        //按钮
        btnDetermine=(Button)view.findViewById(R.id.threshold_btnDetermine);
        btnCancel=(Button)view.findViewById(R.id.threshold_btnCancel);

        //二氧化碳
        ivCarbonDioxideAdd=(ImageView)view.findViewById(R.id.threshold_carbon_dioxide_add) ;
        ivCarbonDioxideReduce=(ImageView)view.findViewById(R.id.threshold_carbon_dioxide_reduce);
        ivCarbonDioxideAdd.setOnClickListener(this);
        ivCarbonDioxideReduce.setOnClickListener(this);
        //温度
        ivTemperatureAdd=(ImageView)view.findViewById(R.id.threshold_temperature_add);
        ivTemperatureReduce=(ImageView)view.findViewById(R.id.threshold_temperature_reduce);
        ivTemperatureAdd.setOnClickListener(this);
        ivTemperatureReduce.setOnClickListener(this);

        //湿度
        ivHumidityAdd=(ImageView)view.findViewById(R.id.threshold_humidity_add);
        ivHumidityReduce=(ImageView)view.findViewById(R.id.threshold_humidity_reduce);
        ivHumidityAdd.setOnClickListener(this);
        ivHumidityReduce.setOnClickListener(this);

        //最大光照值
        ivIlluminationMaxAdd=(ImageView)view.findViewById(R.id.threshold_IlluminationMax_add);
        ivIlluminationMaxReduce=(ImageView)view.findViewById(R.id.threshold_IlluminationMax_reduce);
        ivIlluminationMaxAdd.setOnClickListener(this);
        ivIlluminationMaxReduce.setOnClickListener(this);

        //最小光照值
        ivIlluminationMinAdd=(ImageView)view.findViewById(R.id.threshold_IlluminationMin_add);
        ivIlluminationMinReduce=(ImageView)view.findViewById(R.id.threshold_IlluminationMin_reduce);
        ivIlluminationMinAdd.setOnClickListener(this);
        ivIlluminationMinReduce.setOnClickListener(this);

        //数值显示
        tvCarbonDioxide=(TextView)view.findViewById(R.id.threshold_carbon_dioxide_tvNumber);
        tvTemperature=(TextView)view.findViewById(R.id.threshold_temperature_tvNumber);
        tvHumidity=(TextView)view.findViewById(R.id.threshold_humidity_tvNumber);
        tvIlluminationMax=(TextView)view.findViewById(R.id.threshold_IlluminationMax_tvNumber);
        tvIlluminationMin=(TextView)view.findViewById(R.id.threshold_IlluminationMin_tvNumber);
        //SeekBar控件
        sbCarbonDioxide=(SeekBar)view.findViewById(R.id.threshold_carbon_dioxide_seekbar) ;
        sbCarbonDioxide.setMax(CARBONDIOXDE_MAX);

        sbTemperature=(SeekBar)view.findViewById(R.id.threshold_temperature_seekbar);
        sbTemperature.setMax(TEMPERATURE_MAX);

        sbHumidity=(SeekBar)view.findViewById(R.id.threshold_humidity_seekbar);
        sbHumidity.setMax(HUMIDITY_MAX);

        sbIlluminationMax=(SeekBar)view.findViewById(R.id.threshold_IlluminationMax_seekbar);
        sbIlluminationMax.setMax(ILLUMINATION_MAX_MAX);

        sbIlluminationMin=(SeekBar)view.findViewById(R.id.threshold_IlluminationMin_seekbar);
        sbIlluminationMin.setMax(ILLUMINATION_MAX_MAX);


        sbTemperature.setOnSeekBarChangeListener(this);
        sbCarbonDioxide.setOnSeekBarChangeListener(this);
        sbHumidity.setOnSeekBarChangeListener(this);
        sbIlluminationMax.setOnSeekBarChangeListener(this);
        sbIlluminationMin.setOnSeekBarChangeListener(this);
        //确定
        btnDetermine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetUpData data=new SetUpData();
                data.setAdmin_UserName(sp.getString(OverallSituation.USER_NAME,"admin"));//获取管理员
                //data.setAdmin_UserName("admin");
                data.setTemperature(temperatureNumber+TEMPERATURE_START);
                data.setHumidity(humidityNumber+HUMIDITY_START);
                data.setCarbon_dioxide(carbonDioxideNumber+CARBONDIOXDE_START);
                data.setIllumination_max(illuminationMax+ILLUMINATION_MAX_START);
                data.setIllumination_min(illuminationMin+ILLUMINATION_MIN_START);

                DataModify analysis=new DataModify(handler);
                analysis.setSetUpData(sp.getString(OverallSituation.USER_IP,""),data);
            }
        });

        //取消
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context)*5/7),(ScreenUtils.getScreenHeight(context)*5/6));

    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //二氧化碳
            case R.id.threshold_carbon_dioxide_add:
                if(carbonDioxideNumber>=CARBONDIOXDE_MAX)return;
                carbonDioxideNumber++;
                sbCarbonDioxide.setProgress(carbonDioxideNumber);
                break;
            case R.id.threshold_carbon_dioxide_reduce:
                if(carbonDioxideNumber<=CARBONDIOXDE_MIN)return;
                carbonDioxideNumber--;
                sbCarbonDioxide.setProgress(carbonDioxideNumber);
                break;
            //温度
            case R.id.threshold_temperature_add:
                if(temperatureNumber>=TEMPERATURE_MAX)return;
                temperatureNumber++;
                sbTemperature.setProgress(temperatureNumber);
                break;
            case R.id.threshold_temperature_reduce:
                if(temperatureNumber<=TEMPERATURE_MIN)return;
                temperatureNumber--;
                sbTemperature.setProgress(temperatureNumber);
                break;
            //湿度
            case R.id.threshold_humidity_add:
                if(humidityNumber>=HUMIDITY_MAX)return;
                humidityNumber++;
                sbHumidity.setProgress(humidityNumber);
                break;
            case R.id.threshold_humidity_reduce:
                if(humidityNumber<=HUMIDITY_MIN)return;
                humidityNumber--;
                sbHumidity.setProgress(humidityNumber);
                break;

             //最大光照值
            case R.id.threshold_IlluminationMax_add:
                if(illuminationMax>=ILLUMINATION_MAX_MAX)return;
                illuminationMax++;
                sbIlluminationMax.setProgress(illuminationMax);
                break;
            case R.id.threshold_IlluminationMax_reduce:
                if(illuminationMax<=ILLUMINATION_MAX_MIN)return;
                illuminationMax--;
                sbIlluminationMax.setProgress(illuminationMax);
                break;

            case R.id.threshold_IlluminationMin_add:
                if(illuminationMin>=ILLUMINATION_MIN_MAX)return;
                illuminationMin++;
                sbIlluminationMin.setProgress(illuminationMin);
                break;
            case R.id.threshold_IlluminationMin_reduce:
                if(illuminationMin<=ILLUMINATION_MIN_MIN)return;
                illuminationMin--;
                sbIlluminationMin.setProgress(illuminationMin);
                break;

        }
    }

    //SeekBar滚动过程中的回调函数
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.threshold_carbon_dioxide_seekbar:
                carbonDioxideNumber =progress;
                tvCarbonDioxide.setText(""+(carbonDioxideNumber+CARBONDIOXDE_START+"vpn"));
                break;
            case R.id.threshold_temperature_seekbar:
                temperatureNumber=progress;
                tvTemperature.setText(""+(temperatureNumber+TEMPERATURE_START)+"℃");
                break;
            case R.id.threshold_humidity_seekbar:
                humidityNumber=progress;
                tvHumidity.setText(""+(humidityNumber+HUMIDITY_START)+"%");
                break;
            case R.id.threshold_IlluminationMax_seekbar:
                illuminationMax=progress;
                tvIlluminationMax.setText(""+(illuminationMax+ILLUMINATION_MAX_START)+"lx ");
                break;
            case R.id.threshold_IlluminationMin_seekbar:
                illuminationMin=progress;
                tvIlluminationMin.setText(""+(illuminationMin+ILLUMINATION_MIN_START)+"lx ");
                break;

        }
    }

    //SeekBar开始滚动的回调函数
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    //SeekBar停止滚动的回调函数
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
