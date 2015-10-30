package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;

import java.text.DecimalFormat;
import java.util.Calendar;

public class EventFormViewActivity extends EventMasterActivity {

    private Context ctxt;
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_form_view);

        /*
         * event_id is passed and the description ie fetched from api
         * and displayed here
         *
         */
        ctxt = this;
        Intent intent = getIntent();
        int mode = intent.getIntExtra("EventAction", 0);
        adminId = intent.getStringExtra("admin_id");
        Event currentEvent = (Event)intent.getSerializableExtra("EventObj");


        //setCurrentDateOnView();
        populateControls(currentEvent, mode);
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

    //add mode-0, edit mode-1, read mode-2
    private void populateControls(final Event event,int mode) {
        Button actionButton = (Button) findViewById(R.id.action_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        Button showRegButton = (Button) findViewById(R.id.list_parents_button);
        final EventsAdapter eventsAdapter = new EventsAdapter(this);

        if(mode == 0) {
            actionButton.setText("Add");
            cancelButton.setVisibility(View.INVISIBLE);
            showRegButton.setVisibility(View.INVISIBLE);

            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event newEvent = new Event();
                    newEvent =  setEvent(newEvent);
                    eventsAdapter.addEvent(newEvent,adminId);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ctxt, EventAdminHomeTabActivity.class);
                    intent.putExtra("user_id", adminId);
                    intent.putExtra("message","Event Added!!!");

                    startActivity(intent);

                }
            });
        }
        else if(mode == 1) {
            EditText eventTitle = (EditText) findViewById(R.id.event_title);
            EditText duty = (EditText) findViewById(R.id.event_desc_edit);
            DatePicker eventDatePicker = (DatePicker) findViewById(R.id.event_date_picker);
            TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time);
            TimePicker endTimePicker = (TimePicker) findViewById(R.id.end_time);
            EditText eventDuration = (EditText) findViewById(R.id.event_duration);
            EditText venue = (EditText) findViewById(R.id.venue_edit);
            TimePicker briefTimePicker = (TimePicker) findViewById(R.id.brief_time_picker);
            EditText briefLocation = (EditText) findViewById(R.id.brief_location);
            EditText teacherInCharge = (EditText) findViewById(R.id.teacher_in_charge_edit);
            EditText maxVolunteers = (EditText) findViewById(R.id.max_volunteers_text);
            cancelButton.setVisibility(View.VISIBLE);
            showRegButton.setVisibility(View.VISIBLE);

            eventTitle.setText(event.getEventName());

            int[] eventDate = Utils.splitDate(event.getEventDate());
            int year = eventDate[0];
            int month = eventDate[1];
            int day = eventDate[2];
            eventDatePicker.updateDate(year, month, day);

            //DB will store the time in 24 hrs format
            int[] startTime = Utils.splitTime(event.getStartTime());
            int hour = startTime[0];
            int minute = startTime[1];
            startTimePicker.setCurrentHour(hour);
            startTimePicker.setCurrentMinute(minute);
            Calendar startTimeCal = Calendar.getInstance();
            startTimeCal.set(year,month,day,hour,minute);

            int[] endTime = Utils.splitTime(event.getEndTime());
            hour = endTime[0];
            minute = endTime[1];
            endTimePicker.setCurrentHour(hour);
            endTimePicker.setCurrentMinute(minute);
            Calendar endTimeCal = Calendar.getInstance();
            endTimeCal.set(year, month, day, hour, minute);

            //startTime > endTime validation to be done

            double mToHourConverter = 3600000;
            double duration = ((double)(endTimeCal.getTimeInMillis()-startTimeCal.getTimeInMillis()))/mToHourConverter;
            eventDuration.setText(String.valueOf(new DecimalFormat("0.0").format(duration)));

            int[] briefTime = Utils.splitTime(event.getBriefingTime());
            hour = briefTime[0];
            minute = briefTime[1];
            briefTimePicker.setCurrentHour(hour);
            briefTimePicker.setCurrentMinute(minute);

            duty.setText(event.getEventDescription());
            //eventDuration.setText(event.get()); //to be computed here
            venue.setText(event.getVenue());
            briefLocation.setText(event.getBriefingPlace());
            teacherInCharge.setText(event.getTeacherInCharge());
            maxVolunteers.setText(String.valueOf(event.getMaxVolunteers()));

            actionButton.setText("Update");
            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event result = setEvent(event);
                    eventsAdapter.updateEvent(result, adminId);
                }
            });

            showRegButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(ctxt, EventManageParentsActivity.class);
                    intent.putExtra("event_id", event.getEventId());
                    intent.putExtra("event_name", event.getEventName());

                    startActivity(intent);
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event result = setEvent(event);
                    eventsAdapter.deleteEvent(result,adminId);

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ctxt, EventAdminHomeTabActivity.class);
                    intent.putExtra("user_id", adminId);
                    intent.putExtra("message","Event Deleted!!!");

                    startActivity(intent);
                }
            });
        }
    }

    private Event setEvent(Event newEvent) {

        EditText eventTitle = (EditText) findViewById(R.id.event_title);
        EditText duty = (EditText) findViewById(R.id.event_desc_edit);
        DatePicker eventDatePicker = (DatePicker) findViewById(R.id.event_date_picker);
        TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time);
        TimePicker endTimePicker = (TimePicker) findViewById(R.id.end_time);
        EditText venue = (EditText) findViewById(R.id.venue_edit);
        TimePicker briefTimePicker = (TimePicker) findViewById(R.id.brief_time_picker);
        EditText briefLocation = (EditText) findViewById(R.id.brief_location);
        EditText teacherInCharge = (EditText) findViewById(R.id.teacher_in_charge_edit);
        EditText maxVolunteers = (EditText) findViewById(R.id.max_volunteers_text);


        newEvent.setEventName(eventTitle.getText().toString());
        newEvent.setEventDescription(duty.getText().toString());

        String eventDate = eventDatePicker.getYear()+"/"+eventDatePicker.getMonth()+"/"+eventDatePicker.getDayOfMonth();
        newEvent.setEventDate(eventDate);

        String startTime = startTimePicker.getCurrentHour()+":"+startTimePicker.getCurrentMinute();
        newEvent.setStartTime(startTime);

        String endTime = endTimePicker.getCurrentHour()+":"+endTimePicker.getCurrentMinute();
        newEvent.setEndTime(endTime);

        newEvent.setVenue(venue.getText().toString());

        String briefTime = briefTimePicker.getCurrentHour()+":"+briefTimePicker.getCurrentMinute();
        newEvent.setBriefingTime(briefTime);

        newEvent.setBriefingPlace(briefLocation.getText().toString());
        newEvent.setTeacherInCharge(teacherInCharge.getText().toString());
        newEvent.setMaxVolunteers(Integer.parseInt(maxVolunteers.getText().toString()));

        return newEvent;
    }




}
