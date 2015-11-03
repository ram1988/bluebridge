package com.ibm.bluebridge;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ibm.bluebridge.R;
import com.ibm.bluebridge.valueobject.Children;
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
            private TextView eventNameView;
            private TextView maxRegView;
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
                viewHolder.eventNameView = (TextView) convertView.findViewById(R.id.event_name);
                viewHolder.maxRegView = (TextView) convertView.findViewById(R.id.max_registered);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Event item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.eventNameView.setText(String.format("%s", item.getEventName()));
                viewHolder.maxRegView.setText("Registered: "+ String.format("%s", item.getMaxVolunteers()-item.getVacancy()) + "    Vacancies: "+ String.format("%s", item.getVacancy()));
            }

            return convertView;

        }
    }

    private static class ParentListItemAdapter<T> extends ArrayAdapter<Parent>  {

        private ParentModes parentMode;
        private class ViewHolder {
            private TextView txtView;
            private CheckBox chkBox;
        }

        public ParentListItemAdapter(Context context,
                                     List<Parent> objects, ParentModes mode) {
            super(context, 0, objects);
            parentMode = mode;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.parent_list_item, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.txtView = (TextView) convertView.findViewById(R.id.parent_name);
                viewHolder.chkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

                if(parentMode == ParentModes.PARENT_ATTENDED) {
                    viewHolder.chkBox.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.chkBox.setVisibility(View.INVISIBLE);
                }

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

    private static class ChildrenListItemAdapter<T> extends ArrayAdapter<Children>  {

        private class ViewHolder {
            private TextView txtView;
        }

        public ChildrenListItemAdapter(Context context,
                                     List<Children> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.children_list_view, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.txtView = (TextView) convertView.findViewById(R.id.child_detail);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Children item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.txtView.setText(String.format("%s", item.getName()));
            }

            return convertView;

        }
    }

    protected static ArrayAdapter<Event> getEventArrayAdapter(Context ctxt,List<Event> events) {
        return new EventListItemAdapter<Event>(ctxt,events);
    }

    protected static ArrayAdapter<Parent> getParentListItemAdapter(Context ctxt,List<Parent> parents,ParentModes mode) {
        return new ParentListItemAdapter<Parent>(ctxt, parents, mode);
    }

    protected static ArrayAdapter<Children> getChildrenListItemAdapter(Context ctxt,List<Children> children) {
        return new ChildrenListItemAdapter<Children>(ctxt, children);
    }

    protected enum ParentModes {
        PARENT_REGISTERED, PARENT_ATTENDED
    }
}
