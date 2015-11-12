package com.ibm.bluebridge.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ibm.bluebridge.R;
import com.ibm.bluebridge.valueobject.ChartItem;

import java.util.List;

/**
 * Created by huangq on 11/11/2015.
 */
public class StatisticsAdapter extends ArrayAdapter<ChartItem> {
    private List<ChartItem> chartList;
    private Context ctxt;

    private class ViewHolder {
        private TextView categoryView;
    }

    public StatisticsAdapter(Context context,
                                List<ChartItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.statistics_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.categoryView = (TextView) convertView.findViewById(R.id.category_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChartItem item = getItem(position);
        if (item!= null) {
            // My layout has only one TextView
            // do whatever you want with your string and long
            viewHolder.categoryView.setText(String.format("%s", item.getCategory()));
        }

        return convertView;

    }
}
