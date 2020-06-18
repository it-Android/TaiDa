package com.arcsoft.arcfacedemo.Dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.System.ScreenUtils;


public class RefreshDialog {
    private Context context;//
    private Handler handler;//消息提示
    private Boolean dialogHide=true;

    public RefreshDialog(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void setDialogHide(Boolean dialogHide) {
        this.dialogHide = dialogHide;
    }

    public void showDialog(){
        dialogHide=true;
        View view=LayoutInflater.from(context).inflate(R.layout.refresh_dialog,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(context).setView(view).setCancelable(false).create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.colorTransparent);


        Thread thread=new Thread(new Runnable() {
            int time=0;
            Boolean boo=true;
            @Override
            public void run() {
                while (dialogHide){
                    try {
                        time++;
                        if(time>=10){
                            boo=false;
                            break;
                        }
                        Thread.sleep(1000);
                    }catch (Exception e){time=10;}
                }

                if(handler!=null){
                    Message msg=new Message();
                    msg.what=1010;
                    msg.obj=boo;
                    dialog.dismiss();
                    handler.handleMessage(msg);
                }else{
                        dialog.dismiss();
                }

            }
        });

        thread.start();
         dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context)/5),(ScreenUtils.getScreenWidth(context)/5));
    }
}
