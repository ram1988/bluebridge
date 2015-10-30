package com.ibm.bluebridge;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ibm.bluebridge.R;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manirm on 10/25/2015.
 */
public class EventMasterActivity extends ActionBarActivity {

    private static class EventListItemAdapter<T> extends ArrayAdapter<Event>  {

        private class ViewHolder {
            private TextView txtView;
        }

        public EventListItemAdapter(Context context,
                           List<Event> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.event_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.txtView = (TextView) convertView.findViewById(R.id.event_name);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Event item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.txtView.setText(String.format("%s", item.getEventName()));
            }

            return convertView;

        }
    }

    private static class ParentListItemAdapter<T> extends ArrayAdapter<Parent>  {

        private class ViewHolder {
            private TextView txtView;
        }

        public ParentListItemAdapter(Context context,
                                     List<Parent> objects) {
            super(context, 0, objects);
            System.out.println("Size-->" + objects.size());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.parent_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.txtView = (TextView) convertView.findViewById(R.id.parent_name);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Parent item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.txtView.setText(String.format("%s", item.getFirstname()+" "+item.getLastname()));
            }

            return convertView;

        }
    }

    protected static ArrayAdapter<Event> getEventArrayAdapter(Context ctxt,List<Event> events) {
        return new EventListItemAdapter<Event>(ctxt,events);
    }

    protected static ArrayAdapter<Parent> getParentListItemAdapter(Context ctxt,List<Parent> parents) {
        return new ParentListItemAdapter(ctxt, parents);
    }
}
