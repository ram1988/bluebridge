package com.ibm.bluebridge.eventcalendar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.bluebridge.R;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidGridAdapter;

import java.util.HashMap;

import hirondelle.date4j.DateTime;

/**
 * Created by manirm on 11/5/2015.
 */
public class EventCellAdapter extends CaldroidGridAdapter {
    private HashMap<String,Object> eventsMap;

    public EventCellAdapter(Context ctxt, int month, int year, HashMap<String, Object> caldroidData, HashMap<String, Object> extraData) {
        super(ctxt, month, year, caldroidData, extraData);
        this.eventsMap = extraData;
    }


    private HashMap<String,Object> injectEventsMap() {
        getCaldroidData().put("eventMap",caldroidData);
        return getCaldroidData();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cellView = convertView;

        // For reuse
        if (convertView == null) {
            cellView = inflater.inflate(R.layout.calendar_cell_view, null);
        }

        int topPadding = cellView.getPaddingTop();
        int leftPadding = cellView.getPaddingLeft();
        int bottomPadding = cellView.getPaddingBottom();
        int rightPadding = cellView.getPaddingRight();

        TextView dateText = (TextView) cellView.findViewById(R.id.cell_content);

        dateText.setTextColor(Color.BLACK);

        // Get dateTime of this cell
        DateTime dateTime = this.datetimeList.get(position);
        Resources resources = context.getResources();

        // Set color of the dates in previous / next month
        if (dateTime.getMonth() != month) {
            dateText.setTextColor(resources
                    .getColor(com.caldroid.R.color.caldroid_darker_gray));
        }

        /*
        boolean shouldResetDiabledView = false;
        boolean shouldResetSelectedView = false;

        // Customize for disabled dates and date outside min/max dates
        if ((minDateTime != null && dateTime.lt(minDateTime))
                || (maxDateTime != null && dateTime.gt(maxDateTime))
                || (disableDates != null && disableDates.indexOf(dateTime) != -1)) {

            dateText.setTextColor(CaldroidFragment.disabledTextColor);
            if (CaldroidFragment.disabledBackgroundDrawable == -1) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.disable_cell);
            } else {
                cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
            }

            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border_gray_bg);
            }

        } else {
            shouldResetDiabledView = true;
        }

        // Customize for selected dates
        if (selectedDates != null && selectedDates.indexOf(dateTime) != -1) {
            cellView.setBackgroundColor(resources
                    .getColor(com.caldroid.R.color.caldroid_sky_blue));

            dateText.setTextColor(Color.BLACK);

        } else {
            shouldResetSelectedView = true;
        }

        if (shouldResetDiabledView && shouldResetSelectedView) {
            // Customize for today
            if (dateTime.equals(getToday())) {
                cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
            } else {
                cellView.setBackgroundResource(com.caldroid.R.drawable.cell_bg);
            }
        }
        */

        dateText.setText("" + dateTime.getDay());
        String date = dateTime.getYear()+""+dateTime.getMonth()+""+dateTime.getDay();

        if(eventsMap.containsKey(date)) {
            cellView.setTag(eventsMap.get(date));
            //dateText.setTag(eventsMap.get(date));
            /*dateText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Event event = (Event) v.getTag();
                    Utils.showAlertDialog(event.getEventName(), context);
                }
            });*/
            cellView.setBackgroundResource(com.caldroid.R.drawable.red_border);
        }

        // Somehow after setBackgroundResource, the padding collapse.
        // This is to recover the padding
        cellView.setPadding(leftPadding, topPadding, rightPadding,
                bottomPadding);

        // Set custom color if required
        setCustomResources(dateTime, cellView, dateText);

        return cellView;
    }


}
