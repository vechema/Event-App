package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

public class MyGathers extends ActionBarActivity {
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gathers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String request_url = "http://www.apt2015final.appspot.com/mygathers?number=" + User.getInstance().getNumber();
        System.out.println(request_url);
        AsyncHttpClient httpClient = new AsyncHttpClient();
//        httpClient.get(request_url, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                final ArrayList<String> ends = new ArrayList<String>();
//                final ArrayList<String> lats = new ArrayList<String>();
//                final ArrayList<String> longs = new ArrayList<String>();
//                final ArrayList<String> names = new ArrayList<String>();
//                final ArrayList<String> starts = new ArrayList<String>();
//                final ArrayList<String> statuses = new ArrayList<String>();
//                try {
//                    JSONObject jObject = new JSONObject(new String(response));
//
//                    JSONArray displayEnds = jObject.getJSONArray(Homepage.END_TIME + "s");
//                    JSONArray displayLats = jObject.getJSONArray(Homepage.LATITUDE + "s");
//                    JSONArray displayLongs = jObject.getJSONArray(Homepage.LONGITUDE + "s");
//                    JSONArray displayNames = jObject.getJSONArray(Homepage.NAME + "s");
//                    JSONArray displayStarts = jObject.getJSONArray(Homepage.START_TIME + "s");
//                    JSONArray displayStatuses = jObject.getJSONArray(Homepage.USER_STATUS + "es");
//
//                    for(int i=0;i<displayNames.length();i++) {
//
//                        ends.add(displayEnds.getString(i));
//                        lats.add(displayLats.getString(i));
//                        longs.add(displayLongs.getString(i));
//                        names.add(displayNames.getString(i));
//                        starts.add(displayStarts.getString(i));
//                        statuses.add(displayStatuses.getString(i));
//
//                        System.out.println(displayNames.getString(i));
//                    }
//                    GridView gridview = (GridView) findViewById(R.id.gridview_gathers);
//                    //gridview.setAdapter(new ImageAdapter(context,coverURLs));
//                    gridview.setAdapter(new ImageTextAdapter(MyGathers.this, context, ends, lats, longs, names, starts, statuses));
//                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View v,
//                                                int position, long id) {
//
//                            //When clicked - open up a new activity - view a single stream
//                            Intent intent = new Intent(context, ViewAStream.class);
//                            String stream_name = streamNames.get(position);
//                            String owner_email = ownerEmails.get(position);
//                            System.out.println("DisplayStreams, stream name: " + stream_name);
//                            intent.putExtra(STREAM_NAME, stream_name);
//                            intent.putExtra(OWNER_EMAIL, owner_email);
//                            startActivity(intent);
//
//                            /*Toast.makeText(context, streamNames.get(position), Toast.LENGTH_SHORT).show();
//
//                            Dialog imageDialog = new Dialog(context);
//                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                            imageDialog.setContentView(R.layout.thumbnail);
//                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);
//
//                            Picasso.with(context).load(coverURLs.get(position)).into(image);
//
//                            imageDialog.show();*/
//                        }
//                    });
//                }
//                catch(JSONException j){
//                    System.out.println("JSON Error");
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
//            }
//        }

    }
}
