package com.ibm.bluebridge.eventcalendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;

import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;

import java.util.Calendar;
import java.util.TimeZone;

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

    public void addCalendarEvent(Event event) {

        Calendar cal = Calendar.getInstance();
        ContentResolver cr = context.getContentResolver();

        ContentValues values = prepareContentVal(event);

        System.out.println("Content vales-->" + values);
        long eventId = checkEventExistence(values.getAsLong(CalendarContract.Events.DTSTART),values.getAsLong(CalendarContract.Events.DTEND),event.getEventName());

        if(eventId == -1) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            System.out.println("Content URI-->" + uri);
        } else {
            System.out.println("Already added-->"+eventId );
        }
    }

    public void updateCalendarEvent(Event oldEvent, Event newEvent) {
        int[] eventDate = Utils.splitDate(oldEvent.getEventDate());
        int year = eventDate[0];
        int month = eventDate[1]-1;
        int day = eventDate[2];

        int[] startTime = Utils.splitTime(oldEvent.getStartTime());
        int hour = startTime[0];
        int minute = startTime[1];
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.set(year, month, day, hour, minute);

        int[] endTime = Utils.splitTime(oldEvent.getEndTime());
        hour = endTime[0];
        minute = endTime[1];
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.set(year, month, day, hour, minute);

        long eventId = checkEventExistence(startTimeCal.getTimeInMillis(),endTimeCal.getTimeInMillis(),oldEvent.getEventName());

        if(eventId!=-1) {
            ContentValues values = prepareContentVal(newEvent);
            int rows = context.getContentResolver().update(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,eventId), values, null ,null);
            System.out.println("Content URI-->" + ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,eventId));
            System.out.println("Event updated in calendar-->"+rows);
        }
    }

    public void deleteCalendarEvent(Event event) {
        int[] eventDate = Utils.splitDate(event.getEventDate());
        int year = eventDate[0];
        int month = eventDate[1]-1;
        int day = eventDate[2];

        int[] startTime = Utils.splitTime(event.getStartTime());
        int hour = startTime[0];
        int minute = startTime[1];
        Calendar startTimeCal = Calendar.getInstance();
        startTimeCal.set(year, month, day, hour, minute);

        int[] endTime = Utils.splitTime(event.getEndTime());
        hour = endTime[0];
        minute = endTime[1];
        Calendar endTimeCal = Calendar.getInstance();
        endTimeCal.set(year, month, day, hour, minute);

        long eventId = checkEventExistence(startTimeCal.getTimeInMillis(),endTimeCal.getTimeInMillis(),event.getEventName());

        if(eventId!=-1) {
            int rows = context.getContentResolver().delete(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,eventId), null,null);
            System.out.println("Content URI-->" + ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI,eventId));
            System.out.println("Event deleted from calendar-->"+rows);
        }
    }

    private long checkEventExistence(long startTime, long endTime, String eventTitle) {
        long eventExists = -1;

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
                System.out.print("Events-->"+cursor.getString(1));
                if (eventTitle.equals(cursor.getString(1))) {
                    eventExists = cursor.getLong(4);
                    break;
                }
            }
        }

        return eventExists;
    }

    private ContentValues prepareContentVal(Event event) {
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.TITLE, event.getEventName());
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.DESCRIPTION, event.getEventDescription());
        values.put(CalendarContract.Events.EVENT_LOCATION, event.getVenue());


        int[] eventDate = Utils.splitDate(event.getEventDate());
        int year = eventDate[0];
        int month = eventDate[1]-1;
        int day = eventDate[2];

        int[] startTime = Utils.splitTime(event.getStartTime());
        int hour = startTime[0];
        int minute = startTime[1];
        values.put(CalendarContract.Events.DTSTART, Utils.getDateInMillis(year, month, day, hour, minute));

        int[] endTime = Utils.splitTime(event.getEndTime());
        hour = endTime[0];
        minute = endTime[1];
        values.put(CalendarContract.Events.DTEND, Utils.getDateInMillis(year, month, day, hour, minute));

        TimeZone tz = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        values.put(CalendarContract.Events.STATUS, 1);

        return values;
    }

}
