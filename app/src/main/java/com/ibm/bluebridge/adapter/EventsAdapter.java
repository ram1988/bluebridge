package com.ibm.bluebridge.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manirm on 10/10/2015.
 */
public class EventsAdapter {

    private List<String> eventsList;

    public EventsAdapter() {
        this.eventsList = new ArrayList<String>();
    }

    public List<String> getAdminEventsList() {
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        for (int i = 0; i < values.length; ++i) {
            eventsList.add(values[i]);
        }

        return eventsList;
    }

    public void addEvent(String event) {
        eventsList.add(event);
    }
}
