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
}
