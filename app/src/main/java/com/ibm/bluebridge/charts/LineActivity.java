package com.ibm.bluebridge.charts;

import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.ibm.bluebridge.R;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Expect "input" json string, "max" and "legend" when calling this Activity
 */
public class LineActivity extends AppCompatActivity implements
        OnChartValueSelectedListener, OnChartGestureListener {
    private LineChart mChart;
    private String snapshotPath;
    SessionManager session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_line);
        Intent input = getIntent();
        String data_json = input.getStringExtra("input");
        String legend = input.getStringExtra("legend");
        int max = input.getIntExtra("max", 10);
        session = SessionManager.getSessionInstance(this);

        //data_json = "{\"2017\":2,\"2018\":3,\"2019\":1}";
        //legend = "number of parents by child registration year";

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.addLimitLine(llXAxis); // add x-axis limit line

        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        //LimitLine ll1 = new LimitLine(15f, "Upper Limit");
        //ll1.setLineWidth(4f);
        //ll1.enableDashedLine(10f, 10f, 0f);
        //ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        //ll1.setTextSize(10f);
        //ll1.setTypeface(tf);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        //leftAxis.addLimitLine(ll1);
        leftAxis.setAxisMaxValue(max+2);
        leftAxis.setAxisMinValue(0f);
        //leftAxis.setStartAtZero(false);
        //leftAxis.setYOffset(20f);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        if(data_json == null || legend == null){
            Log.e("LineActivity", "Null data_json or legend from intent");
        }else{
            // add data
            setData(data_json, legend);
        }

//      mChart.setVisibleXRange(20);
//      mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//      mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);


        final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.sendEmail(session.getUserEmail(), mChart, getApplicationContext());
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }

    private void setData(String data_json, String legend) {
        JSONObject obj = null;
        try{
            obj = new JSONObject(data_json);
        }catch(JSONException e){
            e.printStackTrace();
            Utils.showAlertDialog("Invalid data", this);
        }

        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        Iterator<String> i = obj.keys();
        int indx = 0;
        while(i.hasNext()){
            String key = i.next();
            try {
                float val = (float) obj.getDouble(key);
                yVals.add(new Entry(val, indx));
                xVals.add(key);
                indx++;
            }catch(JSONException e){
                e.printStackTrace();
                continue;
            }
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, legend);
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("", "low: " + mChart.getLowestVisibleXIndex() + ", high: " + mChart.getHighestVisibleXIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
