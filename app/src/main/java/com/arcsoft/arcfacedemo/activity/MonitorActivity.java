package com.arcsoft.arcfacedemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.Dialog.ChangIpDialog;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.SetUpData;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;
import com.arcsoft.arcfacedemo.Tool.System.Utilities;

import java.io.File;

import nledu.com.ipcamera.CameraManager;
import nledu.com.ipcamera.PTZ;

public class MonitorActivity extends AppCompatActivity implements View.OnTouchListener,CheckBox.OnCheckedChangeListener,View.OnClickListener{

    private TextureView textureView;
    private CameraManager cameraManager;
    private CheckBox cbOpenOrClose;

    private Handler handler;
    private Toolbar toolbar;

    String userName = "admin";//账号
    String pwd = "admin";//密码
    String ip = "192.168.1.4";//IP
    String channel = "1";//端口

    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);//菜单点击监听
        init();
        initCameraManager();

    }
    //控件初始化
    private void init(){
        textureView = findViewById(R.id.svCamera);
        findViewById(R.id.ibTop).setOnTouchListener(this);
        findViewById(R.id.ibLeft).setOnTouchListener(this);
        findViewById(R.id.ibRight).setOnTouchListener(this);
        findViewById(R.id.ibBottom).setOnTouchListener(this);
        findViewById(R.id.ibStop).setOnTouchListener(this);
        findViewById(R.id.ivGetImg).setOnClickListener(this);

        tvText=(TextView)findViewById(R.id.tvText);

        tvText.setOnClickListener(this);
        textureView.setOnClickListener(this);
        cbOpenOrClose=(CheckBox)findViewById(R.id.cbOpenOrClose);
        cbOpenOrClose.setOnCheckedChangeListener(this);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 42:
                        SetUpData setUpData=(SetUpData) msg.obj;
                        userName=setUpData.getCamera_admin();
                        pwd=setUpData.getCamera_password();
                        ip=setUpData.getCamera_IP();
                        break;
                }
            }
        };

        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        DataAnalysis analysis=new DataAnalysis(handler);
        analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),"admin");


    }

    private void initCameraManager() {
        cameraManager = CameraManager.getInstance();
        cameraManager.setupInfo(textureView, userName, pwd, ip, channel);
    }



    //截图
    public void getImage() {
        String[] PERMISSIONS = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
        //检测是否有写的权限
        int permission = ContextCompat.checkSelfPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // 没有写的权限，去申请写的权限，会弹出对话框
            ActivityCompat.requestPermissions(this, PERMISSIONS,1);
        }

        String path=Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/智能工厂/";//文件夹路径

        File file=new File(path);//获取路径file对象
        if(!file.exists()){//假如路径不存就创建该路径
            file.mkdirs();//创建（不存在，存在不创建）
        }
        String fileName="智能工厂监控抓拍："+Utilities.getNowDate() +".jpg";//文件名
        cameraManager.capture(path, fileName);//保存图片
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+path + fileName)));// 发送广播，通知刷新图库的显示
        Toast.makeText(this,"截图成功，保存到本机相册！",Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        PTZ ptz = null;
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            ptz = PTZ.Stop;
        } else if (action == MotionEvent.ACTION_DOWN) {
            int viewId = v.getId();
            switch (viewId) {
                case R.id.ibTop:
                    ptz = PTZ.Up;
                    break;
                case R.id.ibBottom:
                    ptz = PTZ.Down;
                    break;
                case R.id.ibStop:
                    ptz=PTZ.Stop;
                    break;
                case R.id.ibLeft:
                    ptz = PTZ.Left;
                    break;
                case R.id.ibRight:
                    ptz = PTZ.Right;
                    break;
            }
        }
        cameraManager.controlDir(ptz);
        return false;
    }

    /**
     * 菜单栏
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.monity_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * menu的点击监听
     */

    private Toolbar.OnMenuItemClickListener onMenuItemClick=new Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.monity_menu_refresh:
                    cbOpenOrClose.setChecked(false);
                    SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
                    DataAnalysis analysis=new DataAnalysis(handler);
                    analysis.getSetUpData(sp.getString(OverallSituation.USER_IP,""),"admin");
                    Toast.makeText(MonitorActivity.this,"重新连接成功！",Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.cbOpenOrClose:
                if(isChecked){
                    textureView.setVisibility(View.VISIBLE);
                    initCameraManager();
                    cameraManager.openCamera();
                }else{
                    cameraManager.releaseCamera();
                    textureView.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivGetImg:
                getImage();
                break;
            case R.id.svCamera:
                if(!cbOpenOrClose.isChecked()){
                    cbOpenOrClose.setChecked(true);
                }
                break;
            case R.id.tvText:
                if(!cbOpenOrClose.isChecked()){
                    cbOpenOrClose.setChecked(true);
                }
                break;
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
