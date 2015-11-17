package com.ibm.bluebridge.charts;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ibm.bluebridge.R;
import com.ibm.bluebridge.util.RESTApi;
import com.ibm.bluebridge.util.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class AdminChart1Activity extends AppCompatActivity implements OnChartValueSelectedListener {
    protected HorizontalBarChart mChart;
    private Typeface tf;

    private SessionManager session;
    private String chart1_base_url = "/admin_summary_parents_attended_hours";
    private RESTApi REST_API;
    private JSONObject responseObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chart1);
        session = SessionManager.getSessionInstance(getApplicationContext());
        REST_API = new RESTApi();

        REST_API.getResponse(REST_API.getBaseRestURL() + chart1_base_url);
        JSONObject responseObj = REST_API.getRespObj();

        mChart = (HorizontalBarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        // mChart.setHighlightEnabled(false);

        mChart.setDrawBarShadow(false);

        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        // mChart.setDrawYLabels(false);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);
//        yl.setInverted(true);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
//        yr.setInverted(true);

        setData();
        mChart.animateY(2500);
    }

    private void setData() {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        Log.d("AdminChar1Activity", responseObj.toString());
        Iterator<String> itr = responseObj.keys();
        int i = 0;
        while(itr.hasNext()){
            String key = itr.next();

            xVals.add(key);
            try {
                yVals1.add(new BarEntry((float) responseObj.getDouble(key), i));
            }catch(Exception e){
                Log.e("AdminChart1Activity", "Cannot parse key +"+key);
                continue;
            }
        }

        //for (int i = 0; i < count; i++) {
        //    xVals.add(mMonths[i % 12]);
        //    yVals1.add(new BarEntry((float) (Math.random() * range), i));
        //}

        BarDataSet set1 = new BarDataSet(yVals1, "every parent's finished hour");

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(tf);

        mChart.setData(data);
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, mChart.getData().getDataSetByIndex(dataSetIndex)
                .getAxisDependency());

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());
    }

    public void onNothingSelected() {
    };

}
