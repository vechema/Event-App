package com.aptmini.jreacs.connexus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewAGather extends BasicActivity {

    Context context;
    View mView;
    Button button_going;
    Button button_interested;
    Button button_ignore;
    Button button_delete;

    String number;
    String gatherTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_a_gather);
        mView = this.findViewById(android.R.id.content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the name of the Gather from which this was called.
        Intent intent = getIntent();
        String gatherTitle = intent.getStringExtra(Homepage.NAME);

        //Grab the delete button
        button_delete = (Button) findViewById(R.id.button_delete);

        this.number = User.getInstance().getNumber();
        this.gatherTitle = gatherTitle;
        String request_url = "http://www." + Homepage.SITE + ".appspot.com/viewgather?number=" + number + "&gatherid=" + gatherTitle;
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

                    //Make the delete button show up
                    boolean isAdmin = Boolean.parseBoolean(adStat);
                    if (isAdmin)
                    {
                        s.o("YOU ARE THE FATHER/ADMIN");
                        //make button visible
                        button_delete.setVisibility(View.VISIBLE);
                    }

                    s.o("User status: "+use_status);
                    if(use_status.equals("going"))
                    {
                        s.o(use_status + " picked");
                        selectGoing(mView, false);
                    }
                    else if (use_status.equals("interested"))
                    {
                        s.o(use_status + "picked");
                        selectInterested(mView, false);
                    }
                    else if (use_status.equals("ignore"))
                    {
                        s.o(use_status + "picked");
                        selectIgnore(mView, false);
                    }
                    else if (use_status.equals("invited"))
                    {
                        s.o(use_status + "picked");

                    }
                    else
                    {
                        s.o("SOMETING WEIRD WITH USER STATUS: " + use_status);
                    }

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
        s.o("" + button_going);
    }

    public void selectGoing(View view) {
        selectGoing(view, true);
    }

    public void selectGoing(View view, boolean flag)
    {
        s.o("I'm going!");
        setUpButtons();
        selectButton(button_going, button_interested, button_ignore);
        setStatus("going", flag);
    }

    public void selectInterested(View view) {
        selectInterested(view, true);
    }

    public void selectInterested(View view, boolean flag)
    {
        s.o("I'm interested");
        setUpButtons();
        selectButton(button_interested, button_ignore, button_going);
        setStatus("interested", flag);

    }

    public void selectIgnore(View view) {
        selectIgnore(view, true);
    }

    public void selectIgnore(View view, boolean flag)
    {
        s.o("IGNORE!");
        setUpButtons();
        selectButton(button_ignore, button_interested, button_going);
        setStatus("ignore", flag);

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

    private void setStatus(String status, boolean flag) {
        final boolean fromStart = flag;
        //make async http request - gatherid
        String request_url = "http://www." + Homepage.SITE + ".appspot.com/changestatus?number="
                + number + "&gatherid=" + gatherTitle + "&status=" + status;
        s.o(request_url);
        final String status_final = status;
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                s.o("successfully accessed change status backend!");

                if (fromStart) {
                    Context context = getApplicationContext();
                    CharSequence text = "Successfully set as " + status_final;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                s.o("There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    public void deleteButton(View view)
    {
        s.o("Going into delete gather");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this gather?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                s.o("I CLICKED DELETE");
                deleteGather();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                s.o("I clicked DON'T DELTE PELASE!");
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void deleteGather()
    {
        String request_url = "http://www." + Homepage.SITE + ".appspot.com/deletegather?number="
                + number + "&gatherid=" + gatherTitle;
        s.o(request_url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                s.o("successfully accessed delete gather!");

                Context context = getApplicationContext();
                CharSequence text = "Gather deleted";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Intent i = new Intent(context, MyGathers.class);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                s.o("There was a problem in retrieving the url : " + e.toString());
            }
        });
    }
}
