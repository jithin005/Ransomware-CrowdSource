package com.amrita.shawn.ransomwarecrowd.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amrita.shawn.ransomwarecrowd.R;
import com.amrita.shawn.ransomwarecrowd.app.Connection;

import java.util.List;

/**
 * Created by shawn on 12/8/16.
 */

public class CustomListAdapter extends BaseAdapter{

    private Activity activity;
    private LayoutInflater inflater;
    private List<Connection> connection;

    public CustomListAdapter(Activity activity, List<Connection> con){

        this.activity = activity;
        this.connection = con;

    }
    @Override
    public int getCount() {
        return connection.size();
    }

    @Override
    public Object getItem(int location) {
        return connection.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView ip = (TextView) convertView.findViewById(R.id.ip);
        TextView procName = (TextView) convertView.findViewById(R.id.proc_name);

        // getting movie data for the row
        Connection m = connection.get(position);



        // title
        ip.setText(m.getIp());

        // rating
        procName.setText(m.getProcName());


        return convertView;
    }
}
