package com.aptmini.jreacs.connexus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CreateAGather extends FragmentActivity {
    Context context = this;
    static int startYear;
    static int startMonth;
    static int startDay;
    static int startHour;
    static int startMinute;
    static int endYear;
    static int endMonth;
    static int endDay;
    static int endHour;
    static int endMinute;
    String title;
    String address;
    String startString;
    String endString;
    float lat;
    float lng;
    String allNumbersString;
    ArrayList<String> numbers =  new ArrayList<String>();
    int PICK_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_gather);
        final Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMinute = c.get(Calendar.MINUTE);
    }

    //Start Date: Define a fragment which will help us display a start date picker dialog.
    //Default is current date
    public static class StartDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            System.out.println(year);
            System.out.println(month);
            System.out.println(day);
            startYear = year;
            startMonth = month;
            startDay = day;
        }
    }

    //Start date: Show the start-date picker dialog when the button is pressed
    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //Start Time: Define a fragment which will help us display a start time picker dialog.
    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
            startHour = hourOfDay;
            startMinute = minute;
        }
    }

    //Start time: Show the start-time picker dialog when the button is pressed
    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new StartTimePickerFragment();
        newFragment.show(getFragmentManager(),"timePicker");
    }

    //End Date: Define a fragment which will help us display a end date picker dialog.
    //Default is start date
    public static class EndDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the start date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = startYear;
            int month = startMonth;
            int day = startDay;

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            System.out.println(year);
            System.out.println(month);
            System.out.println(day);
            endYear = year;
            endDay = day;
            endMonth = month;
        }
    }

    //End date: Show the end-date picker dialog when the button is pressed
    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //End date: Define a fragment which will help us display a start time picker dialog.
    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = startHour + 1;
            if(hour == 24){
                hour = 0;
            }
            int minute = startMinute;

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
            endHour = hourOfDay;
            endMinute = minute;
        }
    }

    //End time: Show the end-time picker dialog when the button is pressed
    public void showEndTimePickerDialog(View v) {
        DialogFragment newFragment = new EndTimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    //Pick the contacts by going to the contact picker page
    public void pickContacts(View view){
        Intent intent= new Intent(this, PickContacts.class);
        startActivityForResult(intent, PICK_CONTACTS);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACTS && data != null) {
            System.out.println("PICK CONTACTS");
            numbers = data.getStringArrayListExtra("numbers");
            System.out.println(numbers);
        }
    }

    //Manually add a contact to the list
    public void addAContact(View view){
        EditText txtphoneNo = (EditText) findViewById(R.id.guests);
        String numberString = txtphoneNo.getText().toString();
        System.out.println("I AM DEBUGGING!!!!!");
        //numbers = numberString;
//        numbers = "7137756018+7137756019";
        numbers.add(numberString);
        System.out.println(numberString);
    }

    //Make the gather!
    public void makeGather(View v){
        //get number(s) input
        for (String phoneNo : numbers) {
            allNumbersString = allNumbersString + "+" + phoneNo;
        }

        //get address and convert it to lat/lng
        EditText txtAddress = (EditText) findViewById(R.id.gather_location);
        //address = txtAddress.getText().toString();
        address = "403 East 35th St. Austin, TX 78705";
        System.out.println(address);
        GeoPoint gatherPoint = getLocationFromAddress(address);
        System.out.println(gatherPoint.lat);
        lat = (float) gatherPoint.lat;
        lng = (float) gatherPoint.lng;
        System.out.println(lat);
        System.out.println(lng);

        //get title
        EditText txtTitle = (EditText) findViewById(R.id.gather_title);
        title = txtTitle.getText().toString();
        System.out.println(title);

        //convert date and time to proper format
        //Date & Time format = 2015-11-18 01:34:23.360332. Manually add 0.00 for s and ms
        appendZeros(startMonth,2);

        startString = appendZeros(startYear,4)+"-"+appendZeros(startMonth,2)+"-"+appendZeros(startDay,2)+" "+appendZeros(startHour,2)+":"+appendZeros(startMinute,2)+":" + "0.00";
        endString = appendZeros(endYear,4)+"-"+appendZeros(endMonth,2)+"-"+appendZeros(endDay,2)+" "+appendZeros(endHour,2)+":"+appendZeros(endMinute,2)+":" + "0.00";


        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            Date date = format.parse(startString);
            System.out.println(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        System.out.println(startString);
        System.out.println(endString);

        System.out.println(User.getInstance().getName());
        //Send the gather data to the backend, where a gather object will be created.
        postToServer();

        //Update all the guests that they have been invited to the gather via text message.
        //sendSMSMessage();

        Intent intent = new Intent(context, ViewAGather.class);

        intent.putExtra(Homepage.NAME, title);
        startActivity(intent);
    }

    //prefixes an input number with zeros if it does not meet the required format.
    private String appendZeros(int myNum, int i) {
        String stringNum = myNum + "";
        while (stringNum.length()<i){
            stringNum = "0" + stringNum;
        }
        return stringNum;
    }

    //Get latitude and longitude from the address input
    public GeoPoint getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint(location.getLatitude(),location.getLongitude());

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Send the Gather information out as a text message
    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        for (String phoneNo : numbers) {
            String message = "You're invited to " + title + " at " + startString +"! -Gather";
            System.out.println(message);

            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    //Pass the Gather information to the backend
    //Step 1, get the upload URL
//    private void getUploadURL(){
//        AsyncHttpClient httpClient = new AsyncHttpClient();
//        String request_url="http://www.apt2015final.appspot.com/creategather";
//        System.out.println(request_url);
//        httpClient.get(request_url, new AsyncHttpResponseHandler() {
//            String upload_url;
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//
//                try {
//                    JSONObject jObject = new JSONObject(new String(response));
//
//                    upload_url = jObject.getString("upload_url");
//                    postToServer(upload_url);
//
//                } catch (JSONException j) {
//                    System.out.println("JSON Error");
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
//            }
//        });
//    }

    //Pass the Gather information to the backend
    //Step 2, input the information into the request parameters and send it to the backend.
    private void postToServer(){
        RequestParams params = new RequestParams();
//        System.out.println("Check parameters");
//        System.out.println(startString);

        String upload_url = "http://www." + Homepage.SITE + ".appspot.com/creategather";
        params.put("start_time",startString);
        params.put("users_invited", allNumbersString);
        params.put("end_time",endString);
        params.put("name", title);
        params.put("gatherid", title);
        params.put("latitude", lat);
        params.put("longitude", lng);
        params.put("number", User.getInstance().getNumber());
        params.put("visibility", "private");
        params.put("description", "");

//        String upload_url = "http://www." + Homepage.SITE + ".appspot.com/creategather?";
//        upload_url = upload_url + Homepage.NUMBER + "=" + User.getInstance().getNumber() +"&";
//        upload_url = upload_url + Homepage.LATITUDE + "=" + lat +"&";
//        upload_url = upload_url + Homepage.LONGITUDE + "=" + lng +"&";
//        upload_url = upload_url + Homepage.NAME + "=" + title +"&";
//        upload_url = upload_url + Homepage.START_TIME + "=" + startString +"&";
//        upload_url = upload_url + Homepage.USERS_INVITED + "=" + numbers +"&";
//        upload_url = upload_url + Homepage.END_TIME + "=" + endString +"&";
//        upload_url = upload_url + Homepage.VISIBILITY + "=" + "private" +"&";
//        upload_url = upload_url + Homepage.DESCRIPTION + "=" + "description";

        System.out.println(upload_url);

        AsyncHttpClient client = new AsyncHttpClient();
        //client.post(upload_url, new AsyncHttpResponseHandler() {
        client.post(upload_url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.w("async", "success!!!!");
                System.out.println("Create_Success");
                Toast.makeText(context, "Gather Created Successfully!", Toast.LENGTH_SHORT).show();
//                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("Create_Failure");
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }
}

