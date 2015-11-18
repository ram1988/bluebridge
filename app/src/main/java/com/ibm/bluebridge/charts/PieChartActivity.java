package com.ibm.bluebridge.charts;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ibm.bluebridge.R;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Expect "input" json string and "legend" when calling this Activity
 */
public class PieChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private PieChart mChart;
    private Typeface tf;
    SessionManager session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_pie);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        session = SessionManager.getSessionInstance(this);

        Intent input = getIntent();
        String data_json = input.getStringExtra("input");
        String legend = input.getStringExtra("legend");

        //data_json = "{\"Education\":5,\"Sports\":2}";
        //legend = "Number of registration by event category for current year";

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        //mChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        //mChart.setCenterText();

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        if(data_json == null || legend == null){
            Log.e("LineActivity", "Null data_json or legend from intent");
        }else{
            // add data
            setData(data_json, legend);
        }

        mChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.sendEmail(session.getUserEmail(), mChart, getApplicationContext());
            }
        });

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

        PieDataSet dataSet = new PieDataSet(yVals, legend);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tf);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }



    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}
