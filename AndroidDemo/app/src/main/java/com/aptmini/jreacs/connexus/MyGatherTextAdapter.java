package com.aptmini.jreacs.connexus;

/**
 * Created by Andrew on 11/28/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Jo on 10/24/2015.
 */
public class MyGatherTextAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> ends = new ArrayList<String>();
    private ArrayList<String> lats = new ArrayList<String>();
    private ArrayList<String> longs = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();
    private ArrayList<String> starts = new ArrayList<String>();
    private ArrayList<String> statuses = new ArrayList<String>();

    public MyGatherTextAdapter(Activity a, Context c, ArrayList<String> ends, ArrayList<String> lats, ArrayList<String> longs, ArrayList<String> names, ArrayList<String> starts, ArrayList<String> statuses) {
        mActivity = a;
        mContext = c;
        this.ends = ends;
        this.lats = lats;
        this.longs = longs;
        this.names = names;
        this.starts = starts;
        this.statuses = statuses;
    }

    public int getCount() {
        return names.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        public TextView nameTxt;
        public TextView placeTxt;
        public TextView timeTxt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = mActivity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_gather_text, null);

            view.nameTxt = (TextView) convertView.findViewById(R.id.gather_name);
            view.placeTxt = (TextView) convertView.findViewById(R.id.gather_place);
            view.timeTxt = (TextView) convertView.findViewById(R.id.gather_time);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.nameTxt.setText(names.get(position));
        //view.imgViewPic.setImageResource(imageURLs.get(position));
        //Picasso.with(mContext).load(imageURLs.get(position)).into(view.imgViewPic);

        //Set the location
        String lat = lats.get(position);
        String lng = longs.get(position);
        String location = s.latLngtoAddr(lat, lng, mContext);
        view.placeTxt.setText(location);
        s.o("Lat: " + lat + " lng: " + lng);
        s.o("But location: " + location);
        //view.placeTxt.setText(lats.get(position)+ " " + longs.get(position));

        //Set the start to end time
        String start_time = starts.get(position);
        String end_time = ends.get(position);
        String range = s.timeRange(start_time,end_time);
        view.timeTxt.setText(range);
        // view.timeTxt.setText(starts.get(position) + "to" + ends.get(position));

        return convertView;
    }
}
