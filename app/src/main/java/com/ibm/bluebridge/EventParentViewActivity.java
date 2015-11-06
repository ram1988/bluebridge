package com.ibm.bluebridge;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
import android.widget.ListView;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.eventcalendar.EventCalendarView;
import com.ibm.bluebridge.valueobject.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventParentViewActivity extends EventMasterActivity implements ActionBar.TabListener {

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
    private ViewPager mViewPager;
    private static Context selfCtxt;
    private static String parent_id;
    private static FragmentManager fragmentManager;
    private static Button viewCalendarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selfCtxt = this;
        setContentView(R.layout.activity_event_parent_view);

        fragmentManager  = getSupportFragmentManager();
        Intent intent = getIntent();
        parent_id = intent.getStringExtra("user_id");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_parent_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

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
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All \nEvents";
                case 1:
                    return "Registered \nEvents";
                case 2:
                    return "Completed \nEvents";
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
            View rootView = inflater.inflate(R.layout.fragment_event_parent_view, container, false);
            viewCalendarButton = (Button) rootView.findViewById(R.id.calendar_view);

            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            final EventsAdapter eventsAdapter = new EventsAdapter(selfCtxt);
            final ListView listView = (ListView) rootView.findViewById(R.id.listview);

            //For all events
            if(tabNumber == 1 ) {
                final List<Event> eventList = eventsAdapter.getAllEventsList(parent_id);
                final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt,eventList);
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
            //For joined events
            else if(tabNumber == 2) {
                final List<Event> eventList = eventsAdapter.getAllJoinedEventsList(parent_id);
                final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt,eventList);
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
            //For attended events
            else if(tabNumber == 3) {
                final List<Event> eventList = eventsAdapter.getAllAttendedEventsList(parent_id);
                final ArrayAdapter<Event> adapter = getEventArrayAdapter(selfCtxt,eventList);
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

            return rootView;
        }
    }

    private static void showCalendarBox(List<Event> eventList) {
        EventCalendarView caldroidFragment = new EventCalendarView(selfCtxt, parent_id, eventList, EventCalendarView.UserType.PARENT);
        caldroidFragment.show(fragmentManager, "Tag");
    }
}
