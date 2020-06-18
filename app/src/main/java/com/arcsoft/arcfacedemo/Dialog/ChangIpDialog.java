package com.arcsoft.arcfacedemo.Dialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.OverallSituation;
import com.arcsoft.arcfacedemo.Tool.System.DetectionVerification;
import com.arcsoft.arcfacedemo.Tool.System.ScreenUtils;

import java.util.HashMap;
import java.util.Map;


/**输入IP和端口号的提示框**/
public class ChangIpDialog {
    private Context context;//
    private Handler handler;//消息提示
    private EditText editText_IP,editText_Port;
    private AnimationDrawable animationDrawable;
    private ImageView imageViewBg;
    private DetectionVerification bv;//工具，包含判断是否为数字，IP格式等等
    public ChangIpDialog(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    //显示提示框
    public void showDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.change_ip_dialog,null,false);//绑定layout布局
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).setCancelable(false).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);//透明
        imageViewBg=view.findViewById(R.id.dialog_bg);


        Button btn_cancel_high_opion = view.findViewById(R.id.dialog_button_click);//获取控件
        editText_IP=view.findViewById(R.id.dialog_editText_IP);
        editText_Port=view.findViewById(R.id.dialog_editText_Port);
        ImageView imageView=view.findViewById(R.id.dialog_imageView_signOut);


        //判断是否保存有IP和端口号
        SharedPreferences sp=context.getSharedPreferences(OverallSituation.LOGIN_DATA_NAME,Activity.MODE_PRIVATE);
        String firstlogin = sp.getString(OverallSituation.USER_FIRSTLOGIN,"");
        if(firstlogin!=null&&firstlogin.equals("true")){//进行初始化
            editText_IP.setText(sp.getString(OverallSituation.USER_IP,""));
            editText_Port.setText(sp.getString(OverallSituation.USER_PORT,""));
        }


        bv=new DetectionVerification();//工具初始化
        btn_cancel_high_opion.setOnClickListener(new View.OnClickListener() {   //按钮确定按钮的点击事件监听
            @Override
            public void onClick(View v) {
                 String dataIP=""+editText_IP.getText().toString().trim();
                 String dataPort=""+editText_Port.getText().toString().trim();
                //调用检查输入
                if (inspectInput(dataIP,dataPort)){//inspectInput判断输入的IP，端口号 格式是否正确

                    Map<String,String> map=new HashMap<>(); //将消息传递到Activity界面提供更改IP和端口
                    map.put(OverallSituation.USER_IP,dataIP);
                    map.put(OverallSituation.USER_PORT,dataPort);
                    Message message=new Message();
                    message.what=999;
                    message.obj=map;
                    handler.sendMessage(message);

                    //点击后保存IP以及端口号
                    SharedPreferences.Editor editor = context.getSharedPreferences(OverallSituation.LOGIN_DATA_NAME, Activity.MODE_PRIVATE).edit();
                    editor.putString(OverallSituation.USER_IP,dataIP);
                    editor.putString(OverallSituation.USER_PORT,dataPort);
                    editor.commit();    //提交
                    dialog.dismiss(); //提交
                }



            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {    //退出IP输入框的点击监听
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"退出成功",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show(); //显示
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context)*3/6),(ScreenUtils.getScreenHeight(context)*3/5));


    }


    //检查输入，判断IP 与端口号是否正确
    private boolean inspectInput(String ip,String num){

        if(ip.equals("")||num.equals("")){
            Toast.makeText(context,"IP与端口号不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }

        int ipFormat=bv.isIp(ip);//是否为IP格式，0--纯数字，1--不全部是数字与点，2--不是IP格式，3--IP的值不是0~255
        if(ipFormat!=-1){
            switch (ipFormat){
                case 0:
                    Toast.makeText(context,"IP不能纯数字",Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(context,"IP只能输入数字与点",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(context,"不是正确的IP格式！\n格式 xxx.xxx.xxx.xxx",Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(context,"IP只能输入0~255",Toast.LENGTH_SHORT).show();
                    break;
            }
            editText_IP.setText("");
            return false;
        }
        if(!bv.isNumber(num)){
            Toast.makeText(context,"端口号只能是数字",Toast.LENGTH_SHORT).show();
            editText_Port.setText("");
            return false;
        }
        return true;
    }

}