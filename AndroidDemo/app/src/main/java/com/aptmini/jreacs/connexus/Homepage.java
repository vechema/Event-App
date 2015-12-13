package com.aptmini.jreacs.connexus;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import java.net.URL;
import java.net.URLDecoder;

public class Homepage extends ActionBarActivity {
    static public String NUMBER = "number";
    static public String LATITUDE = "latitude";
    static public String LONGITUDE = "longitude";
    static public String GATHER_ID = "gatherid";
    static public String NAME = "name";
    static public String START_TIME = "start_time";
    static public String END_TIME = "end_time";
    static public String DESCRIPTION = "description";
    static public String USER_STATUS = "user_status";
    static public String VISIBILITY = "visibility";
    static public String PIC_URL = "pic_url";
    static public String INVITE_LEVEL = "invite_level";
    static public String USERS_INVITED = "users_invited";
    static public String SITE = "apt2015final2";

    public static String email;

    private static final String TAG = "android-demo-login";

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    private static final String SAVED_PROGRESS = "sign_in_progress";

    private GoogleApiClient mGoogleApiClient;


    private int mSignInProgress;


    private PendingIntent mSignInIntent;

    private int mSignInError;


    private SignInButton mSignInButton;
    private Button mSignOutButton;
    private Button mRevokeButton;
    private TextView mStatus;

    Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        s.o("Debugging start; Hey it works!");

        //Get number of user
        String number = "7137756016";

        //Set the user singleton's number
        User.getInstance().setNumber(number);

        //Create the URL
        final String request_url = "http://www." + SITE +".appspot.com/login?number=" + number;
        System.out.println(request_url);


        //Check to see if the user with this number already exists. If it does, redirect to My Gathers
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    System.out.println("success");
                    JSONObject jObject = new JSONObject(new String(response));
                    System.out.println(jObject);
                    String user_name = jObject.getString("name");
                    System.out.println(user_name);

                    if (!user_name.equals("null")) {
                        System.out.println("user exists!");
                        User.getInstance().setName(user_name);
                        Intent intent = new Intent(context, MyGathers.class);
                        startActivity(intent);
                    }
                    else{
                        System.out.println("User does not exist.");
                    }

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("did not succeed");
                Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
            }
        });

        //If the user does not already exist, create the new user page
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

    }

    public void submit(View v) {
        //get username input
        System.out.println("submitted");
        EditText txtusername = (EditText) findViewById(R.id.username);
        String username = txtusername.getText().toString();
        System.out.println(username);

        //Check to make sure username is not "null", "None", or empty
        if (username.equals("None") || username.equals("null") || username.equals("")){

            Toast.makeText(context, "Sorry, but this name is invalid.", Toast.LENGTH_SHORT).show();
        }
        else {
            //store username to go with the user object whose ID is now this phone's number
            //Store this both in datastore and in singleton

            //Store in singleton
            User.getInstance().setName(username);

            //Create the URL
            final String request_url = "http://www." + SITE + ".appspot.com/signup?number=" + User.getInstance().getNumber() + "&name=" + username;
            System.out.println(request_url);

            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        //Check that the username was added successfully
                        System.out.println("success");
                        JSONObject jObject = new JSONObject(new String(response));

                        String result = jObject.getString("result");
                        System.out.println(result);

                        //If so, redirect to MyGathers
                        if(result.equals("true")){
                            Intent intent = new Intent(context, MyGathers.class);
                            startActivity(intent);
                        }

                    } catch (JSONException j) {
                        System.out.println("JSON Error");
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    System.out.println("failure");
                    Log.e(TAG, "There was a problem in retrieving the url : " + e.toString());
                }
            });
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        s.o("RESUMED");
        if(User.getInstance().getName() != null)
        {
            Intent i = new Intent(this, MyGathers.class);
            startActivity(i);
        }
    }
}