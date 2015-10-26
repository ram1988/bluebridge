package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.valueobject.Event;
import com.melnykov.fab.FloatingActionButton;

import java.util.List;

public class EventAdminHomeActivity extends EventMasterActivity {

    //sample admin A000000E
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_admin_home);

        final Context ctxt = this;
        final EventsAdapter eventsAdapter = new EventsAdapter(ctxt);
        final ListView listView = (ListView) findViewById(R.id.listview);
        TextView noEventsMsg = (TextView)findViewById(R.id.no_events_message);

        Intent intent = getIntent();
        String admin_id = intent.getStringExtra("user_id");

        List<Event> eventList = eventsAdapter.getAdminEventsList(admin_id);

        if(eventList.isEmpty()){
            noEventsMsg.setVisibility(View.VISIBLE);
        } else {
            noEventsMsg.setVisibility(View.INVISIBLE);

            final ArrayAdapter<Event> adapter = getEventArrayAdapter(ctxt, eventList.toArray(new Event[eventList.size()]));
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    final Event item = (Event) parent.getItemAtPosition(position);

                    Intent intent = new Intent(ctxt, EventFormViewActivity.class);
                    intent.putExtra("EventAction", 1);
                    intent.putExtra("EventObj", item);

                    startActivity(intent);
                }
            });
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            private int count = 0;

            @Override
            public void onClick(View v) {


                Intent intent = new Intent(ctxt, EventFormViewActivity.class);
                intent.putExtra("EventAction", "add");
                startActivity(intent);

                /*
                //Write here anything that you wish to do on click of FAB
                count = count+1;
                eventsAdapter.addEvent("sampleEvent"+count);

                //Notifies the adapter to update the listview since a new data has been added
                adapter.notifyDataSetChanged ();
                */
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
