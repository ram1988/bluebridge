package com.ibm.bluebridge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.eventcalendar.EventCalendarView;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;
import com.roomorama.caldroid.CaldroidFragment;


import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventAdminHomeTabActivity extends EventMasterActivity {

    private static Context selfCtxt;
    private static Activity selfActivity;
    private static String admin_id;
    private static Map<Integer,ArrayAdapter<Event>> arrayAdapterMap;
    private static EventsAdapter eventsAdapter ;
    private static FragmentManager fragmentManager;
    private static Button viewCalendarButton;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private  ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_admin_home_tab);

        selfCtxt = selfActivity = this;
        fragmentManager  = getSupportFragmentManager();
        final Activity localRef = this;
        eventsAdapter = new EventsAdapter(selfCtxt);

        Intent intent = getIntent();
        admin_id = intent.getStringExtra("user_id");
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


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        arrayAdapterMap = new HashMap<Integer,ArrayAdapter<Event>>();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                List<Event> eventList = null;
                System.out.println("Position clicked-->" + position);
                if (position == 0) {
                    eventList = eventsAdapter.getAdminEventsList(admin_id);
                    final List<Event> finalEventList = eventList;
                    fab.setVisibility(View.VISIBLE);

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(finalEventList);
                        }
                    });
                } else if (position == 1) {
                    eventList = eventsAdapter.getAdminCompletedEventsList(admin_id);
                    final List<Event> finalCompletedEventList = eventList;
                    fab.setVisibility(View.INVISIBLE);

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(finalCompletedEventList);
                        }
                    });
                }

                ArrayAdapter<Event> eventArrayAdapter = arrayAdapterMap.get(position);
                eventArrayAdapter.clear();
                eventArrayAdapter.addAll(eventList);
                eventArrayAdapter.notifyDataSetChanged();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_admin_home_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Managed Events";
                case 1:
                    return "Completed Events";
            }
            return null;
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
                System.out.println("Tab1 clicked");
                final List<Event> eventList = eventsAdapter.getAdminEventsList(admin_id);

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);

                    final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt, eventList);
                    arrayAdapterMap.put(tabNumber-1,adapter);
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

                }

                viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showCalendarBox(eventList);
                    }
                });
            }
            //For joined events
            else if(tabNumber == 2) {
                System.out.println("Tab2 clicked");
                final List<Event> completedEventsList = eventsAdapter.getAdminCompletedEventsList(admin_id);

                final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt, completedEventsList);
                arrayAdapterMap.put(tabNumber-1,adapter);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final Event item = (Event) parent.getItemAtPosition(position);

                        //to be in read mode
                        Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                        intent.putExtra("EventAction", 1);
                        intent.putExtra("EventObj", item);
                        intent.putExtra("admin_id", admin_id);

                        startActivity(intent);
                    }
                });

            }

            return rootView;
        }
    }

    private static void showCalendarBox(List<Event> eventList) {
        EventCalendarView caldroidFragment = new EventCalendarView(selfCtxt, admin_id, eventList);
        caldroidFragment.show(fragmentManager, "Tag");
    }
}
