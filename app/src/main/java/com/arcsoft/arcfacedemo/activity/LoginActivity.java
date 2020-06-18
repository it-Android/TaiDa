package com.arcsoft.arcfacedemo.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.BroadcastReceiver.UpDateReceiver;
import com.arcsoft.arcfacedemo.Dialog.ChangIpDialog;
import com.arcsoft.arcfacedemo.Dialog.RefreshDialog;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Service.BackstageSockectServices;
import com.arcsoft.arcfacedemo.Service.FaceDownloadService;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
import com.arcsoft.arcfacedemo.Tool.System.DetectionVerification;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;


import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    public final static String IDENTITY_STAFF="staff";
    public final static String IDENTITY_ADMIN="admin";
    private Handler handler;
    private RefreshDialog refreshDialog;//等待加载的提示框
    private ChangIpDialog changIpDialog;

    private Button btnDetermine;//登录按钮
    private EditText ediUser,ediPassWord;//账号密码
    private CheckBox checkBox;//复选按钮
    private RadioGroup radioGroup;
    private RadioButton rbtnStaff,rbtnAdministrators;//员工,管理员
    private Toolbar toolbar;//标题栏
    private UpDateReceiver upDateReceiver;
    private String identity="staff";
    private BackstageSockectServices bss;//services对象
    private Boolean socketClose=false,isOne=true;

    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();//初始化
        automaticLanding();//初始化基本信息
    }

    //初始化
    private void init(){
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        toolbar.setOnMenuItemClickListener(onMenuItemClick);//菜单点击监听
        btnDetermine=(Button)findViewById(R.id.login_btnDetermine);
        btnDetermine.setOnClickListener(this);
        ediUser=(EditText)findViewById(R.id.login_ediUser);
        ediPassWord=(EditText)findViewById(R.id.login_ediPassWord);

        checkBox=(CheckBox)findViewById(R.id.login_cheFace);

        radioGroup=(RadioGroup)findViewById(R.id.login_rgroup);
        radioGroup.setOnCheckedChangeListener(this);
        rbtnStaff=(RadioButton)findViewById(R.id.login_rbtnStaff); //员工
        rbtnAdministrators=(RadioButton)findViewById(R.id.login_rbtnAdministrators);//管理员

        handler= new Handler(){
            public void handleMessage(android.os.Message msg) {
                switch (msg.what)
                {
                    case HandlerNumberRecord.SERVERDATA_0://服务器数据
                        handle_0(msg);//条件0
                        break;
                    case 1111:
                        handle_1111(msg);//条件10
                        break;
                    case 999://接收IP输入提示框的数据
                        Map<String,String> map=(Map)msg.obj;
                        setConnect(map);// 连接并开启新的服务
                        break;
                    case HandlerNumberRecord.DISCONNECT_10000://连接断开
                        handle_10000(msg);
                        break;
                    default:
                        break;
                }
            };
        };

        refreshDialog=new RefreshDialog(this,null);
    }


    //条件——0——的数据处理
    private void handle_0(Message msg){
        String data=""+(String) msg.obj;
        Log.i("登录界面的Handler消息监听",data);
        //检测是否连接成功
        if(data.equals(BackstageSockectServices.CONNECTION_FAILED)){                    //保存是否连接成功，失败返回 "连接失败！";
            changIpDialog = new ChangIpDialog(LoginActivity.this, handler);//显示IP输入提示框
            changIpDialog.showDialog();
            Toast.makeText(LoginActivity.this,"连接失败！",Toast.LENGTH_SHORT).show();
            socketClose=false;                                                           //保存连接失败关闭sockect
            closeWhole();//关闭
        }else{
            socketClose=true;
            startService(new Intent(LoginActivity.this,FaceDownloadService.class));
            if(isOne&&true){//是否登陆成功
                isOne=false;
                login(true);
            }
        }
        //Toast.makeText(LoginActivity.this,data,Toast.LENGTH_SHORT).show();
    }

    //条件——10——的数据处理
    private void handle_1111(Message msg){
        Boolean ok=(Boolean)msg.obj;
        if(ok){
            try{
                SharedPreferences.Editor editor = getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE).edit();
                editor.putString(OverallSituation.USER_LAST_LOGIN_TIME,Utilities.getTimeNumber());//保存本次登录时间登录时间
                if(checkBox.isChecked()){
                    editor.putBoolean(OverallSituation.USER_CHECKREMEMBER,true);
                    editor.putString(OverallSituation.USER_PASSWORD,ediPassWord.getText().toString().trim());
                }else{
                    editor.putString(OverallSituation.USER_PASSWORD,"");
                    editor.putBoolean(OverallSituation.USER_CHECKREMEMBER,false);
                }
                editor.commit();
            }catch (Exception e){}

            Intent intent=new Intent(LoginActivity.this,ControlActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("userName",ediUser.getText().toString().trim());
            bundle.putString("passWord",ediPassWord.getText().toString().trim());
            bundle.putString("identity",identity);
            intent.putExtra("userData",bundle);

            startActivity(intent);//界面跳转
            closeWhole();
            finish();
            Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this,"登录失败！",Toast.LENGTH_SHORT).show();
        }
        refreshDialog.setDialogHide(false);
    }

    //条件——10000——的数据处理（连接断开）
    private void handle_10000(Message msg){
        //检测是否连接成功
        if((Boolean) msg.obj){                    //保存是否连接成功，失败返回 "连接失败！";
            startActivity(new Intent(this,LoginActivity.class));
            changIpDialog = new ChangIpDialog(LoginActivity.this, handler);//显示IP输入提示框
            changIpDialog.showDialog();
            socketClose=false;                                                           //保存连接失败关闭sockect
            Toast.makeText(LoginActivity.this,"与服务器断开连接！",Toast.LENGTH_SHORT).show();
            closeWhole();//关闭
        }else{
            socketClose=true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_btnDetermine:
                login(false);//不需要检测上传登录时间
                break;
        }
    }

    private void login(Boolean isNow){
        if(socketClose){
            if(ediUser.getText().toString().trim().equals("")){
                Toast.makeText(LoginActivity.this,"账号不能为空！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(ediPassWord.getText().toString().trim().equals("")){
                Toast.makeText(LoginActivity.this,"密码不能为空！",Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE);

            //超过一天没登陆就要重新输入密码
            int nowTime=Integer.valueOf(Utilities.getTimeNumber());//将现在的时间转换为int类型
            int oldTime=Integer.valueOf(sp.getString(OverallSituation.USER_LAST_LOGIN_TIME,"0"));
            if(nowTime-oldTime>1&&isNow){
                ediPassWord.setText("");
                Toast.makeText(LoginActivity.this,"过长时间没登陆！",Toast.LENGTH_SHORT).show();
                return;
            }

            DataAnalysis dataAnalysis=new DataAnalysis(handler);
            String Ip=sp.getString(OverallSituation.USER_IP,"");
            if(!Ip.equals("")){
                //登录验证
                dataAnalysis.validateLogon1(Ip,//输入IP
                        ediUser.getText().toString().trim(),//用户账号
                        ediPassWord.getText().toString().trim(),//用户密码
                        identity);//用户权限
                preservationUserData();//保存数据到本地
                refreshDialog.showDialog();
            }else{
                changIpDialog = new ChangIpDialog(LoginActivity.this, handler);//显示IP输入提示框
                changIpDialog.showDialog();
                Toast.makeText(LoginActivity.this,"请先连接服务器！",Toast.LENGTH_SHORT).show();
            }
        }else{
            changIpDialog = new ChangIpDialog(LoginActivity.this, handler);//显示IP输入提示框
            changIpDialog.showDialog();
            Toast.makeText(LoginActivity.this,"请先连接服务器！",Toast.LENGTH_SHORT).show();
        }
    }

    //RadioGroup
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.login_rbtnAdministrators:
                identity=IDENTITY_ADMIN;
                Toast.makeText(LoginActivity.this,"管理员",Toast.LENGTH_SHORT).show();
                break;
            case R.id.login_rbtnStaff:
                identity=IDENTITY_STAFF;
                Toast.makeText(LoginActivity.this,"员工",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 菜单栏
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * menu的点击监听
     */
    private Toolbar.OnMenuItemClickListener onMenuItemClick=new Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.change_network:
                    changIpDialog=new ChangIpDialog(LoginActivity.this,handler);//显示IP输入提示框
                    changIpDialog.showDialog();
                    break;
                case R.id.change_SignOut:
                    closeWhole();//断开连接
                    ediPassWord.setText("");
                    checkBox.setChecked(false);
                    preservationUserData();
                    Toast.makeText(LoginActivity.this,"退出成功!",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
            return true;
        }
    };

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

    /**保存一些基本信息在本地*/
    private void automaticLanding() {
        SharedPreferences sp = getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE);//设置名称UserData里面的名称
        String firstlogin = sp.getString(OverallSituation.USER_FIRSTLOGIN, "");//保存是否为第一次登陆

        if (null != firstlogin && firstlogin.equals("true")) {
            setConnect(sp.getString(OverallSituation.USER_IP, ""), sp.getString(OverallSituation.USER_PORT, ""));
            ediPassWord.setText(sp.getString(OverallSituation.USER_PASSWORD, ""));
            ediUser.setText(sp.getString(OverallSituation.USER_NAME, ""));
            checkBox.setChecked(sp.getBoolean(OverallSituation.USER_CHECKREMEMBER, false));
            try{
                Boolean is=sp.getBoolean(OverallSituation.USER_ISADMIN,false);
                if(is){
                    rbtnAdministrators.setChecked(true);
                    rbtnStaff.setChecked(false);
                    identity=LoginActivity.IDENTITY_ADMIN;
                }else{
                    rbtnAdministrators.setChecked(false);
                    rbtnStaff.setChecked(true);
                    identity=LoginActivity.IDENTITY_STAFF;
                }
            }catch (Exception e){
                rbtnAdministrators.setChecked(false);
                rbtnStaff.setChecked(true);
                identity=LoginActivity.IDENTITY_STAFF;
            }

        } else {
            //初始化
            SharedPreferences.Editor editor = getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE).edit();
            editor.putString(OverallSituation.USER_FIRSTLOGIN, "true");//保存是否为第一次登陆
            editor.putBoolean(OverallSituation.USER_ISADMIN,true);//保存上次登录人的身份管理员
            editor.putString(OverallSituation.USER_LAST_LOGIN_TIME,Utilities.getTimeNumber());//初始化登录时间
            editor.putString(OverallSituation.USER_ISADMIN,"true");//初始化登录时间
            editor.putBoolean(OverallSituation.USER_CHECKREMEMBER, false);//保存是否记住密码
            editor.putString(OverallSituation.USER_IP, "192.168.1.6");//初始IP地址
            editor.putString(OverallSituation.USER_PORT, "8008");//初始化端口
            editor.putBoolean("isEngine",false);
            editor.putString(OverallSituation.USER_NAME, ediUser.getText().toString().trim());//初始化账号
            editor.putString(OverallSituation.USER_PASSWORD, ediPassWord.getText().toString().trim());//初始化密码
            Toast.makeText(LoginActivity.this, "初始化成功！", Toast.LENGTH_SHORT).show();
            editor.commit();
        }
    }

    //保存数据
    private void preservationUserData(){
        SharedPreferences.Editor editor = getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(OverallSituation.USER_CHECKREMEMBER,checkBox.isChecked());//保存是否记住密码
        editor.putString(OverallSituation.USER_NAME,ediUser.getText().toString().trim());//初始化账号
        editor.putString(OverallSituation.USER_PASSWORD,ediPassWord.getText().toString().trim());//初始化密码
        editor.putBoolean(OverallSituation.USER_ISADMIN,rbtnAdministrators.isChecked());//保存化登录人身份
        editor.commit();
    }



    /**
     * 连接并开启新的服务
     * @param mapConnect
     */
    private void setConnect(Map<String,String> mapConnect){

        if(closeWhole()){
            Toast.makeText(LoginActivity.this,"已断开，正在重新连接!",Toast.LENGTH_SHORT).show();
        }
        //开启socket
        Intent intent=new Intent(LoginActivity.this,BackstageSockectServices.class);
        intent.putExtra(OverallSituation.USER_IP,mapConnect.get(OverallSituation.USER_IP));
        intent.putExtra(OverallSituation.USER_PORT,mapConnect.get(OverallSituation.USER_PORT));
        bindService(intent,connection,BIND_AUTO_CREATE);
        DetectionVerification b=new DetectionVerification();

        //注册广播接收器
        upDateReceiver=new UpDateReceiver(handler);
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.jq.taida3.Service.BackstageSockectServices");
        LoginActivity.this.registerReceiver(upDateReceiver,filter);
    }


    /**
     * 连接并开启新的服务
     */
    private void setConnect(String Ip,String Port){

        if(closeWhole()){
            Toast.makeText(LoginActivity.this,"已断开，正在重新连接!",Toast.LENGTH_SHORT).show();
        }
        //开启socket
        Intent intent=new Intent(LoginActivity.this,BackstageSockectServices.class);
        intent.putExtra(OverallSituation.USER_IP,Ip);
        intent.putExtra(OverallSituation.USER_PORT,Port);
        bindService(intent,connection,BIND_AUTO_CREATE);

        //注册广播接收器
        upDateReceiver=new UpDateReceiver(handler);
        IntentFilter filter=new IntentFilter();
        filter.addAction("com.example.jq.taida3.Service.BackstageSockectServices");
        LoginActivity.this.registerReceiver(upDateReceiver,filter);
    }

    //关闭socket 和广播
    private Boolean closeWhole(){
        try {
            bss.setThreadStart(false);
            unbindService(connection);
            unregisterReceiver(upDateReceiver);
            socketClose=false;//保存socket为关闭状态
            return true;
        }catch (Exception e){

        }
        return false;
    }
}
