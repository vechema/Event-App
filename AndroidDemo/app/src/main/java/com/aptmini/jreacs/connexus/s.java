package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.location.Address;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Andrew on 11/28/2015.
 */
public class s {

    public static String location;
    public static String ALREADY_PICKED = "already_picked";
    public static String CANT_UNPICK = "cant_unpick";
    public static String GUESTS = "guests";
    public static String GUEST_CATEGORY = "guest_category";

    public static void o(String string){
        System.out.println(string);
    }

    public static String latLngtoAddr(String lat, String lng, Context context)
    {
        return latLngtoAddr(Double.parseDouble(lat), Double.parseDouble(lng), context);
    }

    public static String latLngtoAddr(double lat, double lng, Context context)
    {
        s.o("lat to lng");
        final StringBuilder address = new StringBuilder();

        Geocoder geo = new Geocoder(context);
        List<Address> addresses = new ArrayList<Address>();
        try {
            addresses = geo.getFromLocation(lat, lng, 5);
        } catch (IOException e)
        {
            s.o("ERROR WITH: "+ lat + " "+ lng);
        }

        for(Address addr : addresses)
        {
            s.o(addr.getAddressLine(0));
        }
        String addressLine = addresses.get(0).getAddressLine(0);
        s.o("Lat/Lng return: " + addressLine);
        address.append(addressLine);

        return address.toString();
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
        return timeRange(stringToDate(start), stringToDate(end));
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
