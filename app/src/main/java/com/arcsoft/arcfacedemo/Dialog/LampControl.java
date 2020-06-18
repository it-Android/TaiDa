package com.arcsoft.arcfacedemo.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.Tool.Data.Sensor;


public class LampControl {
    private Handler handler;
    private Sensor sensor;
    private Context context;
    private Dialog dialog;
    private ImageView ivClose;
    public LampControl(Handler handler, Sensor sensor, Context context) {
        this.handler = handler;
        this.sensor = sensor;
        this.context = context;
    }

    public void showDialog(){
        View view=LayoutInflater.from(context).inflate(R.layout.lamp_control_dialog,null,false);
        ivClose=view.findViewById(R.id.lamp_ivClose);
        dialog = new AlertDialog.Builder(context).setView(view).setCancelable(false).create();


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show(); //显示
       // dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(context)*4/6),(ScreenUtils.getScreenHeight(context)*4/6));

    }


}
