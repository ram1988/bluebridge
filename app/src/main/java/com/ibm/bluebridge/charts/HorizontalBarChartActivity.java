package com.ibm.bluebridge.charts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ibm.bluebridge.R;
import com.ibm.bluebridge.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Expect "input" json string and "legend" when calling this Activity
 */
public class HorizontalBarChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    protected HorizontalBarChart mChart;
    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_horizontal_bar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Intent input = getIntent();
        String data_json = input.getStringExtra("input");
        String legend = input.getStringExtra("legend");

        //data_json = "{\"Sunny Chow\":8.25," +
        //        "\"Felicia Ng\":5.5," +
        //        "\"Maria Tay\":10.1," +
        //        "\"Andrew Ng\":9.3" +
        //        "}";
        //legend = "every parent's finished hour";

        mChart = (HorizontalBarChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.setDescription("");
        mChart.setMaxVisibleValueCount(60);
        mChart.setPinchZoom(false);
        mChart.setDrawGridBackground(false);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        XAxis xl = mChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setTypeface(tf);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(true);
        xl.setGridLineWidth(0.3f);

        LimitLine ll = new LimitLine(10f, "Finish Goal");
        ll.setLineWidth(4f);
        ll.setLineColor(Color.RED);
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextColor(Color.RED);
        ll.setTextSize(10f);
        ll.setTypeface(tf);

        YAxis yl = mChart.getAxisLeft();
        yl.setTypeface(tf);
        yl.removeAllLimitLines();
        yl.addLimitLine(ll);
        yl.setAxisMaxValue(13f);
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setGridLineWidth(0.3f);

        YAxis yr = mChart.getAxisRight();
        yr.setTypeface(tf);
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);

        setData(data_json, legend);
        mChart.animateY(2500);

        final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.sendEmail(mChart, getApplicationContext());
            }
        });
    }

    private void setData(String data_json, String legend) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();

        JSONObject obj = null;

        try{
            obj = new JSONObject(data_json);
        }catch(JSONException e){
            e.printStackTrace();
            Log.d("HorizontalBarChart", "Cannot parse JSON");
            Utils.showAlertDialog("No data", this);
            return;
        }

        Iterator<String> itr = obj.keys();
        int i = 0;
        while(itr.hasNext()){
            String key = itr.next();

            try {
                Log.d("HorizontalBarChart", String.valueOf((float)obj.getDouble(key)));
                yVals1.add(new BarEntry((float) obj.getDouble(key), i));
                xVals.add(key);
                i++;
            }catch(Exception e){
                Log.e("HorizontalBarChart", "Cannot parse key +"+key);
                continue;
            }
        }

        BarDataSet set1 = new BarDataSet(yVals1, legend);

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

        Log.i("HorizontalBarChart", "Bounds: "+ bounds.toString());
        Log.i("HorizontalBarChart", "Position: " + position.toString());
    }

    public void onNothingSelected() {
    };
}
