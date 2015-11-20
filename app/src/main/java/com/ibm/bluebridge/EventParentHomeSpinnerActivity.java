package com.ibm.bluebridge;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.content.Context;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.content.res.Resources.Theme;

import android.widget.TextView;
import android.widget.Toast;

import com.ibm.bluebridge.adapter.EventsAdapter;
import com.ibm.bluebridge.charts.HorizontalBarChartActivity;
import com.ibm.bluebridge.charts.LineActivity;
import com.ibm.bluebridge.charts.PieChartActivity;
import com.ibm.bluebridge.eventcalendar.CalendarManager;
import com.ibm.bluebridge.adapter.StatisticsAdapter;
import com.ibm.bluebridge.eventcalendar.EventCalendarView;
import com.ibm.bluebridge.util.RESTApi;
import com.ibm.bluebridge.util.SessionManager;
import com.ibm.bluebridge.util.Utils;
import com.ibm.bluebridge.valueobject.ChartItem;
import com.ibm.bluebridge.valueobject.Children;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventParentHomeSpinnerActivity extends EventMasterActivity {
    private static Context selfCtxt;
    private static EventsAdapter eventsAdapter ;
    private static String parent_id;
    private static FragmentManager fragmentManager;
    private static Button viewCalendarButton;
    private static SessionManager session;
    private Parent parent;
    BroadcastReceiver logoutReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_parent_home_spinner);

        /** Asked to logout **/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.ibm.bluebridge.ACTION_LOGOUT");
        logoutReceiver = new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context context, Intent intent) {
                                    Log.d("onReceive", "Logout in progress");
                                    //At this point you should start the login activity and finish this one
                                    finish();
                                }
                            };
        registerReceiver(logoutReceiver, intentFilter);
        //** Asked to logout **//

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

                if (position == 5) {  //Last item is logout
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

        selfCtxt = this;
        session = SessionManager.getSessionInstance(this);
        parent_id = session.getUserId();

        System.out.println("Parent-->"+parent_id);

        fragmentManager  = getSupportFragmentManager();
        eventsAdapter = new EventsAdapter(selfCtxt);
        parent = eventsAdapter.getParentDetail(parent_id);

        Intent intent = getIntent();
        boolean alert = intent.getBooleanExtra("alert", false);
        String message = intent.getStringExtra("message");

        if(alert)
            Utils.showAlertDialog(message, this);

        viewCalendarButton = (Button) findViewById(R.id.calendar_view);

    }

    @Override
    protected void onStop(){
        unregisterReceiver(logoutReceiver);
        super.onStop();
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
        private ProgressDialog pDialog;
        private Bitmap bitmap;
        private ImageView photo;
        private RESTApi REST_API;

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
            LinearLayout layout=(LinearLayout) rootView.findViewById(R.id.parent_list_layout);
            final ListView listView = (ListView) rootView.findViewById(R.id.listview);
            TextView noEventsMsg = (TextView)rootView.findViewById(R.id.no_events_message);
            View aboutmeView = inflater.inflate(R.layout.content_aboutme_parent, container, false);
            ScrollView parentDetailView = (ScrollView)aboutmeView.findViewById(R.id.parent_details);
            ArrayAdapter<Event> adapter = null;
            int tabNumber = getArguments().getInt(ARG_SECTION_NUMBER);


            AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    Object obj = parent.getItemAtPosition(position);

                    if(obj instanceof Event) {
                        final Event item = (Event) obj;

                        Intent intent = new Intent(selfCtxt, EventFormViewActivity.class);
                        intent.putExtra("EventAction", 2);
                        intent.putExtra("EventObj", item);
                        intent.putExtra("parent_id", parent_id);
                        startActivity(intent);
                    }
                }
            };
            listView.setOnItemClickListener(listItemListener);


            //For all events
            if(tabNumber == 1 ) {
                final List<Event> eventList = eventsAdapter.getAllEventsList(parent_id);

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);

                    Map<String,List<Event>> categorizedEventMap = eventsAdapter.categorizeEvents(eventList);
                    displayCategorizedListView(categorizedEventMap, selfCtxt, listView);

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(eventList);
                        }
                    });
                }
            }
            //For joined events
            else if(tabNumber == 2) {

                viewCalendarButton.setVisibility(View.VISIBLE);
                parentDetailView.setVisibility(View.INVISIBLE);
                final List<Event> eventList = eventsAdapter.getAllJoinedEventsList(parent_id);

                CalendarManager calendarManager = new CalendarManager(selfCtxt);

                for(Event event:eventList) {
                    calendarManager.addCalendarEvent(event);
                }


                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);
                    Map<String,List<Event>> categorizedEventMap = eventsAdapter.categorizeEvents(eventList);
                    displayCategorizedListView(categorizedEventMap, selfCtxt, listView);

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(eventList);
                        }
                    });
                }
            }
            //For attended events
            else if(tabNumber == 3) {
                viewCalendarButton.setVisibility(View.VISIBLE);
                parentDetailView.setVisibility(View.INVISIBLE);
                final List<Event> eventList = eventsAdapter.getAllAttendedEventsList(parent_id);

                CalendarManager calendarManager = new CalendarManager(selfCtxt);

                for(Event event:eventList) {
                    calendarManager.addCalendarEvent(event);
                }

                if(eventList.isEmpty()){
                    noEventsMsg.setVisibility(View.VISIBLE);
                } else {
                    noEventsMsg.setVisibility(View.INVISIBLE);
                    Map<String,List<Event>> categorizedEventMap = eventsAdapter.categorizeEvents(eventList);
                    displayCategorizedListView(categorizedEventMap, selfCtxt, listView);

                    viewCalendarButton.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            showCalendarBox(eventList);
                        }
                    });
                }
            }else if (tabNumber == 4){
                //For Statistics
                List<ChartItem> charts = new ArrayList<ChartItem>();

                ChartItem chart1 = new ChartItem();
                chart1.setCategory("Number of Participated Events by Year");
                ChartItem chart2 = new ChartItem();
                chart2.setCategory("Completed Hours of Participated Events by Year");

                charts.add(chart1);
                charts.add(chart2);

                StatisticsAdapter sadapter = new StatisticsAdapter(selfCtxt, charts);

                listView.setAdapter(sadapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {

                        if(position == 0){
                            String data_json = "{\"2017\":2,\"2018\":3,\"2019\":1}";

                            Intent chart = new Intent(getContext(), LineActivity.class);
                            chart.putExtra("max", 3);
                            chart.putExtra("input", data_json);
                            chart.putExtra("legend", "Number of Participated Events by Year");
                            startActivity(chart);
                        }else if(position == 1){
                            String data_json = "{\"2017\":8,\"2018\":9,\"2019\":10}";

                            Intent chart = new Intent(getContext(), LineActivity.class);
                            chart.putExtra("max", 10);
                            chart.putExtra("input", data_json);
                            chart.putExtra("legend", "Completed Hours of Participated Events by Year");
                            startActivity(chart);
                        }
                    }
                });

                viewCalendarButton.setVisibility(View.INVISIBLE);

            }else if (tabNumber == 5){
                viewCalendarButton.setVisibility(View.INVISIBLE);
                //For About Me
                Parent parent = eventsAdapter.getParentDetail(parent_id);

                TextView nameView = (TextView) parentDetailView.findViewById(R.id.name_text);
                TextView nricView = (TextView) parentDetailView.findViewById(R.id.nric_text);
                TextView genderView = (TextView) parentDetailView.findViewById(R.id.gender_text);
                TextView contactView = (TextView) parentDetailView.findViewById(R.id.contact_text);
                TextView emailView = (TextView) parentDetailView.findViewById(R.id.email_text);
                TextView jobView = (TextView) parentDetailView.findViewById(R.id.job_text);
                TextView addressView = (TextView) parentDetailView.findViewById(R.id.address_text);
                ListView childrenView = (ListView) parentDetailView.findViewById(R.id.childrenlist);

                this.REST_API = new RESTApi();
                photo = (ImageView) parentDetailView.findViewById(R.id.photo);
                String imageURL = REST_API.getBaseRestURL() + "/view_user_image?user_id=" + parent_id;
                new LoadImage().execute(imageURL);

                nameView.setText("Name:         " + parent.getFirstname() + " " + parent.getLastname());
                nricView.setText("NRIC:           " + parent.getId());
                genderView.setText( "Gender:        " + parent.getGender());
                contactView.setText("Contact:       " + parent.getContact());
                emailView.setText("Email:          " + parent.getEmail());
                jobView.setText(    "Job:              " + parent.getJob());
                addressView.setText("Address:      " + parent.getAddress());

                ArrayAdapter<Children> mAdapter = getChildrenListItemAdapter(selfCtxt, parent.getChildren());
                childrenView.setAdapter(mAdapter);
                return aboutmeView;
            }

            return rootView;
        }

        private class LoadImage extends AsyncTask<String, String, Bitmap> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = new ProgressDialog(selfCtxt);
                pDialog.setMessage("Loading Image ....");
                pDialog.show();

            }
            protected Bitmap doInBackground(String... args) {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap image) {

                if(image != null){
                    photo.setImageBitmap(image);
                    pDialog.dismiss();
                }else{
                    pDialog.dismiss();
                    Toast.makeText(selfCtxt, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    private static void showCalendarBox(List<Event> eventList) {
        EventCalendarView caldroidFragment = new EventCalendarView(selfCtxt, parent_id, eventList, EventCalendarView.UserType.PARENT);
        caldroidFragment.show(fragmentManager, "Tag");
    }
}
