package com.arcsoft.arcfacedemo.MyView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;


public class MonitorView extends RelativeLayout {

    private TextView textView_num,textView_text;
    private String titleText =null;
    private String contentText =null;
    private int titleTextColor,contentTextColor;
    private int titleTextSize,contentTextSize;

    public MonitorView(Context context) {
        super(context);
    }

    public MonitorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view=View.inflate(getContext(),R.layout.view_show_data,this);
        textView_num=(TextView)findViewById(R.id.tv_num);
        textView_text=(TextView)findViewById(R.id.tv_text);
        TypedArray appearance=getContext().obtainStyledAttributes(attrs,R.styleable.MonitorView);
        titleText = appearance.getString(R.styleable.MonitorView_MOtitleText);
        titleTextColor = appearance.getColor(R.styleable.MonitorView_MOtitleTextColor,Color.BLACK);
        titleTextSize=appearance.getDimensionPixelSize(R.styleable.MonitorView_MOtitleTextSize,18);
        contentText = appearance.getString(R.styleable.MonitorView_MOcontentText);
        contentTextColor = appearance.getColor(R.styleable.MonitorView_MOcontentTextColor,Color.BLACK);
        contentTextSize=appearance.getDimensionPixelSize(R.styleable.MonitorView_MOcontentTextSize,10);

        textView_num.setText(titleText);
        textView_num.setTextColor(titleTextColor);
        textView_num.setTextSize(titleTextSize);

        textView_text.setText(contentText);
        textView_text.setTextColor(contentTextColor);
        textView_text.setTextSize(contentTextSize);
        appearance.recycle();
    }


    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        textView_num.setText(titleText);

    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
        textView_text.setText(contentText);
    }

    public int getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        textView_num.setTextColor(titleTextColor);
    }

    public int getContentTextColor() {
        return contentTextColor;
    }

    public void setContentTextColor(int contentTextColor) {
        this.contentTextColor = contentTextColor;
        textView_text.setTextColor(contentTextColor);
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
        textView_num.setTextSize(titleTextSize);
    }

    public int getContentTextSize() {
        return contentTextSize;
    }

    public void setContentTextSize(int contentTextSize) {
        this.contentTextSize = contentTextSize;
        textView_text.setTextSize(contentTextSize);
    }
}
