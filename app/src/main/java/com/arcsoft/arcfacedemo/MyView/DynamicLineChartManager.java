package com.arcsoft.arcfacedemo.MyView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.arcsoft.arcfacedemo.Tool.System.Utilities;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class DynamicLineChartManager {


    //https://blog.csdn.net/ww897532167/article/details/77334345  教学网址
    private LineChart lineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private List<ILineDataSet> lineDataSets = new ArrayList<>();

    //private SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式  
    private List<String> timeList = new ArrayList<>(); //存储x轴的时间
    private int count=10;
    private Drawable drawable;
    //一条曲线
    public DynamicLineChartManager(LineChart mLineChart, String name, int color,int count ,Drawable drawable) {
        this.drawable=drawable;
        this.lineChart = mLineChart;
        this.count=count;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart(count);//初始化
        initLineDataSet(name, color,drawable);
    }

    //多条曲线
    public DynamicLineChartManager(LineChart mLineChart, List<String> names, List<Integer> colors) {
        this.lineChart = mLineChart;
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart(count);//初始化
        initLineDataSet(names, colors);
    }
    /**
     * 初始化LineChar
     * X轴：XAxis
     * Y轴：YAxis
     * 图例：Legend
     * 描述：Description
     * 限制线：LimitLine
     */
    private void initLineChart(int count) {

        lineChart.setDrawGridBackground(false);
        //显示边界
        lineChart.setDrawBorders(true);
        lineChart.setDoubleTapToZoomEnabled(false);//禁止双击放大

        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);//隐藏描述
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(15f);
        //显示位置
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setTextColor(Color.YELLOW);//标签颜色
        legend.setDrawInside(false);

        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.WHITE);//设置X轴底部文字的颜色


        xAxis.setGridColor(Color.BLACK);//设置竖线颜色

        xAxis.setGridLineWidth(0.5f);//设置竖线大小


        xAxis.setLabelCount(count,false);//
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    return timeList.get((int) value % timeList.size());
                }catch (Exception e){
                    return "00:00:00";
                }
            }
        });
        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.BLUE);//左边字体的颜色

        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.GREEN);//右边字体的颜色
        rightAxis.setGridLineWidth(0.5f);//网格的横向颜色
        rightAxis.setGridColor(Color.BLACK); //网格线颜色

    }

    /**
     * 初始化折线(一条线)
     * @param name
     * @param color
     */
    private void initLineDataSet(String name, int color,Drawable drawable) {




        lineDataSet = new LineDataSet(null, name);
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setCircleRadius(2.0f);
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(Color.WHITE);//圆心的颜色
        lineDataSet.setDrawCircleHole(false);//实心
        lineDataSet.setHighLightColor(color);
        //设置曲线填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setValueTextColor(Color.YELLOW);//设置曲线字体颜色
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    //线条渐变线
    public void setChartFillDrawable(Drawable drawable) {
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() >0) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
            lineChart.invalidate();
        }
    }
    /**
     * 初始化折线（多条线）
     * @param names
     * @param colors
     */
    private void initLineDataSet(List<String> names, List<Integer> colors) {

        for (int i = 0; i < names.size(); i++) {
            lineDataSet = new LineDataSet(null, names.get(i));
            lineDataSet.setColor(colors.get(i));
            lineDataSet.setLineWidth(1.5f);
            lineDataSet.setCircleRadius(1.5f);
            lineDataSet.setColor(colors.get(i));

            lineDataSet.setDrawFilled(true);
            lineDataSet.setCircleColor(colors.get(i));
            lineDataSet.setHighLightColor(colors.get(i));
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSet.setValueTextSize(10f);
            lineDataSets.add(lineDataSet);


        }
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */

    public void addEntry(float number,String time) {

        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);
//        //避免集合数据过多，及时清空（做这样的处理，并不知道有没有用，但还是这样做了）
//        if (timeList.size() > count<<7) {
//            timeList.clear();
//        }
        timeList.add(time);

        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(count);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
    }

    /**
     * 动态添加数据（一条折线图）
     *
     * @param number
     */

    public void addEntry(float number) {

        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);
        //避免集合数据过多，及时清空（做这样的处理，并不知道有没有用，但还是这样做了）
        if (timeList.size() > count<<7) {
            timeList.clear();
        }
        timeList.add(Utilities.getTime());

        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(count);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 5);
        if(drawable!=null)
            setChartFillDrawable(drawable);
    }



    /**
     * 动态添加数据（多条折线图）
     * @param numbers
     */
    public void addEntry(List<Float> numbers) {

        if (lineDataSets.get(0).getEntryCount() == 0) {
            lineData = new LineData(lineDataSets);
            lineChart.setData(lineData);
        }
        if (timeList.size() > count<<7) {
            timeList.clear();
        }
        timeList.add(Utilities.getTime());
        for (int i = 0; i < numbers.size(); i++) {
            Entry entry = new Entry(lineDataSet.getEntryCount(), numbers.get(i));
            lineData.addEntry(entry, i);
            lineData.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(6);
            lineChart.moveViewToX(lineData.getEntryCount() - 5);
        }
    }

    /**
     * 设置Y轴值
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);//设置Y轴的刻度数量

        rightAxis.setAxisMaximum(max);
        rightAxis.setAxisMinimum(min);
        rightAxis.setLabelCount(labelCount, false);

        lineChart.invalidate();
    }

    /**
     * 设置高限制线
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(3f);
        hightLimit.setTextSize(9f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置低限制线
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name,int color) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(3f);
        hightLimit.setTextSize(9f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }

}