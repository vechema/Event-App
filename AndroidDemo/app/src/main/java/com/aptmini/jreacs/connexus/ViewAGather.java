package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewAGather extends ActionBarActivity {

    Context context;
    Button button_going;
    Button button_interested;
    Button button_ignore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_a_gather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the name of the Gather from which this was called.
        Intent intent = getIntent();
        String gatherTitle = intent.getStringExtra(Homepage.NAME);

        String request_url = "http://www." + Homepage.SITE + ".appspot.com/viewgather?number=" + User.getInstance().getNumber() + "&gatherid=" + gatherTitle;
        s.o(request_url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                s.o("successfully accessed viewagather backend!");
                final String name;
                final String start;
                final String end;
                final String lat;
                final String lng;
                final String description;
                final String use_status;
                final String adStat;
                final String visibility;


                try {
                    JSONObject jObject = new JSONObject(new String(response));
                    name = jObject.getString(Homepage.NAME);
                    start = jObject.getString(Homepage.START_TIME);
                    end = jObject.getString(Homepage.END_TIME);
                    lat = jObject.getString(Homepage.LATITUDE);
                    lng = jObject.getString(Homepage.LONGITUDE);
                    description = jObject.getString(Homepage.DESCRIPTION);
                    use_status = jObject.getString(Homepage.USER_STATUS);
                    adStat = jObject.getString("admin");
                    visibility = jObject.getString(Homepage.VISIBILITY);

                    s.o(visibility);
                    s.o(start);
                    s.o(adStat);

                    TextView titleTextView = (TextView) findViewById(R.id.viewg_title);
                    titleTextView.setText(name);

                    TextView timeTextView = (TextView) findViewById(R.id.viewg_time);
                    String range = s.timeRange(start, end);
                    timeTextView.setText(range);
                    //timeTextView.setText(start + " to " + end);

                    TextView placeTextView = (TextView) findViewById(R.id.viewg_place);
                    String location = s.latLngtoAddr(lat, lng, context);
                    placeTextView.setText(location);
                    //placeTextView.setText(lat + " " + lng);

                    TextView descriptionTextView = (TextView) findViewById(R.id.viewg_description);
                    descriptionTextView.setText("Description: " + description);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                s.o("There was a problem in retrieving the url : " + e.toString());
            }


        });
    }

    public void setUpButtons()
    {
        button_going = (Button) findViewById(R.id.button_going);
        button_interested = (Button) findViewById(R.id.button_interested);
        button_ignore = (Button) findViewById(R.id.button_ignore);
        s.o("Buttons made!");
        s.o(""+button_going);
    }

    public void selectGoing(View view) {
        s.o("I'm going!");
        setUpButtons();
        selectButton(button_going, button_interested, button_ignore);
    }

    public void selectInterested(View view) {
        s.o("I'm interested");
        setUpButtons();
        selectButton(button_interested, button_ignore, button_going);
    }

    public void selectIgnore(View view) {
        s.o("IGNORE");
        setUpButtons();
        selectButton(button_ignore,button_going, button_interested);
    }

    public void selectButton(Button selected, Button not_selected_one, Button not_selected_two)
    {
        selected.setSelected(true);
        not_selected_one.setSelected(false);
        not_selected_two.setSelected(false);

        selected.setActivated(false);
        not_selected_one.setActivated(true);
        not_selected_two.setActivated(true);
    }
}
