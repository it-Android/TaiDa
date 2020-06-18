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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.Dialog.ModifyDialog;
import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.Attendance;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.Analysis.DataAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffActivity extends AppCompatActivity implements View.OnClickListener {

    private Handler handler;
    private Staff staff;
    private List<Attendance> listAtt;

    private TextView tv_post,tv_number,tv_name,tv_age,tv_sex;//职位，工号，姓名，年龄，性别
    private TextView tv_eMail,tv_phone;//邮箱，电话
    private ImageView iv_Image;//

    private Button btnModify;
    private Toolbar toolbar;


    private String identity;//身份
    private String userName,passWord;//员工账号、密码
    private String adminUserName,adminPassWord;//管理员账号、密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        //标题导航栏
        toolbar=(Toolbar)findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);//ActionBar更换为toobar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent=getIntent();//获取传来的数据
        Bundle bundle=intent.getBundleExtra("userData");
        if(bundle.getString("identity").equals(LoginActivity.IDENTITY_ADMIN)){
            adminUserName=bundle.getString("adminUserName");
            adminPassWord=bundle.getString("adminPassWord");
        }

        init();//初始化
        userInit(bundle.getString("user"));
    }

    //初始化
    private void init(){
        tv_age=(TextView)findViewById(R.id.staff_tv_age);
        tv_sex=(TextView)findViewById(R.id.staff_tv_sex);
        tv_name=(TextView)findViewById(R.id.staff_tv_name);
        tv_number=(TextView)findViewById(R.id.staff_tv_number);
        tv_post=(TextView)findViewById(R.id.staff_tv_post);

        tv_eMail=(TextView)findViewById(R.id.staff_tv_eMail);
        tv_phone=(TextView)findViewById(R.id.staff_tv_phone);

        btnModify=(Button)findViewById(R.id.staff_btnModify);
        btnModify.setOnClickListener(this);

        iv_Image=(ImageView)findViewById(R.id.staff_iv_image);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 20:
                        Map<String,Object> map=(Map<String,Object>)msg.obj;
                        staff=(Staff) map.get("staff");
                        listAtt=(List<Attendance>) map.get("listAtt");
                        dataShow();
                        break;
                    case 11://接收到修改界面传来的数据刷新界面
                        try{
                            if((Boolean) msg.obj){
                                userInit(staff.getUserName());
                            }
                        }catch (Exception e){}
                        break;
                }
            }
        };

    }

    //用户基本数据初始化
    private void userInit(String userName){
        DataAnalysis da=new DataAnalysis(handler);//获取数据库操作对象
        SharedPreferences sp=getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,MODE_PRIVATE);//读取本地保存IP
        da.staffData(sp.getString(OverallSituation.USER_IP,""),userName,true);//获取员工的信息
    }


    private void dataShow() {
        //lisview适配
        List<Map<String,String>> list=new ArrayList<Map<String,String>>();
        for(Attendance ad:listAtt){
            Map<String,String> map=new HashMap<>();
            map.put("informationDate",ad.getDate());
            map.put("morningGoToWork",ad.getAmAttendance());
            map.put("morningGoOffWork",ad.getAmNoAttendance());
            map.put("afternoonGoToWork",ad.getPmAttendance());
            map.put("afternoonGoOffWork",ad.getPmNoAttendance());
            map.put("nightGoToWork",ad.getNiAttendance());
            map.put("nightGoOffWork",ad.getNiNoAttendance());
            list.add(map);
        }

        SimpleAdapter myadapter=new SimpleAdapter(getApplicationContext(),list,R.layout.check_work_attendance,
                new String[]{"informationDate","morningGoToWork","morningGoOffWork","afternoonGoToWork","afternoonGoOffWork","nightGoToWork","nightGoOffWork"},
                new int[]{R.id.informationDate,R.id.morningGoToWork,R.id.morningGoOffWork,R.id.afternoonGoToWork,R.id.afternoonGoOffWork,R.id.nightGoToWork,R.id.nightGoOffWork});
        ListView listView=(ListView)findViewById(R.id.list_test);
        listView.setAdapter(myadapter);

        tv_post.setText("身份："+staff.getUserJob());
        tv_number.setText("工号："+staff.getUserName());
        tv_name.setText("姓名："+staff.getName());
        tv_sex.setText("性别："+staff.getUserSex());
        tv_age.setText("年龄："+staff.getAge());

        tv_phone.setText(staff.getPhone());
        tv_eMail.setText(staff.getE_Mail());
        try {
            Bitmap bitmap=BitmapFactory.decodeByteArray(staff.getUserImage(),0,staff.getUserImage().length);
            iv_Image.setImageBitmap(bitmap);
        }catch (Exception e){
            iv_Image.setImageResource(R.mipmap.no_image);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.staff_btnModify:
                ModifyDialog md=new ModifyDialog(StaffActivity.this,handler,staff);
                md.modifyDialogShow();
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
