package com.ibm.bluebridge;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.ibm.bluebridge.eventcalendar.EventCalendarView;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.valueobject.Event;

import java.util.HashMap;
import java.util.List;

public class EventParentHomeSpinnerActivity extends EventMasterActivity {
    private static Context selfCtxt;
    private static EventsAdapter eventsAdapter ;
    private static String parent_id;
    private static FragmentManager fragmentManager;
    private static Button viewCalendarButton;
    private static SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_parent_home_spinner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        // Setup spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(new MyAdapter(
                toolbar.getContext(),
                new String[]{
                        "All Events",
                        "Registered Events",
                        "Completed Events",
                        "Statistics",
                        "About Me",
                        "Logout"
                }));

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        selfCtxt = this;
        session = SessionManager.getSessionInstance(this);
        parent_id = session.getUserId();

        fragmentManager  = getSupportFragmentManager();
        eventsAdapter = new EventsAdapter(selfCtxt);

        Intent intent = getIntent();

        viewCalendarButton = (Button) findViewById(R.id.calendar_view);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_parent_home_spinner, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_event_parent_home_spinner, container, false);

            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            //final EventsAdapter eventsAdapter = new EventsAdapter(selfCtxt);
            final ListView listView = (ListView) rootView.findViewById(R.id.listview);
            TextView noEventsMsg = (TextView)rootView.findViewById(R.id.no_events_message);
            ArrayAdapter<Event> adapter = null;

            //For all events
            if(tabNumber == 1 ) {
                final List<Event> eventList = eventsAdapter.getAllEventsList(parent_id);

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);
                    adapter = getEventArrayAdapter(selfCtxt, eventList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            final Event item = (Event) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                            intent.putExtra("EventAction", 2);
                            intent.putExtra("EventObj", item);
                            intent.putExtra("parent_id", parent_id);
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
                final List<Event> eventList = eventsAdapter.getAllJoinedEventsList(parent_id);

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    adapter = getEventArrayAdapter(selfCtxt, eventList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            final Event item = (Event) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                            intent.putExtra("EventAction", 2);
                            intent.putExtra("EventObj", item);
                            intent.putExtra("parent_id", parent_id);
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
            //For attended events
            else if(tabNumber == 3) {
                final List<Event> eventList = eventsAdapter.getAllAttendedEventsList(parent_id);

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    adapter = getEventArrayAdapter(selfCtxt, eventList);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            final Event item = (Event) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                            intent.putExtra("EventAction", 2);
                            intent.putExtra("EventObj", item);
                            intent.putExtra("parent_id", parent_id);
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

            return rootView;
        }
    }

    private static void showCalendarBox(List<Event> eventList) {
        EventCalendarView caldroidFragment = new EventCalendarView(selfCtxt, parent_id, eventList, EventCalendarView.UserType.PARENT);
        caldroidFragment.show(fragmentManager, "Tag");
    }
}
