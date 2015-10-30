package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

import java.util.ArrayList;
import java.util.List;

public class EventRegisteredParentActivity extends EventMasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registered_parent);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ListView listView = (ListView) findViewById(R.id.listview);

        final Context ctxt = this;
        final EventsAdapter eventsAdapter = new EventsAdapter(ctxt);
        Intent intent = getIntent();
        final String event_id = intent.getStringExtra("event_id");
        List<Parent> parentList = eventsAdapter.getRegisteredParentsList(event_id);

        // specify an adapter (see also next example)
        ArrayAdapter<Parent> mAdapter = getParentListItemAdapter(ctxt,parentList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Parent item = (Parent) parent.getItemAtPosition(position);

                Intent intent = new Intent(ctxt, EventParentDetailsActivity.class);
                intent.putExtra("ParentObj", item);

                startActivity(intent);
            }
        });

    }


}
