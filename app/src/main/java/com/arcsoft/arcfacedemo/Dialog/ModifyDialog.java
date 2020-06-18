package com.arcsoft.arcfacedemo.Dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.Data.Staff;
import com.arcsoft.arcfacedemo.Tool.SQL.Modify.DataModify;


public class ModifyDialog {
    private Context context;
    private Handler handler;
    private AlertDialog dialog;
    private RefreshDialog refreshDialog;
    private EditText ed_age,ed_phone,ed_email;
    private RadioButton ra_boy,ra_girl,ra_secrecy;
    private Staff staffData;

    public ModifyDialog(final Context context,Staff staffData) {
        super();
        this.context = context;
        this.staffData=staffData;
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 888:
                        if((Boolean) msg.obj){//保存成功时返回888
                            refreshDialog.setDialogHide(false);//关闭等待提示框
                            dialog.dismiss();//提交
                        }else{
                            Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
                        }
                    case 1010:
                        if((Boolean) msg.obj){//关闭等提示框
                            refreshDialog.setDialogHide(false);
                            dialog.dismiss();
                        }else{//超时
                            Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
                        }
                }
            }
        };
    }


    public ModifyDialog(final Context context, final Handler handlerMain, Staff staffData) {
        this.context = context;
        this.staffData=staffData;
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 888:
                        if((Boolean) msg.obj){//保存成功时返回888
                            refreshDialog.setDialogHide(false);//关闭等待提示框
                            dialog.dismiss();//提交
                        }else{
                            Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
                        }
                    case 1010:
                        if((Boolean) msg.obj){//关闭等提示框
                            refreshDialog.setDialogHide(false);
                            dialog.dismiss();
                            //发消息到主界面提示更新数据
                            Message msgMain=new Message();
                            msgMain.what=11;
                            msgMain.obj=true;
                            handlerMain.handleMessage(msgMain);

                        }else{//超时
                            Toast.makeText(context,"保存失败",Toast.LENGTH_SHORT).show();
                        }
                }
            }
        };
    }


    //员工信息修改框显示
    public void modifyDialogShow(){
        View view=LayoutInflater.from(context).inflate(R.layout.modify_dialog,null,false);//获取布局
        dialog = new AlertDialog.Builder(context).setView(view).setCancelable(false).create();//设置提示框
        ed_age=view.findViewById(R.id.modify_et_age);
        ed_phone=view.findViewById(R.id.modify_et_phone);
        ed_email=view.findViewById(R.id.modify_et_email);
        ra_boy=view.findViewById(R.id.modify_rb_boy);
        ra_girl=view.findViewById(R.id.modify_rb_girl);
        ra_secrecy=view.findViewById(R.id.modify_rb_secrecy);
        ra_secrecy.setEnabled(false);

        //初始化数据
        ed_age.setText(""+staffData.getAge());
        ed_email.setText(staffData.getE_Mail());
        ed_phone.setText(staffData.getPhone());
        switch (staffData.getUserSex()){
            case "男":
                ra_boy.setChecked(true);
                break;
            case "女":
                ra_girl.setChecked(true);
                break;
            case "保密":
                ra_secrecy.setChecked(true);
                break;
        }

        Button btnCancel=view.findViewById(R.id.modify_dl_btnCancel);//点击的取消按钮
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnDetermined=view.findViewById(R.id.modify_dl_btnDetermined);
        btnDetermined.setOnClickListener(new View.OnClickListener() {//提交信息
            @Override
            public void onClick(View v) {
                DataModify dataModify=new DataModify(handler);//sql的数据修改类
                SharedPreferences sp=context.getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,Context.MODE_PRIVATE);//获取保存成的SharedPreferences的数据
                String sex="";//保存性别
                if(ra_boy.isChecked()){ sex="男"; }else if(ra_girl.isChecked()){ sex="女"; }else { sex="保密"; }//获取性别
                Staff staff=new Staff();//创建员工类
                staff.setUserSex(sex);//保存员工性别
                staff.setAge(Integer.valueOf(ed_age.getText().toString()));//保存年龄
                staff.setE_Mail(ed_email.getText().toString().trim());//保存邮箱信息
                staff.setPhone(ed_phone.getText().toString().trim());//保存电话信息
                /**
                 *                sp.getString(OverallSituation.USER_IP,"")获取保存的IP 地址
                 *                staffData.getUserName() 员工的员工好
                 *                staff  员工的数据
                 */
                dataModify.setStaff(sp.getString(OverallSituation.USER_IP,""),staffData.getUserName(),staff);//将保存的员工信息写入数据库
                refreshDialog=new RefreshDialog(context,handler);//显示等待提示框
                refreshDialog.showDialog();
            }
        });

        dialog.show(); //显示
        //控制大小
        //dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context)*4/6),(ScreenUtils.getScreenHeight(context)*4/6));
    }

}
