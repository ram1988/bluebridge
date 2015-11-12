package com.ibm.bluebridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ibm.bluebridge.adapter.StatisticsAdapter;
import com.ibm.bluebridge.valueobject.ChartItem;

import java.util.ArrayList;
import java.util.List;

public class AdminStatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        setSupportActionBar(toolbar);

        List<ChartItem> charts = new ArrayList<ChartItem>();

        ChartItem chart1 = new ChartItem();
        chart1.setCategory("Parents Registration by Year");
        ChartItem chart2 = new ChartItem();
        chart2.setCategory("Parents Completion of Volunteer");

        charts.add(chart1);
        charts.add(chart2);

        StatisticsAdapter adapter = new StatisticsAdapter(this, charts);

        final ListView listview = (ListView) findViewById(R.id.statistics_listview);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Intent chart = new Intent();
                chart.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.charts.LineActivity");


                startActivity(chart);

            }
        });


    }

}
