package com.arcsoft.arcfacedemo.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;


public class TipsDialog extends AlertDialog implements View.OnClickListener{
    private Context context;
    private String title="确认信息！";
    private String message="";
    private Button btnCancel,btnDetermine;
    private TextView tvTitle,tvMessage;
    private View.OnClickListener clickListener;

    public TipsDialog(Context context,View.OnClickListener clickListener) {
        super(context);
        this.context = context;
        this.clickListener=clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tips_dialog_layout);
        showDialog();
    }


    public void showDialog(){
        btnCancel=(Button)findViewById(R.id.tips_diglog_cancel);
        tvTitle=(TextView)findViewById(R.id.tips_diglog_title);
        tvMessage=(TextView)findViewById(R.id.tips_diglog_message);
        btnDetermine=(Button)findViewById(R.id.tips_diglog_determine);


        btnCancel.setOnClickListener(this);
        btnDetermine.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tips_diglog_cancel:
                //clickListener.onClick(btnCancel);
                dismiss();
                break;
            case R.id.tips_diglog_determine:
                clickListener.onClick(btnDetermine);
                dismiss();
                break;
        }

    }



    public String getTitle() {
        return tvTitle.getText().toString();
    }

    public void setTitle(String title) {
        this.title = title;
        tvTitle.setText(title);
    }

    public String getMessag() {
        return tvMessage.getText().toString();
    }

    public void setMessag(String message) {
        this.message = message;
        tvMessage.setText(message);
    }

}
