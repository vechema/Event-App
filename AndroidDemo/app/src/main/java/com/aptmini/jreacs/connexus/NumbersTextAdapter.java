package com.aptmini.jreacs.connexus;

/**
 * Created by Andrew on 11/28/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jo on 10/24/2015.
 */
public class NumbersTextAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context mContext;
    private ArrayList<String> numbers = new ArrayList<String>();
    String username;
    int busywaitflag;

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
        busywaitflag = 0;
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

        //Check to see if the number has a username associated with it
        username = numbers.get(position);

        final String request_url = "http://www." + Homepage.SITE +".appspot.com/login?number=" + username;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    System.out.println("success");
                    JSONObject jObject = new JSONObject(new String(response));
                    System.out.println(jObject);
                    String user_name = jObject.getString("name");
                    System.out.println("User_name is " + user_name);

                    if (!user_name.equals("null")) {
                        username = user_name;
                        s.o("username is " + username);
                    } else {
                        //do nothing
                    }
                    busywaitflag = 1;

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("did not succeed");
                Log.e("NumbersTextAdapter", "There was a problem in retrieving the url : " + e.toString());
            }
        });

        //delay 1 second
//        try {
//            Thread.sleep(5000);                 //1000 milliseconds is one second.
//        } catch(InterruptedException ex) {
//            Thread.currentThread().interrupt();
//        }

        s.o("THE USERNAME IS");
        s.o(username);
        view.numberTxt.setText(username);

        return convertView;
    }
}
