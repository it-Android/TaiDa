package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.BroadcastReceiver.UpDateReceiver;
import com.arcsoft.arcfacedemo.Dialog.ThresholdDialog;
import com.arcsoft.arcfacedemo.Dialog.TipsDialog;
import com.arcsoft.arcfacedemo.MyView.DynamicLineChartManager;
import com.arcsoft.arcfacedemo.MyView.MonitorView;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Service.BackstageSockectServices;
import com.arcsoft.arcfacedemo.Service.FaceDownloadService;
import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.ServerData;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.Json.Analysis.JsonDataAnalysis;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
import com.arcsoft.arcfacedemo.Tool.SQL.Modify.DataModify;
import com.arcsoft.arcfacedemo.Tool.System.SignIn;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;
import com.arcsoft.arcfacedemo.common.Constants;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Observer;
public class ControlActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private Handler handler;//消息
    private LineChart lcTemperature,lcHumidity,lcCarbonDioxide,lcNoise;//数据显示
    private DynamicLineChartManager dcTemperature,dcHumidity,dcCarbonDioxide,dcNoise;//数据初始并赋值

    private UpDateReceiver upDateReceiver;//广播
    private Attendance oldAtt;

    private BackstageSockectServices bss;//services对象

    private Button btnHistory,btnThreshold,btnMonitor,btnFaceAttendance,btnRegister,btnEmployeeInformation;//侧划菜单按钮
    private TextView conFan;


    private ServerData serverData;//保存传感器数据
    private List<Integer> list = new ArrayList<>(); //数据集合
    private List<String> names = new ArrayList<>(); //折线名字集合
    private List<Integer> colour = new ArrayList<>();//折线颜色集合

    private int oldSmoke=0,oldFlame=0,oldFan=-1,oldIntelligence=0,oldLamp=0,oldAllLamp=0;

    private static boolean mBackKeyPressed = false;//记录是否有首次按键

    private SetUpData setUpData=new SetUpData();//阈值

//    private CheckBox cbLamp,cbSecurity,cbWorkshop;

    private DrawerLayout drawerLayout;
    private AnimationDrawable mvAnimationflame,mvAnimationSmoke,ivAnimationFan;
    private MonitorView mvSmoke,mvFlame;
    private ImageView ivFan;
    private String userName,passWord,identity;
    private TipsDialog tipsDialog;


    private int fanText=0;




    //相机激活
    private Toast toast = null;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        activeEngine(null);
        try{
            Intent intent=getIntent();
            Bundle bundle= intent.getBundleExtra("userData");
            userName=bundle.getString("userName");//账号
            passWord=bundle.getString("passWord");//密码
            identity=bundle.getString("identity");//身份

        }catch (Exception e){}
        init();//初始化基本控件
        initLineChart();//初始化LineChart
        initData();//初始化数据接收
    }
    private void init(){
        setUpData=new SetUpData();//阈值
        drawerLayout=(DrawerLayout)findViewById(R.id.control_DrawerLayout);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        toolbar.setOnMenuItemClickListener(onMenuItemClick);//菜单点击监听
        btnThreshold=(Button)findViewById(R.id.control_btnThreshold);//阈值控制
        btnMonitor=(Button)findViewById(R.id.control_btnMonitor);//视频监控
        btnFaceAttendance=(Button)findViewById(R.id.control_btnFaceAttendance);//人脸考勤
        btnRegister=(Button)findViewById(R.id.control_btnRegister);//人脸注册
        btnEmployeeInformation=(Button)findViewById(R.id.control_btnEmployeeInformation);//员工信息
        btnHistory=(Button)findViewById(R.id.control_btnHistory);//历史记录

        /*cbLamp=(CheckBox)findViewById(R.id.control_cbLamp);
        cbSecurity=(CheckBox)findViewById(R.id.control_cbSecurity);
        cbWorkshop=(CheckBox)findViewById(R.id.control_cbWorkshop);*/

        conFan=(TextView)findViewById(R.id.control_fan_text) ;


        /*cbLamp.setOnCheckedChangeListener(this);
        cbSecurity.setOnCheckedChangeListener(this);
        cbWorkshop.setOnCheckedChangeListener(this);*/

        if(identity!=null&&identity.equals(LoginActivity.IDENTITY_ADMIN)){
            btnEmployeeInformation.setText("员工信息");
        }else{
            btnEmployeeInformation.setText("我的信息");
        }

        btnHistory.setOnClickListener(this);
        btnMonitor.setOnClickListener(this);
        btnThreshold.setOnClickListener(this);
        btnFaceAttendance.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnEmployeeInformation.setOnClickListener(this);

        ivFan=(ImageView)findViewById(R.id.control_ivFan) ;
        ivAnimationFan=(AnimationDrawable)ivFan.getBackground();
        ivAnimationFan.start();

        mvSmoke=(MonitorView)findViewById(R.id.control_mvSmoke);//烟雾监控
        mvAnimationSmoke=(AnimationDrawable) mvSmoke.getBackground();
        mvAnimationSmoke.start();//默认动画为启动

        mvFlame=(MonitorView) findViewById(R.id.fun_adm_imageView);//火焰监控
        mvAnimationflame=(AnimationDrawable) mvFlame.getBackground();
        mvAnimationflame.start();

        oldAtt=new Attendance();//某天的记录

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HandlerNumberRecord.SERVERDATA_0://接收服务器发送过来的字符串数据，并 解析
                        handler_0(msg);//解析，发配
                        break;
                    case 666:
                        List<Map<String,Object>> mma=(List<Map<String,Object>>) msg.obj;
                        break;
                    case 19://获取单个员工的某天的记录
                        oldAtt=(Attendance) msg.obj;
                        break;
                    case 42:
                        setUpData=(SetUpData)msg.obj;
                       /* if(setUpData.getIntelligence()==1){
                            if (cbLamp.isChecked())break;
                            cbLamp.setChecked(true);
                        }else if(setUpData.getIntelligence()==0){
                            if (!cbLamp.isChecked())break;
                            cbLamp.setChecked(false);
                        }*/
                        break;
                    case HandlerNumberRecord.DISCONNECT_10000://与服务器断开连接
                        startActivity(new Intent(ControlActivity.this,LoginActivity.class));//跳回到登录界面
                        finish();//关闭当前Activity
                        break;
                }
            }
        };

        //identity=LoginActivity.IDENTITY_ADMIN;
        if(identity!=null&&identity.equals(LoginActivity.IDENTITY_ADMIN)){
            btnThreshold.setEnabled(true);
            btnMonitor.setEnabled(true);
            SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
            DataAnalysis analysis=new DataAnalysis(handler);
            analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),sp.getString(OverallSituation.USER_NAME,""));
        }else{
            btnThreshold.setEnabled(false);
            btnMonitor.setEnabled(false);
        }

        Intent intentService=new Intent(ControlActivity.this,BackstageSockectServices.class);
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        intentService.putExtra(OverallSituation.USER_IP,sp.getString(OverallSituation.USER_IP,""));
        intentService.putExtra(OverallSituation.USER_PORT,sp.getString(OverallSituation.USER_PORT,""));
        bindService(intentService,connection,BIND_AUTO_CREATE);

        //获取阈值
        DataAnalysis analysis=new DataAnalysis(handler);
        analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),sp.getString(OverallSituation.USER_NAME,""));
    }

    //初始化数据显示曲线图
    private void initLineChart(){
        //折线名字
        names.add("温度");
        names.add("湿度");
        names.add("二氧化碳");
        names.add("光照强度");
        //折线颜色
        colour.add(Color.CYAN);
        colour.add(Color.GREEN);
        colour.add(Color.BLUE);
        colour.add(Color.YELLOW);

        lcTemperature=(LineChart)findViewById(R.id.control_lcTemperature);//温度
        lcHumidity=(LineChart)findViewById(R.id.control_lcHumidity);//湿度
        lcCarbonDioxide=(LineChart)findViewById(R.id.control_lcCarbonDioxide);//二氧化碳
        lcNoise=(LineChart)findViewById(R.id.control_lcNoise);//噪声

        Drawable drawable = getResources().getDrawable(R.drawable.linechart_color_change);
        //温度
        dcTemperature = new DynamicLineChartManager(lcTemperature, names.get(0), colour.get(0),6,drawable);//控件，数据的标题，曲线颜色，显示多少格
        dcTemperature.setYAxis(50, 0, 10);//垂直方向最大，最小，（分为的多少份）数量



        //湿度
        dcHumidity = new DynamicLineChartManager(lcHumidity, names.get(1), colour.get(1),6,null);
        dcHumidity.setYAxis(100, 0, 10);
        /*dcHumidity.setHightLimitLine(80,"湿度过大",Color.BLUE);
        dcHumidity.setLowLimitLine(20,"过大干燥",Color.BLUE);*/


        //二氧化碳
        dcCarbonDioxide = new DynamicLineChartManager(lcCarbonDioxide, names.get(2), colour.get(2),6,null);
        dcCarbonDioxide.setYAxis(2000, 0, 10);
        //dcCarbonDioxide.setHightLimitLine(700,"二氧化碳浓度过大",Color.RED);

        //光照
        dcNoise = new DynamicLineChartManager(lcNoise, names.get(3), colour.get(3),6,null);
        dcNoise.setYAxis(20000, 0, 10);


    }

    //注册广播接收器
    private void initData(){
        upDateReceiver=new UpDateReceiver(handler);
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.jq.taida3.Service.BackstageSockectServices");
        ControlActivity.this.registerReceiver(upDateReceiver,filter);
    }

    /**
     * 获取Services的对象
     */
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bss=((BackstageSockectServices.MyServicesBinder)service).getBinder();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private float []dataMax={0,0,0,0};
    private void dataShow(ServerData serverData){


        dcTemperature.addEntry(serverData.getTemperature());//温度数据
        dcHumidity.addEntry(serverData.getHumidity());//湿度数
        dcCarbonDioxide.addEntry(serverData.getCarbon_dioxide());//二氧化碳数据
        dcNoise.addEntry(serverData.getIllumination());//噪声数据


        //温度判断
        if(dataMax[0]<serverData.getTemperature()||(Math.abs(dataMax[0]-serverData.getTemperature()))>10){
            int tMax=25,tMin=15;

            dataMax[0]=serverData.getTemperature();
            if(dataMax[0]<tMin){
                dcTemperature.setLowLimitLine(10,"温度过低",Color.BLUE);
                dcTemperature.setHightLimitLine(30,"高温过高",Color.TRANSPARENT);
                dcTemperature.setYAxis(tMin+15, 0, 6);//垂直方向最大，最小，（分为的多少份）数量

            }else if(dataMax[0]>tMax){
                dcTemperature.setLowLimitLine(10,"温度过低",Color.TRANSPARENT);
                dcTemperature.setHightLimitLine(30,"高温过高",Color.RED);
                dcTemperature.setYAxis(tMax+35, 0, 10);//垂直方向最大，最小，（分为的多少份）数量
            }else {
                dcTemperature.setLowLimitLine(10,"温度过低",Color.BLUE);
                dcTemperature.setHightLimitLine(30,"高温过高",Color.RED);
                dcTemperature.setYAxis(tMax+15, 0, 8);//垂直方向最大，最小，（分为的多少份）数量
            }

        }

        if(dataMax[1]<serverData.getHumidity()||(Math.abs(dataMax[1]-serverData.getHumidity()))>30){
            dataMax[1]=serverData.getHumidity();
            if(dataMax[1]<40){
                dcHumidity.setYAxis(40, 0, 4);
                dcHumidity.setHightLimitLine(80,"湿度过大",Color.TRANSPARENT);
                dcHumidity.setLowLimitLine(20,"过于干燥",Color.BLUE);
            }else if(dataMax[1]>70){
                dcHumidity.setYAxis(100, 0, 10);
                dcHumidity.setHightLimitLine(80,"湿度过大",Color.BLUE);
                dcHumidity.setLowLimitLine(20,"过于干燥",Color.TRANSPARENT);
            }else{
                dcHumidity.setYAxis(100, 0, 10);
                dcHumidity.setHightLimitLine(80,"湿度过大",Color.RED);
                dcHumidity.setLowLimitLine(20,"过于干燥",Color.BLUE);
            }

        }

        if(dataMax[2]<serverData.getCarbon_dioxide()||(Math.abs(dataMax[2]-serverData.getCarbon_dioxide()))>200){
            dataMax[2]=serverData.getCarbon_dioxide();

            if(dataMax[2]>1000) {
                if(dataMax[2]>1500){
                    dcCarbonDioxide.setHightLimitLine(1300, "空气浑浊", Color.RED);
                    dcCarbonDioxide.setLowLimitLine(700, "正常", Color.TRANSPARENT);
                    dcCarbonDioxide.setYAxis(2000, 0, 10);
                }else{
                    dcCarbonDioxide.setHightLimitLine(1300, "空气浑浊", Color.RED);
                    dcCarbonDioxide.setLowLimitLine(700, "正常", Color.BLUE);
                    dcCarbonDioxide.setYAxis(1600, 0, 10);
                }


            }else{
                dcCarbonDioxide.setHightLimitLine(1300, "空气浑浊", Color.TRANSPARENT);
                dcCarbonDioxide.setLowLimitLine(700, "正常", Color.BLUE);
                dcCarbonDioxide.setYAxis(1100, 0, 10);
            }

        }

        //光照
        if(dataMax[3]<serverData.getIllumination()||(Math.abs(dataMax[3]-serverData.getIllumination()))>300){
            int iMax=10000;//很亮
            int iMin=100;//最小
            dataMax[3]=serverData.getIllumination();

            if(dataMax[3]<1000){

                if(dataMax[3]<300){
                    dcNoise.setYAxis(400, 0, 10);
                    dcNoise.setHightLimitLine(600,"光照正常",Color.TRANSPARENT);
                    dcNoise.setLowLimitLine(iMin,"光线不足",Color.BLUE);
                }else {
                    dcNoise.setYAxis(1100, 0, 10);
                    dcNoise.setHightLimitLine(700,"光照较亮",Color.BLUE);
                    dcNoise.setLowLimitLine(iMin+300,"光照充足",Color.BLUE);
                }

            } else if(dataMax[3]>15000){
                dcNoise.setHightLimitLine(8000,"光线刺眼",Color.RED);
                dcNoise.setYAxis(30000, 0, 10);
                dcNoise.setLowLimitLine(40,"光线不足",Color.TRANSPARENT);

            }else{
                if(dataMax[3]>10000){
                    dcNoise.setYAxis(15000, 0, 10);
                    dcNoise.setHightLimitLine(8000,"光线刺眼",Color.RED);
                    dcNoise.setLowLimitLine(40,"光线不足",Color.TRANSPARENT);
                }else if(dataMax[3]<3000){
                    dcNoise.setYAxis(3500, 0, 10);
                    dcNoise.setHightLimitLine(2500,"光照明亮",Color.RED);
                    dcNoise.setLowLimitLine(40,"光线不足",Color.TRANSPARENT);
                }else{
                    dcNoise.setYAxis(11000, 0, 10);
                    dcNoise.setHightLimitLine(8000,"光线刺眼",Color.RED);
                    dcNoise.setLowLimitLine(40,"光线不足",Color.TRANSPARENT);
                }
            }
        }

    }


    //handler_0 的事件处理，0 广播传过来的数据
    private void handler_0(Message msg){
        String data=""+(String) msg.obj;
        Log.i("登录界面的Handler消息监听",data);
        serverData=JsonDataAnalysis.getServerData((String)msg.obj);//json数据解析


        /*dcTemperature.addEntry(serverData.getTemperature());//温度数据
        dcHumidity.addEntry(serverData.getHumidity());//湿度数
        dcCarbonDioxide.addEntry(serverData.getCarbon_dioxide());//二氧化碳数据
        dcNoise.addEntry(serverData.getIllumination());//噪声数据*/
        dataShow(serverData);


        fanText=serverData.getFan();
        conFan.setText("风扇转速档次："+fanText);
        if (oldFan!=Integer.valueOf(fanText)){
            switch (Integer.valueOf(fanText)){
                case 0:
                    ivAnimationFan.stop();
                    break;
                case 1:
                    ivAnimationFan.stop();
                    ivFan.setBackgroundResource(R.drawable.animation_fan_1);
                    ivAnimationFan=(AnimationDrawable)ivFan.getBackground();
                    ivAnimationFan.start();
                    break;
                case 2:
                    ivAnimationFan.stop();
                    ivFan.setBackgroundResource(R.drawable.animation_fan_2);
                    ivAnimationFan=(AnimationDrawable)ivFan.getBackground();
                    ivAnimationFan.start();
                    break;
                case 3:
                    ivAnimationFan.stop();
                    ivFan.setBackgroundResource(R.drawable.animation_fan_3);
                    ivAnimationFan=(AnimationDrawable)ivFan.getBackground();
                    ivAnimationFan.start();
                    break;
            }
        }

        oldFan=Integer.valueOf(fanText);//保存老的数据


        int newSmoke=serverData.getSmoke();//新的烟雾信息
        //判断新的和上一个消息是否一致
        if(oldSmoke!=newSmoke){
            try{ mvAnimationSmoke.stop();}catch (Exception e){}
            if(newSmoke==1){
                mvSmoke.setBackgroundResource(R.drawable.abnormal_animation_01);
                mvSmoke.setContentText("发现烟雾！");
                mvAnimationSmoke=(AnimationDrawable) mvSmoke.getBackground();
                mvAnimationSmoke.start();
            }else{
                mvSmoke.setBackgroundResource(R.drawable.normal_animation_01);
                mvSmoke.setContentText("无烟雾异常");
                mvAnimationSmoke=(AnimationDrawable) mvSmoke.getBackground();
                mvAnimationSmoke.start();
            }
        }

        int newFlame=serverData.getFlame();

        if (oldFlame!=newFlame){
            try{ mvAnimationflame.stop();}catch (Exception e){}
            if(newFlame==1){
                mvFlame.setBackgroundResource(R.drawable.abnormal_animation_01);
                mvFlame.setContentText("发现火源！");
                mvAnimationflame=(AnimationDrawable) mvFlame.getBackground();
                mvAnimationflame.start();
            }else{
                mvFlame.setBackgroundResource(R.drawable.normal_animation_01);
                mvFlame.setContentText("无明火！");
                mvAnimationflame=(AnimationDrawable) mvFlame.getBackground();
                mvAnimationflame.start();
            }
        }

        if(serverData.getThreshold()==1){
            Toast.makeText(this,"阈值数据发生更新！",Toast.LENGTH_SHORT).show();
            //获取阈值
            SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
            DataAnalysis analysis=new DataAnalysis(handler);
            analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),sp.getString(OverallSituation.USER_NAME,""));
        }

        if(serverData.getFace()==1){
            Toast.makeText(this,"人脸数据发生更新！",Toast.LENGTH_SHORT).show();
            startService(new Intent(ControlActivity.this,FaceDownloadService.class));
        }

        if(serverData.getIntelligence()==1){
            SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
            DataAnalysis analysis=new DataAnalysis(handler);
            analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),sp.getString(OverallSituation.USER_NAME,""));
            Toast.makeText(this,"智能控制数据发生更新！",Toast.LENGTH_SHORT).show();
        }



/*        //智能灯监控
        if(serverData.getLamp()==1&&!cbSecurity.isChecked()){
            cbSecurity.setChecked(true);//保安处灯
        }else if(serverData.getLamp()==0&&cbSecurity.isChecked()){
                cbSecurity.setChecked(false);
        }

        if(serverData.getAllLamp()==1&&!cbWorkshop.isChecked()){
            cbWorkshop.setChecked(true);//保安处灯
        }else if(serverData.getAllLamp()==0&&cbWorkshop.isChecked()){
            cbWorkshop.setChecked(false);
        }*/

        //保存新的
        oldSmoke=serverData.getSmoke();
        oldFlame=serverData.getFlame();

    }

/*    @Override
    public void onCheckedChanged(CompoundButton c, boolean isChecked) {
        JsonDataModify modify=new JsonDataModify();
        JsonNotice notice=new JsonNotice();
        if(serverData!=null){
            notice.setLamp(serverData.getLamp());
            notice.setAllLamp(serverData.getAllLamp());
        }

        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        DataModify dataModify=new DataModify(handler);
        SetUpData setUpData=new SetUpData();

        switch (c.getId()){
            case R.id.control_cbLamp:
                Toast.makeText(this,"智能灯控！", Toast.LENGTH_SHORT).show();
                if (isChecked){//发信息给服务器关闭智能控制
                    if(serverData!=null){
                        notice.setIntelligence(1);
                        setUpData.setIntelligence(1);
                        dataModify.setSetUpData(sp.getString(OverallSituation.USER_IP,""),setUpData);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                    }
                    cbWorkshop.setEnabled(false);
                }else{//打开
                    if(serverData!=null){
                        notice.setIntelligence(1);
                        setUpData.setIntelligence(0);
                        dataModify.setSetUpData(sp.getString(OverallSituation.USER_IP,""),setUpData);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                        cbWorkshop.setEnabled(true);
                        cbWorkshop.setChecked(true);
                    }
                }
                break;
            case R.id.control_cbSecurity:
                if(isChecked){
                    if(serverData!=null){
                        notice.setLamp(1);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                     }
                }else{
                    if(serverData!=null){
                        notice.setLamp(0);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                    }
                }

                Toast.makeText(this,"保安灯", Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_cbWorkshop:
                if(isChecked){
                    if(serverData!=null){
                        notice.setAllLamp(serverData.getAllLamp());
                        notice.setIntelligence(serverData.getIntelligence());
                        notice.setAllLamp(1);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                    }

                }else{
                    if(serverData!=null){
                        notice.setAllLamp(serverData.getAllLamp());
                        notice.setIntelligence(serverData.getIntelligence());
                        notice.setAllLamp(0);
                        bss.socketOutput(modify.jsonNoticeToString(notice),(byte) 0x01);
                    }
                }
                Toast.makeText(this,"车间灯", Toast.LENGTH_SHORT).show();
                break;
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.control_btnHistory://历史
                startActivity(new Intent(ControlActivity.this,HistoryActivity.class));
                Toast.makeText(ControlActivity.this,"历史数据",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_btnThreshold:
                ThresholdDialog thresholdDialog=new ThresholdDialog(ControlActivity.this,bss);
                thresholdDialog.showDialog();
                Toast.makeText(ControlActivity.this,"阈值控制",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_btnMonitor:
                startActivity(new Intent(ControlActivity.this,MonitorActivity.class));
                Toast.makeText(ControlActivity.this,"车间监控",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_btnFaceAttendance:
                //faceattendance();
                myda(Utilities.getTime());

                break;
            case R.id.control_btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                Toast.makeText(ControlActivity.this,"人脸注册",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_btnEmployeeInformation:
                //Toast.makeText(ControlActivity.this,"员工信息",Toast.LENGTH_SHORT).show();
                identityActivityShow();
                break;
        }
        drawerShow();
    }

    private void myda(String date){
        final Map<String,Object> map= SignIn.getSignIn(Utilities.getTimeNumbers());//获取信息
        final String reason=(String)map.get("reason");
        Boolean normal=(Boolean) map.get("normal");
        if(false){

        }else{
            tipsDialog=new TipsDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tips_diglog_determine:
                            //Toast.makeText(ControlActivity.this,"完成上传",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ControlActivity.this, RecognizeActivity.class));
                            Toast.makeText(ControlActivity.this,"人脸考勤",Toast.LENGTH_SHORT).show();
                            tipsDialog.dismiss();
                            break;
                        case R.id.tips_diglog_cancel:
                            tipsDialog.dismiss();
                            break;
                    }
                }
            });

            tipsDialog.show();
            tipsDialog.setTitle("确定考勤！");
            tipsDialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);//透明
            tipsDialog.setMessag(reason);
        }
    }


    //考勤登记
    private void faceattendance(){
        /**
         * /允许打卡时间
         * 上午 上班：7：00 （70000） ~  8：00 （80000） 下班 ：    12：00（120000）~ 12:45（124500）
         * 下午 上班：12：45（124500） ~ 13：30（133000）  下班：  17：30（173000） ~ 18：15（181500）
         * 晚上 上班：18：15（181500） ~ 19：00（190000）  下班 ： 21：00（210000） ~ 21：30（213000）
         */

        final SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        final DataModify dataModify=new DataModify(handler);

        Map<String,Object> map=SignIn.getSignIn(Utilities.getTimeNumbers(),oldAtt);//获取信息
        final Attendance newAtt=(Attendance) map.get("attendance");
        Boolean normal=(Boolean) map.get("normal");
        String reason=(String)map.get("reason");

        if(normal){
            dataModify.setAttendance(sp.getString(OverallSituation.USER_IP,""),userName,Utilities.getDate(),newAtt);//写入考勤记录
            Toast.makeText(this,"考勤成功！",Toast.LENGTH_SHORT).show();
        }else{
            tipsDialog=new TipsDialog(this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.tips_diglog_determine:
                            //Toast.makeText(ControlActivity.this,"完成上传",Toast.LENGTH_SHORT).show();
                            dataModify.setAttendance(sp.getString(OverallSituation.USER_IP,""),userName,Utilities.getDate(),newAtt);
                            break;
                        /*case R.id.tips_diglog_cancel:
                            //Toast.makeText(ControlActivity.this,"取消",Toast.LENGTH_SHORT).show();
                            break;*/
                    }
                }
            });
            tipsDialog.show();
            tipsDialog.setTitle("确定考勤！");
            tipsDialog.setMessag(reason);
        }
    }


    //（员工信息）判断身份，然后跳转
    private void identityActivityShow(){
        if(identity.equals(LoginActivity.IDENTITY_ADMIN)){
            Intent intent=new Intent(ControlActivity.this,AdministratorsActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("identity",LoginActivity.IDENTITY_ADMIN);
            bundle.putString("user",userName);
            bundle.putString("psw",passWord);
            intent.putExtra("userData",bundle);
            startActivity(intent);
        }else{
            Intent intent=new Intent(ControlActivity.this,StaffActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("identity",LoginActivity.IDENTITY_STAFF);
            bundle.putString("user",userName);
            bundle.putString("psw",passWord);
            intent.putExtra("userData",bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeWhole();
    }

    //隐藏和显示侧划菜单
    private void drawerShow(){
        if(drawerLayout.isDrawerOpen(Gravity.START)){
            drawerLayout.closeDrawers();
        }else{
            drawerLayout.openDrawer(Gravity.START);
            SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
            DataAnalysis analysis=new DataAnalysis(handler);
            analysis.getAttendance(sp.getString(OverallSituation.USER_IP,""),userName,Utilities.getDate());//获取单个员工某天的考勤记录，19
        }
    }


    /**
     * 菜单栏绑定
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.control_toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * menu的点击监听
     */
    private Toolbar.OnMenuItemClickListener onMenuItemClick=new Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.control_ment_SignOut:
                    startActivity(new Intent(ControlActivity.this,LoginActivity.class));//跳回到登录界面
                    SharedPreferences.Editor editor=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE).edit();
                    editor.putString(OverallSituation.USER_PASSWORD,"");//删除密码
                    editor.commit();//提交
                    closeWhole();
                    finish();
                    break;
                case R.id.control_ment_Close:
                    closeWhole();
                    finish();
                    break;
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        //删除super就可以实现不跳转（前提 前面的activity要摧毁掉finish()）
        if(!mBackKeyPressed){
            Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
            mBackKeyPressed=true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }//延时两秒，如果超出则擦错第一次按键记录

            },2000);
        }else{
            moveTaskToBack(true);

        }
    }

    //点击返回
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                drawerShow();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 激活引擎
     * @param view
     */
    public void activeEngine(final View view) {
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        if(sp.getBoolean(OverallSituation.USER_ISENFINE,false)){
            return;
        }
        if (!checkPermissions(NEEDED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
            return;
        }

        if (view != null) {
            view.setClickable(false);
        }
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                FaceEngine faceEngine = new FaceEngine();
                int activeCode = faceEngine.active(ControlActivity.this, Constants.APP_ID, Constants.SDK_KEY);
                emitter.onNext(activeCode);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }
                    @Override
                    public void onNext(Integer activeCode) {
                        if (activeCode == ErrorInfo.MOK) {
                            showToast(getString(R.string.active_success));
                            SharedPreferences.Editor editor=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE).edit();
                            editor.putBoolean("isEngine",true);
                            editor.commit();
                        } else if (activeCode == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED) {
                            showToast(getString(R.string.already_activated));
                        } else {
                            showToast(getString(R.string.active_failed, activeCode));
                        }

                        if (view != null) {
                            view.setClickable(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this, neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                activeEngine(null);
            } else {
                showToast(getString(R.string.permission_denied));
            }
        }
    }

    private void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast.setText(s);
            toast.show();
        }
    }

    //关闭socket 和广播
    private Boolean closeWhole(){
        try{
            bss.setThreadStart(false);
            unbindService(connection);
            unregisterReceiver(upDateReceiver);
            unbindService(connection);
        }catch (Exception e){}

        return false;
    }

}
