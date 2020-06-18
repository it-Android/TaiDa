package com.arcsoft.arcfacedemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


import com.arcsoft.arcfacedemo.model.DrawInfo;
import com.arcsoft.arcfacedemo.util.DrawHelper;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceRectView extends View {
    private static final String TAG = "FaceRectView";
    //并发集合 允许多线程进行读写和访问
    private CopyOnWriteArrayList<DrawInfo> faceRectList = new CopyOnWriteArrayList<>();

    public FaceRectView(Context context) {
        this(context, null);
    }
    //AttributeSet attrs 属性组
    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //Canvas 画布  获取矩形进行绘制
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRectList != null && faceRectList.size() > 0) {
        for (int i = 0; i < faceRectList.size(); i++) {
            DrawHelper.drawFaceRect(canvas, faceRectList.get(i), Color.YELLOW, 5);
        }
    }
}

    public void clearFaceInfo() {
            faceRectList.clear();
            postInvalidate(); //直接调用线程
        }

    public void addFaceInfo(DrawInfo faceInfo) {
        faceRectList.add(faceInfo);
        postInvalidate();
    }

    public void addFaceInfo(List<DrawInfo> faceInfoList) {
        faceRectList.addAll(faceInfoList);
        postInvalidate();
    }
}