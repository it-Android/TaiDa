package com.arcsoft.arcfacedemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcsoft.arcfacedemo.R;
import com.arcsoft.arcfacedemo.faceserver.CompareResult;
import com.arcsoft.arcfacedemo.faceserver.FaceServer;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class ShowFaceInfoAdapter extends RecyclerView.Adapter<ShowFaceInfoAdapter.CompareResultHolder> {
    private List<CompareResult> compareResultList;
    private LayoutInflater inflater;

    public ShowFaceInfoAdapter(List<CompareResult> compareResultList, Context context) {
         inflater = LayoutInflater.from(context);
        this.compareResultList = compareResultList;
    }

    //每个项目吹气出一个视图
    @NonNull
    @Override
    public CompareResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_head, parent,false);
        CompareResultHolder compareResultHolder = new CompareResultHolder(itemView);

        compareResultHolder.imageView = itemView.findViewById(R.id.iv_item_head_img);
        compareResultHolder.Employ_name = itemView.findViewById(R.id.Text_name);
        compareResultHolder.Employ_num = itemView.findViewById(R.id.Text_num);
        compareResultHolder.Employ_stateTime = itemView.findViewById(R.id.Text_stateTime);
        compareResultHolder.Employ_stateTime1 = itemView.findViewById(R.id.Text_stateTime1);
        compareResultHolder.Employ_state = itemView.findViewById(R.id.Text_state);

        return compareResultHolder;
    }
    //适配渲染数据到视图
    @Override
    public void onBindViewHolder(@NonNull CompareResultHolder holder, int position) {
        if (compareResultList == null) {
            return;
        }
        //寻找图片
        //修改 1
        File imgFile = new File(FaceServer.ROOT_PATH + File.separator + FaceServer.SAVE_IMG_DIR + File.separator + compareResultList.get(position).getUser_Name() + FaceServer.IMG_SUFFIX);
        Glide.with(holder.imageView)
                .load(imgFile) //引入
                .into(holder.imageView);

        holder.Employ_name.setText("员工号："+compareResultList.get(position).getUser_Name());
        holder.Employ_num.setText("姓名："+compareResultList.get(position).getUser_num());
        Log.i("******************",compareResultList.get(position).getUser_stateTime());
        if(!TextUtils.isEmpty(compareResultList.get(position).getTime_success()))
        {
            String[] s1 = compareResultList.get(position).getTime_success().split(" ");
            Log.i("******************11111", compareResultList.get(position).getTime_success());

            holder.Employ_stateTime.setText("日期："+s1[1]);
            holder.Employ_stateTime1.setText("签到时间："+s1[2]);
        }
        if(compareResultList.get(position).getTimeCompare() == 1 || compareResultList.get(position).getTimeCompare() == 2)
        {
            holder.Employ_state.setText("已签到");
        }else
        {
            holder.Employ_state.setText("未签到");
        }

     }

    @Override
    public int getItemCount() {
        return compareResultList == null ? 0 : compareResultList.size();
    }

    class CompareResultHolder extends RecyclerView.ViewHolder {


        ImageView imageView;
        TextView Employ_name;
        TextView Employ_num;
        TextView Employ_stateTime;
        TextView Employ_stateTime1;
        TextView Employ_state;

        /*CompareResultHolder(@NonNull View itemView) {
            super(itemView);
        }*/

        public CompareResultHolder(View itemView) {
            super(itemView);
        }
    }
}
