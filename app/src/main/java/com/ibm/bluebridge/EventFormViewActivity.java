package com.ibm.bluebridge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.ibm.bluebridge.valueobject.Event;

import java.util.Calendar;

public class EventFormViewActivity extends EventMasterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form_view);

        Button addEditButton = (Button)findViewById(R.id.add_update_button);

        /*
         * event_id is passed and the description ie fetched from api
         * and displayed here
         *
         */
        Intent intent = getIntent();
        String actionType = intent.getStringExtra("EventAction");
        Event currentEvent = (Event)intent.getSerializableExtra("EventObj");
        String eventDesc = null;

        if(actionType.equals("edit")) {
            eventDesc = intent.getStringExtra("EventDesc");
            TextView descTxtView = (TextView)findViewById(R.id.event_title);

            addEditButton.setText("Update Event Details");
            //TextView txtView = new TextView(this);
            descTxtView.setText(eventDesc);
        }
        else if(actionType.equals("add")) {
            addEditButton.setText("Add Event Details");
        }
        setCurrentDateOnView();

    }


    // display current date
    public void setCurrentDateOnView() {

        DatePicker evtDatePick
                = (DatePicker) findViewById(R.id.event_date_picker);

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // set current date into datepicker
        evtDatePick.init(year, month, day, null);

    }


}
