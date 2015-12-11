package com.aptmini.jreacs.connexus;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Andrew on 11/28/2015.
 */
public class s {

    public static String location;

    public static void o(String string){
        System.out.println(string);
    }

    public static String latLngtoAddr(double lat, double lng)
    {
        final StringBuilder address = new StringBuilder();
        String request_url = "http://maps.google.com/maps/api/geocode/json?latlng="+lat+","+lng+"&sensor=false";
        AsyncHttpClient httpClient = new AsyncHttpClient();

        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                s.o("success");
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    String getAddr = jObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    address.append(getAddr);
                    location = getAddr;
                    s.o("In s! " + getAddr);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("Failure, error code " + e.toString());
                //Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });

        return address.toString();
    }

    public static String formatLocation(String s)
    {
        return formatLocation(s, 0);
    }

    public static String formatLocation(String s, int level)
    {
        String[] location = s.split(",");
        for (String t : location)
        {
            t = t.trim();
            //System.out.print(t);
        }
        int index = level;
        if(location.length == 5) {index+=1;}
        String result = "";
        if (index >= location.length)
        {
            return s;
        }
        for(int i = 0; i <= index; i++)
        {
            result+=location[i];
            if( index >=1 && i < index)
            {
                result+=",";
            }
        }
        return result;
    }

    public static Date stringToDate(String s)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(s);
        Date date = new Date();
        try {
            date = format.parse(s);
            System.out.println(date.getHours());
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    public static String timeRange(String start, String end)
    {
        return timeRange(stringToDate(start),stringToDate(end));
    }

    public static String timeRange(Date start, Date end)
    {
        String result = prettyDate(start);
        if(isSameDay(start,end))
        {
            String prettyEnd = prettyDate(end);
            result += " -" + prettyEnd.substring(prettyEnd.lastIndexOf(" "));
        }
        else
        {
            result += " - " + prettyDate(end);
        }

        return result;
    }

    public static String prettyDate(String s)
    {
        return prettyDate(stringToDate(s));
    }

    public static String prettyDate(Date d)
    {
        String[] t = d.toString().split(" ");
        String temp = t[1] + " " + t[2] + " " + t[3].substring(0,t[3].lastIndexOf(":"));

        return temp;
    }

    public static boolean isSameDay(Date one, Date two)
    {
        boolean isSameDay = false;
        if(one.getDate() == two.getDate() && one.getMonth() == two.getMonth() && one.getYear() == two.getYear())
        {
            isSameDay = true;
        }
        return isSameDay;
    }
}
