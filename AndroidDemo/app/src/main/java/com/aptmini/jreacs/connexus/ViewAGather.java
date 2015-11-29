package com.aptmini.jreacs.connexus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewAGather extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                try {
                    JSONObject jObject = new JSONObject(new String(response));
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
}
