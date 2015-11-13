package com.ibm.bluebridge;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.adapter.StatisticsAdapter;
import com.ibm.bluebridge.eventcalendar.CalendarManager;
import com.ibm.bluebridge.eventcalendar.EventCalendarView;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.ChartItem;
import com.ibm.bluebridge.valueobject.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventAdminHomeSpinnerActivity extends EventMasterActivity {

    private static Context selfCtxt;
    private static Activity selfActivity;
    private static String admin_id;
    private static Map<Integer,ArrayAdapter<Event>> arrayAdapterMap;
    private static EventsAdapter eventsAdapter ;
    private static FragmentManager fragmentManager;
    private static Button viewCalendarButton;

    private static SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_admin_home_spinner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);


        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "Managed Events",
                        "Completed Events",
                        "Statistics",
                        "About Me",
                        "Logout",
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 4) {  //Last item is logout
                    session.logout();
                    return;
                }

                // When the given dropdown item is selected, show its contents in the
                // container view.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        session = SessionManager.getSessionInstance(this);
        admin_id = session.getUserId();

        selfCtxt = selfActivity = this;
        fragmentManager  = getSupportFragmentManager();
        final Activity localRef = this;
        eventsAdapter = new EventsAdapter(selfCtxt);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");



        if(message!=null && !message.equals("") ) {
            Utils.showAlertDialog(message, this);
            intent.removeExtra("message");
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_fab);
        viewCalendarButton = (Button) findViewById(R.id.calendar_view);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {

            private int count = 0;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                intent.putExtra("EventAction", 0);
                intent.putExtra("admin_id", admin_id);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_admin_home_spinner, menu);
        return true;
    }

    private static class MyAdapter extends ArrayAdapter<String> implements ThemedSpinnerAdapter {
        private final ThemedSpinnerAdapter.Helper mDropDownHelper;

        public MyAdapter(Context context, String[] objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mDropDownHelper = new ThemedSpinnerAdapter.Helper(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                // Inflate the drop down using the helper's LayoutInflater
                LayoutInflater inflater = mDropDownHelper.getDropDownViewInflater();
                view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            } else {
                view = convertView;
            }

            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getItem(position));

            return view;
        }

        @Override
        public void setDropDownViewTheme(Theme theme) {
            mDropDownHelper.setDropDownViewTheme(theme);
        }

        @Override
        public Theme getDropDownViewTheme() {
            return mDropDownHelper.getDropDownViewTheme();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_event_admin_home_tab, container, false);

            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            final ListView listView = (ListView) rootView.findViewById(R.id.listview);
            TextView noEventsMsg = (TextView)rootView.findViewById(R.id.no_events_message);


            //For all events
            if(tabNumber == 1 ) {
                Log.d("EventAdminHomeSpinner", "Tab1 clicked");
                final List<Event> eventList = eventsAdapter.getAdminEventsList(admin_id);
                CalendarManager calendarManager = new CalendarManager(selfCtxt);

                for(Event event:eventList) {
                    calendarManager.addCalendarEvent(event);
                }

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);

                    final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt, eventList);
                    //arrayAdapterMap.put(tabNumber-1,adapter);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            final Event item = (Event) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                            intent.putExtra("EventAction", 1);
                            intent.putExtra("EventObj", item);
                            intent.putExtra("admin_id", admin_id);

                            startActivity(intent);
                        }
                    });

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(eventList);
                        }
                    });
                }
            }
            //For joined events
            else if(tabNumber == 2) {
                Log.d("EventAdminHomeSpinner", "Tab2 clicked");
                final List<Event> completedEventsList = eventsAdapter.getAdminCompletedEventsList(admin_id);

                final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt, completedEventsList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final Event item = (Event) parent.getItemAtPosition(position);

                        //to be in read mode
                        Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                        intent.putExtra("EventAction", 2);
                        intent.putExtra("EventObj", item);
                        intent.putExtra("admin_id", admin_id);

                        startActivity(intent);
                    }
                });

                viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showCalendarBox(completedEventsList);
                    }
                });

            } else if (tabNumber == 3){
                //For Statistics
                List<ChartItem> charts = new ArrayList<ChartItem>();

                ChartItem chart1 = new ChartItem();
                chart1.setCategory("Parents' Finished Hours");
                ChartItem chart2 = new ChartItem();
                chart2.setCategory("Number of Parents by Child Registration Year");
                ChartItem chart3 = new ChartItem();
                chart3.setCategory("Percentage of Finished by child registration year");
                ChartItem chart4 = new ChartItem();
                chart4.setCategory("Number of Registration by Event Category");

                charts.add(chart1);
                charts.add(chart2);
                charts.add(chart3);
                charts.add(chart4);

                StatisticsAdapter adapter = new StatisticsAdapter(selfCtxt, charts);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        Intent chart = new Intent();
                        chart.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.charts.LineActivity");
                        startActivity(chart);
                    }
                });

                viewCalendarButton.setVisibility(View.INVISIBLE);
            } else if (tabNumber == 4){
                //For About Me
            }

            return rootView;
        }
    }

    private static void showCalendarBox(List<Event> eventList) {
        EventCalendarView caldroidFragment = new EventCalendarView(selfCtxt, admin_id, eventList, EventCalendarView.UserType.ADMIN);
        caldroidFragment.show(fragmentManager, "Tag");
    }
}
