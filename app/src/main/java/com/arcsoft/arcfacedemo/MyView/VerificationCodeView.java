package com.arcsoft.arcfacedemo.MyView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class VerificationCodeView extends View implements View.OnClickListener{
    private Paint paintView;
    private Rect rect;
    private String  code="";
    private VerificationCode vc;
    public VerificationCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paintView=new Paint(Paint.ANTI_ALIAS_FLAG);
        rect=new Rect();
        vc=new VerificationCode(5);
        setOnClickListener(this);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        code=vc.getCode();
        paintView.setColor(Color.rgb(186,186,186));
        canvas.drawRect(0,0,getWidth(),getHeight(),paintView);
        paintView.setTextSize(40);
        paintView.setColor(Color.BLUE);
        paintView.setStrokeCap(Paint.Cap.ROUND);
        paintView.setAntiAlias(true);
        paintView.setTextSkewX(-0.25f);
        paintView.setTypeface(Typeface.DEFAULT_BOLD);
        paintView.setStrokeJoin(Paint.Join.ROUND);//圆角
        String text=String.valueOf(code);
        paintView.getTextBounds(text,0,text.length(),rect);
        float textwidth=rect.width()/2;
        float textheight=rect.height()/2;
        canvas.drawText(text,getWidth()/2-textwidth,getHeight()/2+textheight,paintView);

        paintView.setColor(Color.DKGRAY);

        Random r=new Random();
        for(int i=0;i<70;i++){
            long x=r.nextInt(getWidth()-6);
            long y=r.nextInt(getHeight()-6);
            canvas.drawRect(x,y,x+6,y+6,paintView);
        }


    }
    @Override
    public void onClick(View view) {
        invalidate();
    }

    public String getCode(){
        return code;
    }
}
