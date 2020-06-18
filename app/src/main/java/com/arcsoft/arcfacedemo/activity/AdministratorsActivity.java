package com.arcsoft.arcfacedemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.Admin;
import com.arcsoft.arcfacedemo.Tool.Data.HandlerNumberRecord;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdministratorsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private Toolbar toolbar;
    private Handler handler;
    private List<Staff> listArrya;//全部员工数据
    private TextView tv_post,tv_number;//显示，职位与账号
    private ImageView iv_head;//显示头像

    private String userName,passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrators);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//toolrbar左上角按钮可用
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("userData");
        userName=bundle.getString("user");
        passWord=bundle.getString("psw");

//        init();
        //userInit("admin");
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        userInit(userName);//初始化管理员数据
        //Toast.makeText(this,"454644s4fsdfsd",Toast.LENGTH_SHORT).show();
    }

    //初始化
    private void init(){
        iv_head=(ImageView)findViewById(R.id.admin_iv_head);
        tv_post=(TextView)findViewById(R.id.admin_tv_post);
        tv_number=(TextView)findViewById(R.id.admin_tv_number);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case HandlerNumberRecord.DATA_ANALYSIS_21://获取全部员工的基本信息
                        listArrya=(List<Staff>)msg.obj;
                        showData();//数据显示(获取全部员工的基本信息)
                        break;
                    case HandlerNumberRecord.DATA_ANALYSIS_22://获取全部管理员的基本信息
                        Admin admin=(Admin) msg.obj;//管理员信息
                        tv_post.setText("身份：管理员");
                        tv_number.setText("账号："+admin.getUserName());
                        try{
                            Bitmap bitmap=BitmapFactory.decodeByteArray(admin.getImage(),0,admin.getImage().length);//图片（管理员头像）
                            iv_head.setImageBitmap(bitmap);
                        }catch (Exception e){}

                        break;
                }
            }
        };

        //获取 全部 员工的基本信息
        DataAnalysis da=new DataAnalysis(handler);//数据库数据解析类对象
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);
        da.getStaffArryaData(sp.getString(OverallSituation.USER_IP,""));//返回  21
    }

    //账号初始化（获取管理员信息）
    private void userInit(String userName){
        DataAnalysis da=new DataAnalysis(handler);//获取数据库操作对象
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);//读取本地保存IP
        da.getAdminData(sp.getString(OverallSituation.USER_IP,""),userName);//获取员工的信息 返回 22
    }


    //数据显示(显示全部员工的基本信息)
    private void showData(){
        List<Map<String,String>> list=new ArrayList<>();
        for(Staff staff:listArrya){
            Map<String,String> map=new HashMap<>();
            map.put("name","姓名："+staff.getName());
            map.put("number","工号："+staff.getUserName());
            map.put("sex",staff.getUserSex());
            list.add(map);
        }
        SimpleAdapter myadapter=new SimpleAdapter(getApplicationContext(),//listView适配器
                list,R.layout.admin_listview_layout,//布局
                new String[]{"name","number","sex"},//数据
                new int[]{R.id.admin_listView_name,R.id.admin_listView_number,R.id.admin_listView_sex});//数据传递到的控件
        ListView listView=(ListView)findViewById(R.id.admin_listView);//创建listview对象
        listView.setAdapter(myadapter);//添加适配器
        listView.setOnItemClickListener(this);//点击监听
    }

    //listView点击监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //跳转到用户界面
        Intent intent=new Intent(AdministratorsActivity.this,StaffActivity.class);
        Staff staff=listArrya.get(position);
        Bundle bundle=new Bundle();//数据绑定
        bundle.putString("identity",LoginActivity.IDENTITY_ADMIN);//传递身份
        bundle.putString("user",staff.getUserName());//传递账号
        bundle.putString("psw",staff.getUserPassWord());//传递密码
        bundle.putString("adminUserName",userName);
        bundle.putString("adminPassWord",passWord);

        intent.putExtra("userData",bundle);//发送
        startActivity(intent);//跳转
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
