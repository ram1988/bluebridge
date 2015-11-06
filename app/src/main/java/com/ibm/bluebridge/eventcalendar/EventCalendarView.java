package com.ibm.bluebridge.eventcalendar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RelativeLayout;

import com.ibm.bluebridge.EventFormViewActivity;
import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by manirm on 11/5/2015.
 */
public class EventCalendarView extends CaldroidFragment {

    private Context ctxt;
    private List<Event> eventList;
    private String keyId;
    private UserType userType;

    public EventCalendarView() {

    }

    public EventCalendarView(Context ctxt,String id, List<Event> eventList, UserType type) {
        this.ctxt = ctxt;
        this.eventList = eventList;
        this.keyId = id;
        this.userType = type;
        this.setCaldroidListener(new EventCalendarListener());

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, "Event Calendar");
        setArguments(args);
    }

    private HashMap<String,Object> prepareEventMap() {

        HashMap<String,Object> eventsMap = new HashMap<>();

        for(Event event:eventList) {
            int[] eventDate = Utils.splitDate(event.getEventDate());
            String dateKey = eventDate[0]+""+eventDate[1]+""+eventDate[2];
            eventsMap.put(dateKey,event);
        }

        return eventsMap;
    }

    @Override
    public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
        return new EventCellAdapter(ctxt, month, year,
                getCaldroidData(), prepareEventMap());
    }

    private class EventCalendarListener extends CaldroidListener {

        @Override
        public void onSelectDate(Date date, View view) {
            Object obj =  view.getTag();
            if(obj!=null) {
                Event event = (Event)obj;
                Utils.showAlertDialog(event.getEventName(), ctxt);
            }
        }


        @Override
        public void onLongClickDate(Date date, View view) {
            Object obj =  view.getTag();
            if(obj!=null) {
                //to implement mode based activity
                Event event = (Event)obj;
                Intent intent = intent = new Intent(ctxt, EventFormViewActivity.class);;
                if(userType == UserType.ADMIN) {
                    intent.putExtra("EventAction", 1);
                    intent.putExtra("admin_id", keyId);
                } else {
                    intent.putExtra("EventAction", 2);
                    intent.putExtra("parent_id", keyId);
                }
                intent.putExtra("EventObj", event);


                startActivity(intent);
            }
        }
    }

    public enum UserType {
        ADMIN,PARENT
    }
}
