package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.util.CONSTANTS;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

import org.w3c.dom.Text;

import java.util.List;

public class EventManageParentsActivity extends EventMasterActivity {
    private static Context selfCtxt;
    private static EventsAdapter eventsAdapter ;
    private static String event_id;
    private static String event_name;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_manage_parents);

        selfCtxt = this;
        eventsAdapter = new EventsAdapter(selfCtxt);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent intent = getIntent();
        event_id = intent.getStringExtra("event_id");
        event_name = intent.getStringExtra("event_name");

        this.setTitle("For the Event "+ event_name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manage_parents, menu);
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
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Registered Parents";
                case 1:
                    return "Mark Attendance";
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
            View rootView = inflater.inflate(R.layout.fragment_event_manage_parents, container, false);
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            TextView noRegMsg = (TextView)rootView.findViewById(R.id.no_registration);
            List<Parent> parentList = eventsAdapter.getRegisteredParentsList(event_id);

            if(tabNumber == 1 ) {
                System.out.println("Tab1 clicked");

                if(parentList.isEmpty()){
                    noRegMsg.setVisibility(View.VISIBLE);
                } else {
                    noRegMsg.setVisibility(View.INVISIBLE);
                    ListView listView = (ListView)rootView.findViewById(R.id.listview);

                   // Utils.LoaderDialog loadingDialog = new Utils.LoaderDialog();
                    ArrayAdapter<Parent> mAdapter = getParentListItemAdapter(selfCtxt, parentList, ParentModes.PARENT_REGISTERED, event_id);
                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            Parent item = (Parent) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventParentDetailActivity.class);
                            intent.putExtra("ParentObj", item);

                            startActivity(intent);
                        }
                    });
                   // loadingDialog.closeDialog();
                }
            }
            else if(tabNumber == 2){
                System.out.println("Tab2 clicked");

                if(parentList.isEmpty()){
                    noRegMsg.setVisibility(View.VISIBLE);
                } else {
                    noRegMsg.setVisibility(View.INVISIBLE);
                    ListView listView = (ListView)rootView.findViewById(R.id.listview);

                   // Utils.LoaderDialog loadingDialog = new Utils.LoaderDialog();
                    ArrayAdapter<Parent> mAdapter = getParentListItemAdapter(selfCtxt, parentList, ParentModes.PARENT_ATTENDED, event_id);
                    listView.setAdapter(mAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, final View view,
                                                int position, long id) {
                            Parent item = (Parent) parent.getItemAtPosition(position);

                            Intent intent = new Intent(selfCtxt, EventParentDetailActivity.class);
                            intent.putExtra("ParentObj", item);

                            startActivity(intent);
                        }
                    });
                   // loadingDialog.closeDialog();
                }
            }


            return rootView;
        }
    }
}
