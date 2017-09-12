package com.example.yy.temperaturedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private TemperatureView temperature_color_number_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        temperature_color_number_view = (TemperatureView) findViewById(R.id.temperature_color_number_view);
        temperature_color_number_view.setTemperature(16);

        int temperature = temperature_color_number_view.getTemprature();
        Log.d("yy","temperature:"+temperature);
    }
}
