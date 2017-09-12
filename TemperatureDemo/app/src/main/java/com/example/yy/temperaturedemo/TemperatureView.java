package com.example.yy.temperaturedemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yy on 2017/9/11.
 */

public class TemperatureView extends LinearLayout{

    public static int MAX_TEMPERATURE = 27;//最高温度
    public static int MIN_TEMPERATURE = 16;//最低温度

    private VerticalSeekBar verticalSeekBar;
    private ListView temperature_number_lv;
    private TemperatureAdapter adapter;

    private Context mContext;
    private int mMaxIndex;//最大索引
    private int mCurrentIndex = -1;//当前温度索引

    public TemperatureView(Context context) {
        this(context,null);
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    /**
     * 初始化view
     */
    private void init(AttributeSet attrs) {
        //初始化数据
        LayoutInflater.from(mContext).inflate(R.layout.layout_temperature_view,this);
        temperature_number_lv = (ListView) findViewById(R.id.temperature_number_lv);
        List<String> numberList = new ArrayList<>();
        for (int i=MAX_TEMPERATURE; i >= MIN_TEMPERATURE;i--){
            numberList.add(i+"");
        }
        adapter = new TemperatureAdapter(mContext,numberList);
        temperature_number_lv.setAdapter(adapter);

        //seekbar相关
        verticalSeekBar = (VerticalSeekBar) findViewById(R.id.seek_temperature_color);
        mMaxIndex = MAX_TEMPERATURE-MIN_TEMPERATURE;
        verticalSeekBar.setOnStateChangeListener(new VerticalSeekBar.OnStateChangeListener() {
            @Override
            public void OnStateChangeListener(View view, float progress) {
                if (progress < 0) {
                    progress = 0;
                }
                if(progress > 100) {
                    progress = 100;
                }
                setTemperaturePercent(1-(progress/100));
            }

            @Override
            public void onStopTrackingTouch(View view, float progress) {
            }
        });

        //自定义属性
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TemperatureView);
        MAX_TEMPERATURE = typedArray.getInteger(R.styleable.TemperatureView_max_temperature,MAX_TEMPERATURE);
        MIN_TEMPERATURE = typedArray.getInteger(R.styleable.TemperatureView_min_temperature,MIN_TEMPERATURE);
        verticalSeekBar.setmThumbColor(typedArray.getColor(R.styleable.TemperatureView_thumb_color, Color.WHITE));
        verticalSeekBar.setmThumbBorderColor(typedArray.getColor(R.styleable.TemperatureView_thumb_border_color, Color.TRANSPARENT));
        int startColor = typedArray.getColor(R.styleable.TemperatureView_background_start_color,mContext.getResources().getColor(R.color.temperature_color_first));
        int secondColor = typedArray.getColor(R.styleable.TemperatureView_background_middle_color,mContext.getResources().getColor(R.color.temperature_color_second));
        int thirdColor = typedArray.getColor(R.styleable.TemperatureView_background_end_color,mContext.getResources().getColor(R.color.temperature_color_third));
        verticalSeekBar.setColorArray(new int[]{startColor,secondColor,thirdColor});
    }

    /**
     * 设置温度的数值百分比
     * @param progress
     * @return 是否可以更新seekbar
     */
    private void setTemperaturePercent(float progress) {

        //设置要变大的字体
        int childIndex = (int)(mMaxIndex*progress+0.5);
        if (childIndex<=MAX_TEMPERATURE-MIN_TEMPERATURE&&childIndex>=0){
            mCurrentIndex = childIndex;
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * sp转化成px
     */
    private static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }

    /**
     * 设置温度
     */
    public void setTemperature(float temperature){
        if (temperature<=MAX_TEMPERATURE&&temperature>=MIN_TEMPERATURE){
            //listview的设置
            float listProgress = (temperature-MIN_TEMPERATURE)/(MAX_TEMPERATURE-MIN_TEMPERATURE);
            setTemperaturePercent(1-listProgress);

            //seekbar的设置
            float seekProgress = (temperature-MIN_TEMPERATURE+1)/(MAX_TEMPERATURE-MIN_TEMPERATURE+2)*100;
            verticalSeekBar.setProgress(seekProgress);
        }
    }

    /**
     * 获取温度值
     * @return
     */
    public int getTemprature() {
        return MAX_TEMPERATURE-mCurrentIndex;
    }

    /**
     * 温度值的adapter
     */
    private class TemperatureAdapter extends BaseAdapter {

        private List<String> mNumberList;//数据
        private Context mContext;

        public TemperatureAdapter(Context context,List<String> numberList) {
            this.mNumberList = numberList;
            mContext = context;
        }

        @Override
        public int getCount() {
            if (mNumberList!=null){
                return mNumberList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mNumberList!=null){
                return mNumberList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView==null){
                LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_temperature_number,null);
                holder = new ViewHolder();
                holder.temperature_number_tv = (TextView) convertView.findViewById(R.id.temperature_number_tv);
                holder.temperature_degree_tv = (TextView) convertView.findViewById(R.id.temperature_degree_tv);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.temperature_number_tv.setText(mNumberList.get(position));

            //放大字体
            if (position==mCurrentIndex){
                holder.temperature_number_tv.setTextSize(sp2px(mContext,8));
                holder.temperature_degree_tv.setVisibility(VISIBLE);
            }else {
                holder.temperature_number_tv.setTextSize(sp2px(mContext,6));
                holder.temperature_degree_tv.setVisibility(GONE);
            }
            return convertView;
        }

        private class ViewHolder{
            public TextView temperature_number_tv;
            public TextView temperature_degree_tv;
        }
    }
}
