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
public class NumbersTextAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> numbers = new ArrayList<String>();

    public NumbersTextAdapter(Activity a, Context c, ArrayList<String> numbers) {
        mActivity = a;
        mContext = c;
        this.numbers = numbers;
    }

    public int getCount() {
        return numbers.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolder
    {
        public TextView numberTxt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder view;
        LayoutInflater inflator = mActivity.getLayoutInflater();

        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.layout_numbers_text, null);

            view.numberTxt = (TextView) convertView.findViewById(R.id.numberTextView);

            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.numberTxt.setText(numbers.get(position));

        return convertView;
    }
}
