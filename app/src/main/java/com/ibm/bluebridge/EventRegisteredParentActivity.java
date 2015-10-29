package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ibm.bluebridge.adapter.EventsAdapter;

import java.util.ArrayList;
import java.util.List;

public class EventRegisteredParentActivity extends EventMasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registered_parent);

        final Context ctxt = this;
        final EventsAdapter eventsAdapter = new EventsAdapter(ctxt);
        final ListView listView = (ListView) findViewById(R.id.listview);

        Intent intent = getIntent();
        final String event_id = intent.getStringExtra("event_id");

        List<String> parentList = eventsAdapter.getRegisteredParentsList(event_id);


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctxt, R.id.event_name,parentList.toArray(new String[parentList.size()]));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //show parent details
            }
        });
    }
}
