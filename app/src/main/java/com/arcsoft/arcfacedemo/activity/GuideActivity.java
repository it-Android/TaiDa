package com.arcsoft.arcfacedemo.activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.arcsoft.arcfacedemo.R;

public class GuideActivity extends AppCompatActivity {
    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        imageView=(ImageView)findViewById(R.id.guide_iv);
        animationDrawable=(AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animationDrawable.stop();
                startActivity(new Intent(GuideActivity.this,LoginActivity.class));
                finish();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationDrawable.stop();
                startActivity(new Intent(GuideActivity.this,LoginActivity.class));
                finish();
            }
        },6000);
    }
}
