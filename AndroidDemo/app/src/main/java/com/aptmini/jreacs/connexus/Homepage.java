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
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
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
import java.util.Random;

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
    static public String HAS_PIC = "has_pic";
    static public String INVITE_LEVEL = "invite_level";
    static public String USERS_INVITED = "users_invited";
    static public String SITE = "apt2015final2";

    public static int randNum;
    String temp_phone_number;
    String weird_id;

    private static final String TAG = "android-demo-login";

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    private static final String SAVED_PROGRESS = "sign_in_progress";

    private GoogleApiClient mGoogleApiClient;
    public static String email = "";

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
        super.onCreate(savedInstanceState);
        //Get id of user
        this.weird_id = getWeirdId();

        // For a loading page or whatever
        //setContentView(R.layout.activity_homepage);

        //See if they are already a user
        seeIfUser(this.weird_id);


    }

    private void seeIfUser(String id) {
        //Create the URL
        final String request_url = "http://www." + SITE +".appspot.com/login?id=" + id;
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
                    String user_number = jObject.getString("number");

                    if (!user_name.equals("null")) {
                        System.out.println("user exists!");
                        User.getInstance().setName(user_name);
                        User.getInstance().setNumber(user_number);
                        User.getInstance().addId(weird_id);
                        Intent intent = new Intent(context, MyGathers.class);
                        startActivity(intent);
                    } else {
                        System.out.println("User does not exist.");
                        setContentView(R.layout.activity_homepage);
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
    }

    public void submit(View v) {
        //get username input
        System.out.println("submitted");
        EditText txt_phone_number = (EditText) findViewById(R.id.phone_number);
        String phone_number = txt_phone_number.getText().toString();
        temp_phone_number = phone_number;
        System.out.println(phone_number);

        //Check to make sure phone_number is not "null", "None", or empty
        if (phone_number.equals("None") || phone_number.equals("null") || phone_number.equals("") ){

            Toast.makeText(context, "Sorry, but this number isn't invalid.", Toast.LENGTH_SHORT).show();
        }
        else {
            //We need to text them a "random number"

            //Set the "random number"
            randNum = randInt(0, 10000);

            //Send text
            String message = "Your code for Gather login is: " + randNum;
            System.out.println(message);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phone_number, null, message, null, null);
                //Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                s.o("SMS sent for login");
                setContentView(R.layout.activity_homepage2);

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                s.o("SMS NOT sent for login");
                e.printStackTrace();
            }
        }
    }

    public void submit2(View v)
    {
        EditText txt_phone_code = (EditText) findViewById(R.id.phone_code);
        String phone_code = txt_phone_code.getText().toString();
        String given_code = "" + randNum;
        if(!phone_code.equals(given_code))
        {
            Toast.makeText(getApplicationContext(), "Wrong code", Toast.LENGTH_SHORT).show();
        } else {
            EditText txt_user_name = (EditText) findViewById(R.id.user_name);
            String user_name = txt_user_name.getText().toString();

            //Now I have the user name (user_name), the phone number (temp_phone_number), AND the weird id (weird_id)
            signUpUser(user_name, temp_phone_number, weird_id);

        }
    }

    public void signUpUser(String user, String phone_number, String id)
    {
        final String user_name_final = user;
        final String phone_final = phone_number;
        final String weird_id_final = id;
        //Create the URL
        final String request_url = "http://www." + SITE + ".appspot.com/signup?number=" + phone_number
                + "&name=" + user + "&id=" + id;
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
                    User.getInstance().setName(user_name_final);
                    User.getInstance().setNumber(phone_final);
                    User.getInstance().addId(weird_id_final);

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

    public static int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
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

    public String getWeirdId()
    {
        return Installation.id(this);
    }

}