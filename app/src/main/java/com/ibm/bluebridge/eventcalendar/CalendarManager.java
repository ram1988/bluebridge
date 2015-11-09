package com.ibm.bluebridge.eventcalendar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;

import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;

import java.util.Calendar;

/**
 * Created by manirm on 11/9/2015.
 */
public class CalendarManager {

    private Context context;

    public CalendarManager() {

    }

    public CalendarManager(Context context) {
        this.context = context;
    }

    public void addCalendarEvent(Event event){

        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(Instances.TITLE, event.getEventName());
        intent.putExtra(Instances.DESCRIPTION, event.getEventDescription());

        int[] eventDate = Utils.splitDate(event.getEventDate());
        int year = eventDate[0];
        int month = eventDate[1];
        int day = eventDate[2];

        int[] startTime = Utils.splitTime(event.getStartTime());
        int hour = startTime[0];
        int minute = startTime[1];
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.set(year,month,day,hour,minute);

        int[] endTime = Utils.splitTime(event.getEndTime());
        hour = endTime[0];
        minute = endTime[1];
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.set(year, month, day, hour, minute);

        intent.putExtra(Instances.BEGIN, startTimeCal.getTimeInMillis());
        intent.putExtra(Instances.END, endTimeCal.getTimeInMillis());

        /* to add duration in event object1
        if(Integer.parseInt(event.getDuration()) >= 8) {
            intent.putExtra(Instances.ALL_DAY, true);
        }
*/
        if(!checkEventExistence(startTimeCal.getTimeInMillis(),endTimeCal.getTimeInMillis(),event.getEventName())) {
            context.startActivity(intent);
        }
    }

    private boolean checkEventExistence(long startTime, long endTime, String eventTitle) {
        boolean eventExists = false;

        long begin = startTime;
        long end = endTime;
                String[] proj =
                new String[]{
                        Instances._ID,
                        Instances.TITLE,
                        Instances.BEGIN,
                        Instances.END,
                        Instances.EVENT_ID};
        Cursor cursor =
                Instances.query(context.getContentResolver(), proj, begin, end);
        if (cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                if (eventTitle.equals(cursor.getString(1))) {
                    eventExists = true;
                    break;
                }
            }
        }

        return eventExists;
    }
}
