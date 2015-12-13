package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyGathers extends BasicActivity implements SwipeRefreshLayout.OnRefreshListener{
    Context context = this;
    SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_gathers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getGathers();
    }

    private void getGathers() {
        final String request_url = "http://www." + Homepage.SITE + ".appspot.com/mygathers?number=" + User.getInstance().getNumber();
        System.out.println(request_url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                s.o("success");
                final ArrayList<String> ends = new ArrayList<String>();
                final ArrayList<String> lats = new ArrayList<String>();
                final ArrayList<String> longs = new ArrayList<String>();
                final ArrayList<String> names = new ArrayList<String>();
                final ArrayList<String> starts = new ArrayList<String>();
                final ArrayList<String> statuses = new ArrayList<String>();
                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    JSONArray displayEnds = jObject.getJSONArray(Homepage.END_TIME + "s");
                    JSONArray displayLats = jObject.getJSONArray(Homepage.LATITUDE + "s");
                    JSONArray displayLongs = jObject.getJSONArray(Homepage.LONGITUDE + "s");
                    JSONArray displayNames = jObject.getJSONArray(Homepage.NAME + "s");
                    JSONArray displayStarts = jObject.getJSONArray(Homepage.START_TIME + "s");
                    JSONArray displayStatuses = jObject.getJSONArray(Homepage.USER_STATUS + "es");

                    for (int i = 0; i < displayNames.length(); i++) {

                        ends.add(displayEnds.getString(i));
                        lats.add(displayLats.getString(i));
                        longs.add(displayLongs.getString(i));
                        names.add(displayNames.getString(i));
                        starts.add(displayStarts.getString(i));
                        statuses.add(displayStatuses.getString(i));

                        System.out.println(displayNames.getString(i));
                    }
                    GridView gridview = (GridView) findViewById(R.id.gridview_gathers);
                    //gridview.setAdapter(new ImageAdapter(context,coverURLs));
                    gridview.setAdapter(new MyGatherTextAdapter(MyGathers.this, context, ends, lats, longs, names, starts, statuses));
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {

                            //When clicked - open up a new activity - view a single stream
                            Intent intent = new Intent(context, ViewAGather.class);

                            intent.putExtra(Homepage.NAME, names.get(position));
                            startActivity(intent);



                            /*Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(coverURLs.get(position)).into(image);

                            imageDialog.show();*/
                        }
                    });
                } catch (JSONException j) {
                    System.out.println("JSON Error");
                    j.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("Failure, error code " + e.toString());
                //Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    public void createAGather(View view) {
        Intent intent = new Intent(context, CreateAGather.class);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        s.o("Went to onRefresh()");
        getGathers();
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        onRefresh();
    }
}
