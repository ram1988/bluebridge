package com.ibm.bluebridge;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.eventcalendar.CalendarManager;
import com.ibm.bluebridge.util.RESTApi;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.util.Validator;
import com.ibm.bluebridge.valueobject.Event;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EventFormViewActivity extends EventMasterActivity {

    private Context ctxt;
    private String adminId;
    private String parentId;
    SessionManager session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionManager.getSessionInstance(this);
        Intent intent = getIntent();
        int mode = intent.getIntExtra("EventAction", 2);
        if (mode == 0 || mode == 1) {
            //admin:
            // 0: add event
            // 1: edit event
            setContentView(R.layout.activity_event_form_view);
        } else if (mode == 2) {
            //admin:
            //  view event, allow to view show registered parents
            //parent:
            // list events, allow join and unjoin new event
            // list registered events, allow unjoin registered event
            // list attended events, only view event details, no action
            setContentView(R.layout.activity_event_detail_view);
        }

        /*
         * event_id is passed and the description ie fetched from api
         * and displayed here
         *
         */
        ctxt = this;

        if(session.isAdmin())
            adminId = session.getUserId();
        else
            parentId = session.getUserId();

        Event currentEvent;
        String eventId = intent.getStringExtra("event_id");
        if(eventId != null) {
            EventsAdapter eventUtil = new EventsAdapter();
            currentEvent = eventUtil.getEvent(eventId);
        }else{
            currentEvent = (Event)intent.getSerializableExtra("EventObj");
        }
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
        TimePicker startTimePicker = null;
        TimePicker endTimePicker = null;
        EditText eventDuration = null;

        if (mode == 0 || mode == 1) {
            startTimePicker = (TimePicker) findViewById(R.id.start_time);
            endTimePicker = (TimePicker) findViewById(R.id.end_time);
            eventDuration = (EditText) findViewById(R.id.event_duration);
            eventDuration.setKeyListener(null);
            startTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                public void onTimeChanged(TimePicker view, int hour, int minute) {
                    DatePicker eventDatePicker = (DatePicker) findViewById(R.id.event_date_picker);
                    TimePicker endTimePicker = (TimePicker) findViewById(R.id.end_time);
                    EditText eventDuration = (EditText) findViewById(R.id.event_duration);
                    int year = eventDatePicker.getYear();
                    int month = eventDatePicker.getMonth() + 1;
                    int day = eventDatePicker.getDayOfMonth();
                    int endHour = endTimePicker.getCurrentHour();
                    int endMinute = endTimePicker.getCurrentMinute();

                    Calendar startTimeCal = Calendar.getInstance();
                    startTimeCal.set(year, month, day, hour, minute);
                    long startTimeMillis = startTimeCal.getTimeInMillis();

                    Calendar endTimeCal = Calendar.getInstance();
                    endTimeCal.set(year, month, day, endHour, endMinute);
                    long endTimeMillis = endTimeCal.getTimeInMillis();

                    double mToHourConverter = 3600000;
                    if (endTimeMillis > startTimeMillis) {
                        double duration = ((double) (endTimeMillis - startTimeMillis)) / mToHourConverter;
                        eventDuration.setText(String.valueOf(new DecimalFormat("0.0").format(duration)));
                    }
                }
            });

            endTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

                public void onTimeChanged(TimePicker view, int hour, int minute) {
                    DatePicker eventDatePicker = (DatePicker) findViewById(R.id.event_date_picker);
                    TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time);
                    EditText eventDuration = (EditText) findViewById(R.id.event_duration);
                    int year = eventDatePicker.getYear();
                    int month = eventDatePicker.getMonth() + 1;
                    int day = eventDatePicker.getDayOfMonth();
                    int startHour = startTimePicker.getCurrentHour();
                    int startMinute = startTimePicker.getCurrentMinute();

                    Calendar startTimeCal = Calendar.getInstance();
                    startTimeCal.set(year, month, day, startHour, startMinute);
                    long startTimeMillis = startTimeCal.getTimeInMillis();

                    Calendar endTimeCal = Calendar.getInstance();
                    endTimeCal.set(year, month, day, hour, minute);
                    long endTimeMillis = endTimeCal.getTimeInMillis();

                    double mToHourConverter = 3600000;
                    if (endTimeMillis > startTimeMillis) {
                        double duration = ((double) (endTimeMillis - startTimeMillis)) / mToHourConverter;
                        eventDuration.setText(String.valueOf(new DecimalFormat("0.0").format(duration)));
                    }
                }
            });
        }

        if(mode == 0) { //admin add event

            actionButton.setText("Add");
            cancelButton.setVisibility(View.INVISIBLE);
            showRegButton.setVisibility(View.INVISIBLE);


            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event newEvent = new Event();
                    newEvent =  setEvent(newEvent);

                    String resCode = Validator.validate(newEvent);
                    if (resCode.equals("")) {
                        eventsAdapter.addEvent(newEvent,adminId);
                    } else {
                        Utils.showAlertDialog(resCode, ctxt);
                        return;
                    }

                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    Intent intent = new Intent(ctxt, EventAdminHomeSpinnerCateActivity.class);
                    intent.putExtra("user_id", adminId);
                    intent.putExtra("message","Event Added!!!");

                    startActivity(intent);
                    finish();

                }
            });
        }
        else if(mode == 1) { //admin edit events
            EditText eventTitle = (EditText) findViewById(R.id.event_title);
            EditText duty = (EditText) findViewById(R.id.event_desc_edit);
            DatePicker eventDatePicker = (DatePicker) findViewById(R.id.event_date_picker);

            EditText venue = (EditText) findViewById(R.id.venue_edit);
            TimePicker briefTimePicker = (TimePicker) findViewById(R.id.brief_time_picker);
            EditText briefLocation = (EditText) findViewById(R.id.brief_location);
            EditText teacherInCharge = (EditText) findViewById(R.id.teacher_in_charge_edit);
            EditText maxVolunteers = (EditText) findViewById(R.id.max_volunteers_text);
            Spinner categoryVal = (Spinner)findViewById(R.id.category_value);
            cancelButton.setVisibility(View.VISIBLE);
            showRegButton.setVisibility(View.VISIBLE);

            eventTitle.setText(event.getEventName());


            int[] eventDate = Utils.splitDate(event.getEventDate());
            int year = eventDate[0];
            int month = eventDate[1];
            int day = eventDate[2];
            eventDatePicker.updateDate(year, month-1, day);

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
            venue.setText(event.getVenue());
            briefLocation.setText(event.getBriefingPlace());
            teacherInCharge.setText(event.getTeacherInCharge());
            maxVolunteers.setText(String.valueOf(event.getMaxVolunteers()));

            List<String> categoryList = Arrays.asList(getResources().getStringArray(R.array.category_values));
            categoryVal.setSelection(categoryList.indexOf(event.getCategory()));

            actionButton.setText("Update");
            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event oldEvent = event;
                    Event newEvent = setEvent(new Event());
                    newEvent.setEventId(event.getEventId());
                    String resCode = Validator.validate(newEvent);
                    if (resCode.equals("")) {
                        eventsAdapter.updateEvent(newEvent, adminId);
                        CalendarManager calendarManager = new CalendarManager(ctxt);
                        calendarManager.updateCalendarEvent(oldEvent,newEvent);
                        Utils.showAlertDialog("Event Updated!!!", ctxt);

                    } else {
                        System.out.println("error");
                        Utils.showAlertDialog(resCode, ctxt);
                        return;
                    }
                }
            });

            showRegButton.setText("Show Registered Volunteers");
            showRegButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(ctxt, EventManageParentsActivity.class);
                    intent.putExtra("event_id", event.getEventId());
                    intent.putExtra("event_name", event.getEventName());

                    startActivity(intent);
                    finish();
                }
            });

            cancelButton.setText("Delete");
            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event result = setEvent(event);
                    eventsAdapter.deleteEvent(result, adminId);

                    CalendarManager calendarManager = new CalendarManager(ctxt);
                    calendarManager.deleteCalendarEvent(result);


                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }



                    Intent intent = new Intent(ctxt, EventAdminHomeSpinnerCateActivity.class);
                    intent.putExtra("user_id", adminId);
                    intent.putExtra("message","Event Deleted!!!");

                    startActivity(intent);
                    finish();
                }
            });
        }
        else if(mode == 2) { //admin view event, parent view event, registered events, completed events
            TextView eventTitle = (TextView) findViewById(R.id.event_title);
            TextView duty = (TextView) findViewById(R.id.event_desc);
            TextView eventDate = (TextView) findViewById(R.id.event_date);
            TextView startTime = (TextView) findViewById(R.id.start_time);
            TextView endTime = (TextView) findViewById(R.id.end_time);
            TextView eventDurationView = (TextView) findViewById(R.id.event_duration);
            TextView venue = (TextView) findViewById(R.id.venue_edit);
            TextView briefTime = (TextView) findViewById(R.id.brief_time);
            TextView briefLocation = (TextView) findViewById(R.id.brief_location);
            TextView teacherInCharge = (TextView) findViewById(R.id.teacher_in_charge_edit);
            TextView maxVolunteers = (TextView) findViewById(R.id.max_volunteers_text);
            TextView category = (TextView) findViewById(R.id.category_text);

            actionButton.setVisibility(View.INVISIBLE);
            showRegButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            if (parentId != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date event_date = new Date(event.getEventDate());
                if (new Date().before(event_date)) {
                    if (event.getRegistered() && !event.getAttended()) {
                        cancelButton.setVisibility(View.VISIBLE);
                    }
                    if (!event.getRegistered() && !event.getAttended()) {
                        actionButton.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                showRegButton.setVisibility(View.VISIBLE);
            }

            eventTitle.setText(event.getEventName());
            eventDate.setText(event.getEventDate());
            startTime.setText(event.getStartTime());
            endTime.setText(event.getEndTime());
            eventDurationView.setText(event.getDuration());
            briefTime.setText(event.getBriefingTime());

            duty.setText(event.getEventDescription());
            //venue.setText(event.getVenue());
            briefLocation.setText(event.getBriefingPlace());
            teacherInCharge.setText(event.getTeacherInCharge());
            maxVolunteers.setText(String.valueOf(event.getMaxVolunteers()));
            category.setText(event.getCategory());

            venue.setText(Html.fromHtml(
                            "<a href=\"https://www.google.com.sg/maps/place/" + event.getVenue() + "\">" +
                            event.getVenue() +
                            "</a>"));
            venue.setMovementMethod(LinkMovementMethod.getInstance());

            actionButton.setText("Join");
            actionButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventsAdapter.joinEvent(event, parentId);
                    Utils.showAlertDialog("You have joined this event!", ctxt);
                    Intent intent = new Intent(ctxt, EventParentHomeSpinnerActivity.class);
                    intent.putExtra("user_id", parentId);
                    startActivity(intent);
                    finish();
                }
            });

            cancelButton.setText("unJoin");
            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    eventsAdapter.unjoinEvent(event, parentId);
                    CalendarManager calendarManager = new CalendarManager(ctxt);
                    calendarManager.deleteCalendarEvent(event);
                    Utils.showAlertDialog("You have unjoined this event!", ctxt);
                    Intent intent = new Intent(ctxt, EventParentHomeSpinnerActivity.class);
                    intent.putExtra("user_id", parentId);
                    startActivity(intent);
                    finish();
                }
            });

            showRegButton.setText("Show Registered Parents");
            showRegButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(ctxt, EventManageParentsActivity.class);
                    intent.putExtra("event_id", event.getEventId());
                    intent.putExtra("event_name", event.getEventName());

                    startActivity(intent);
                    finish();
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
        Spinner categoryVal = (Spinner)findViewById(R.id.category_value);

        newEvent.setEventName(eventTitle.getText().toString());
        newEvent.setEventDescription(duty.getText().toString());

        String eventDate = eventDatePicker.getYear()+"/"+(eventDatePicker.getMonth()+1)+"/"+eventDatePicker.getDayOfMonth();
        System.out.print("Selected month-----)))"+eventDatePicker.getMonth());
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
        newEvent.setCategory(categoryVal.getSelectedItem().toString());

        return newEvent;
    }
}
