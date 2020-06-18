package com.arcsoft.arcfacedemo.MyView;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.arcsoft.arcfacedemo.R;


public class CirclePercentBar extends View{

    private Context mContext;
    private Boolean twoWay=false;//是否开启双向
    private int mArcColor;//圆环的颜色
    private int mArcWidth;//圆环的宽



    private int minnerCircleColor;//内圆的颜色
    private int mTitleTextColor;//标题字体颜色
    private int mTitleTextSize;//标题字体大小
    private int mCenterTextColor;//字体颜色
    private int mCenterTextSize;//字体大小
    private int mCircleRadius;//圆的半径
    private Paint arcPaint;
    private Paint innerCirclePaint;
    private Paint arcCirclePaint;
    private Paint titleTextPaint;//画标题
    private Paint centerTextPaint;
    private RectF arcRectF;
    private Rect textBoundRect;
    private float mCurData=0;//数值
    private String mTitleText="";//标题
    private String mSuffix="";//默认为%

    private int arcStartColor;//起始颜色（正向）
    private int arcStartColorReverse;//起始颜色（反向）

    private int arcEndColor;//末尾颜色 （正向）
    private int arcEndColorReverse;//末尾颜色 （正向）

    private Paint startCirclePaint;

    public CirclePercentBar(Context context) {
        this(context, null);
    }

    public CirclePercentBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePercentBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CirclePercentBar,defStyleAttr,0);
        mArcColor = typedArray.getColor(R.styleable.CirclePercentBar_arcColor,0xff0000);//外圆环的颜色

        mSuffix=typedArray.getString(R.styleable.CirclePercentBar_suffix);
        if(mSuffix==null)mSuffix="%";
        twoWay=typedArray.getBoolean(R.styleable.CirclePercentBar_twoWay,false);//默认不开启双向

        mArcWidth = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_arcWidth, DisplayUtil.dp2px(context, 20));//外圆环的宽度

        minnerCircleColor=typedArray.getColor(R.styleable.CirclePercentBar_innerCircleColor,0x00ffffff);//内圆

        mTitleText=typedArray.getString(R.styleable.CirclePercentBar_titleText);//标题字体
        mTitleTextColor = typedArray.getColor(R.styleable.CirclePercentBar_titleTextColor, 0xFF000000);//标题文字颜色
        mTitleTextSize = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_titleTextSize, DisplayUtil.dp2px(context, 20));//文字的大小

        mCenterTextColor = typedArray.getColor(R.styleable.CirclePercentBar_centerTextColor, 0xFF000000);//文字颜色
        mCenterTextSize = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_centerTextSize, DisplayUtil.dp2px(context, 20));//文字的大小
        mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.CirclePercentBar_circleRadius, DisplayUtil.dp2px(context, 100));//圆的半径


        arcStartColor = typedArray.getColor(R.styleable.CirclePercentBar_arcStartColor,
                ContextCompat.getColor(mContext, R.color.colorStart));//开始的颜色
        arcStartColorReverse = typedArray.getColor(R.styleable.CirclePercentBar_arcEndColorReverse,
                ContextCompat.getColor(mContext, R.color.colorEnd));//开始的颜色（反向）
        arcEndColor = typedArray.getColor(R.styleable.CirclePercentBar_arcEndColor,
                ContextCompat.getColor(mContext, R.color.colorEnd));//末尾的颜色
        arcEndColorReverse = typedArray.getColor(R.styleable.CirclePercentBar_arcStartColorReverse,
                ContextCompat.getColor(mContext, R.color.colorStart));//末尾的颜色（反向）
        typedArray.recycle();

        initPaint();

    }

    private void initPaint() { //初始化画笔

        startCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startCirclePaint.setStyle(Paint.Style.FILL);
        //startCirclePaint.setStrokeWidth(mArcWidth);

        startCirclePaint.setColor(arcStartColor);


        innerCirclePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setStrokeWidth(mArcWidth);
        innerCirclePaint.setColor(minnerCircleColor);
        innerCirclePaint.setAntiAlias(true);

        arcCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//圆环背景
        arcCirclePaint.setStyle(Paint.Style.STROKE);
        arcCirclePaint.setStrokeWidth(mArcWidth);
        arcCirclePaint.setColor(ContextCompat.getColor(mContext,R.color.colorCirclebg));
        arcCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//圆环
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(mArcWidth);
        arcPaint.setColor(mArcColor);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        titleTextPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//标题字体设置
        titleTextPaint.setStyle(Paint.Style.STROKE);
        titleTextPaint.setColor(mTitleTextColor);
        titleTextPaint.setTextSize(mTitleTextSize);

        centerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//内容
        centerTextPaint.setStyle(Paint.Style.STROKE);
        centerTextPaint.setColor(mCenterTextColor);
        centerTextPaint.setTextSize(mCenterTextSize);

        //圓弧的外接矩形
        arcRectF = new RectF();

        //文字的边界矩形
        textBoundRect = new Rect();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureDimension(widthMeasureSpec),measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int specMode=MeasureSpec.getMode(measureSpec);
        int specSize=MeasureSpec.getSize(measureSpec);
        if(specMode==MeasureSpec.EXACTLY){
            result=specSize;
        }else{
            result=mCircleRadius*2;
            if(specMode==MeasureSpec.AT_MOST){
                result=Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.rotate(-90, getWidth()/ 2, getHeight()/ 2);//设置旋转

        arcRectF.set(getWidth()/2-mCircleRadius+mArcWidth/2,getHeight()/2-mCircleRadius+mArcWidth/2    //圓弧的外接矩形
                ,getWidth()/2+mCircleRadius-mArcWidth/2,getHeight()/2+mCircleRadius-mArcWidth/2);

        drawInnerCircle(canvas);//画内圆

        canvas.drawArc(arcRectF, 0,360,false,arcCirclePaint);//画圆环背景

        if(mCurData>=0){
            arcPaint.setShader(new SweepGradient(getWidth()/2,getHeight()/2,arcStartColor,arcEndColor));
        }else{
            arcPaint.setShader(new SweepGradient(getWidth()/2,getHeight()/2,arcStartColorReverse,arcEndColorReverse));
        }
        canvas.drawArc(arcRectF, 0,360* mCurData /100,false,arcPaint);//进度情况，0进度画到 360度的百分比

        canvas.rotate(90, getWidth()/ 2, getHeight()/ 2);//圆环初始的圆点和字体的方向
        if(mCurData>=0){
            startCirclePaint.setColor(arcStartColor);
        }else{
            startCirclePaint.setColor(arcEndColorReverse);
        }
        canvas.drawCircle(getWidth()/2,getHeight()/2-mCircleRadius+mArcWidth/2,mArcWidth/2,startCirclePaint);//画起点圆点

        drawText(canvas);//画文字
    }

    //设置字体
    public void setTitleText(String mTitleText) {
        this.mTitleText = mTitleText;
    }
    //设置背景颜色
    public void setInnerCircleColor(int minnerCircleColor) {
        this.minnerCircleColor = minnerCircleColor;
    }
    //画内圆
    private void drawInnerCircle(Canvas canvas){
        innerCirclePaint.setColor(minnerCircleColor);
        //内圆
        int radius = Math.min(getWidth()-mCircleRadius/2, getHeight()-mCircleRadius/2)/ 2;//向外扩展了小部分
        //int radius = Math.min(getWidth()-mArcWidth*2, getHeight()-mArcWidth*2)/ 2;//刚刚好充满
        canvas.drawCircle(getWidth()/2,getHeight()/2,radius,innerCirclePaint);
    }

    //画文字
    private void drawText(Canvas canvas){
        //画内容
        String data= String.valueOf(mCurData) + mSuffix;//内容
        centerTextPaint.getTextBounds(mSuffix,0,mSuffix.length(),textBoundRect);//从0画到末尾

        int mSuffixSize=0;//获取单位的的长度

        centerTextPaint.getTextBounds(data,0,data.length(),textBoundRect);//从0画到末尾
        int dataWidth=textBoundRect.width()/2,dataheight=textBoundRect.height()/2;
        canvas.drawText(data,getWidth()/2-dataWidth+mSuffixSize,getHeight()/2+dataheight,centerTextPaint);//画出字体

        //画标题
        titleTextPaint.getTextBounds(mTitleText,0,mTitleText.length(),textBoundRect);//从0画到末尾
        canvas.drawText(mTitleText,getWidth()/2-textBoundRect.width()/2,getHeight()/2-dataheight-dataheight/2,titleTextPaint);//画出字体
    }


    public void setPercentData(float data, final String suffix, TimeInterpolator interpolator){
        if(!twoWay&&data<0)data=0;
        ValueAnimator valueAnimator=ValueAnimator.ofFloat(mCurData,data);
        valueAnimator.setDuration((long) (Math.abs(mCurData-data)*30));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value= (float) valueAnimator.getAnimatedValue();
                mCurData=(float)(Math.round(value*10))/10;
                if(suffix!=null){ mSuffix = suffix; }

                invalidate();
            }
        });
        if(interpolator!=null){valueAnimator.setInterpolator(interpolator);}else{valueAnimator.setInterpolator(new DecelerateInterpolator());}
        valueAnimator.start();
    }
}
