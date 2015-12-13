package com.aptmini.jreacs.connexus;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class CreateAGather extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final String LOG_TAG = "MainActivity";

    //Stuff to make the autocomplete work
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));
    private TextView mAddressTextView;

    //Declare the variables for the gather
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
    String description;
    String imageFilePath;
    byte[] encodedImage;
    float lat;
    float lng;
    String allNumbersString;
    ArrayList<String> numbers =  new ArrayList<String>();
    int PICK_CONTACTS = 1;
    int PICK_PICTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_a_gather);

        //Set the default start end/time
        final Calendar c = Calendar.getInstance();
        startYear = c.get(Calendar.YEAR);
        startMonth = c.get(Calendar.MONTH);
        startDay = c.get(Calendar.DAY_OF_MONTH);
        startHour = c.get(Calendar.HOUR_OF_DAY) + 1;
        startMinute = 0;
        mAddressTextView = (TextView) findViewById(R.id.address);

        //display the default start time
        displayStartDate();
        displayStartTime();

        //Set the default end/time
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);
        endHour = c.get(Calendar.HOUR_OF_DAY) + 2;
        endMinute = 0;

        //display the default end time
        displayEndDate();
        displayEndTime();


        //Set stuff up for place autocomplete
        mGoogleApiClient = new GoogleApiClient.Builder(CreateAGather.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
    }

    //Start Date: Define a fragment which will help us display a start date picker dialog.
    //Default is current date
    public static class StartDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use Gather's default date or last picked date as the default date in the picker
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
            startYear = year;
            startMonth = month;
            startDay = day;
            ((CreateAGather)getActivity()).displayStartDate();
        }
    }

    //Start date: Show the start-date picker dialog when the button is pressed
    public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new StartDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
        displayStartDate();
    }

    //Start Time: Define a fragment which will help us display a start time picker dialog.
    public static class StartTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use Gather's default time or last picked time as the default time in the picker
            final Calendar c = Calendar.getInstance();
            int hour = startHour;
            int minute = startMinute;

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
            startHour = hourOfDay;
            startMinute = minute;
            ((CreateAGather)getActivity()).displayStartTime();
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
            // Use Gather's default date or last picked date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = endYear;
            int month = endMonth;
            int day = endDay;

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
            ((CreateAGather)getActivity()).displayEndDate();
        }
    }

    //End date: Show the end-date picker dialog when the button is pressed
    public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new EndDatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    //End date: Define a fragment which will help us display an end time picker dialog.
    public static class EndTimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = endHour;
            int minute = endMinute;

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            System.out.println(hourOfDay);
            System.out.println(minute);
            endHour = hourOfDay;
            endMinute = minute;
            ((CreateAGather)getActivity()).displayEndTime();
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
        intent.putStringArrayListExtra(s.ALREADY_PICKED,numbers);
        startActivityForResult(intent, PICK_CONTACTS);
    }

    //Pick the picture by going to the picture picker page
    public void addPicture(View view){
        Intent intent= new Intent(this, ImageUpload.class);
        startActivityForResult(intent, PICK_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACTS && data != null) {
            System.out.println("PICK CONTACTS");
            numbers = data.getStringArrayListExtra("numbers");
            System.out.println(numbers);
        }
        if (requestCode == PICK_PICTURE && data != null) {
            System.out.println("PICK PICTURE");
            imageFilePath = data.getStringExtra("file");
            System.out.println(imageFilePath);
        }
    }

    //Manually add a contact to the list
//    public void addAContact(View view){
//        EditText txtphoneNo = (EditText) findViewById(R.id.guests);
//        String numberString = txtphoneNo.getText().toString();
//        System.out.println("I AM DEBUGGING!!!!!");
//        //numbers = numberString;
////        numbers = "7137756018+7137756019";
//        numbers.add(numberString);
//        System.out.println(numberString);
//    }

    //Make the gather!
    public void makeGather(View v){
//        Button myButton = (Button) findViewById(R.id.create_button);
//        myButton.setEnabled(false);
        s.o("MAKE GATHER PRESSED");
        //get number(s) input
        int first = 0;
        for (String phoneNo : numbers) {
            if (!phoneNo.equals(User.getInstance().getNumber())) {
                if (first == 0) {
                    allNumbersString = phoneNo;
                    first = 1;
                } else {
                    allNumbersString = allNumbersString + "+" + phoneNo;
                }
            }
        }
        System.out.println("ALL NUMBERS IS: " + allNumbersString);

        //get address and convert it to lat/lng
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

        //get description
        EditText txtDescription = (EditText) findViewById(R.id.gather_description);
        description = txtDescription.getText().toString();
        System.out.println(description);

        //convert date and time to proper format
        //Date & Time format = 2015-11-18 01:34:23.360332. Manually add 0.00 for s and ms

        //picker increments Months at 0 so have to add 1 before it's passed back
        int startDisplayMonth = startMonth + 1;
        int endDisplayMonth = endMonth + 1;

        startString = appendZeros(startYear,4)+"-"+appendZeros(startDisplayMonth,2)+"-"+appendZeros(startDay,2)+" "+appendZeros(startHour,2)+":"+appendZeros(startMinute,2)+":" + "0.00";
        endString = appendZeros(endYear,4)+"-"+appendZeros(endDisplayMonth,2)+"-"+appendZeros(endDay,2)+" "+appendZeros(endHour,2)+":"+appendZeros(endMinute,2)+":" + "0.00";

        //Check that dates are valid (not yet complete)
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        try {
            Date startDate = format.parse(startString);
            System.out.println(startDate);
            Date endDate = format.parse(endString);
            System.out.println(endDate);

            if(startDate.after(endDate)){
                s.o("start date after end date");
                Toast.makeText(getApplicationContext(), "Error: start date is after end date.", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println(startString);
        System.out.println(endString);

        //Get the image from the filepath
        if(!imageFilePath.equals(null)) {
            final Bitmap bitmapImage = BitmapFactory.decodeFile(imageFilePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] b = baos.toByteArray();
            encodedImage = Base64.encode(b, Base64.DEFAULT);
            String encodedImageStr = encodedImage.toString();
        }


        //Update all the guests that they have been invited to the gather via text message.
        //sendSMSMessage();

        //Send the gather data to the backend, where a gather object will be created.
        getUploadURL();
    }

    private void getUploadURL(){
        AsyncHttpClient httpClient = new AsyncHttpClient();
        String request_url="http://" + Homepage.SITE + ".appspot.com/mgetUploadURL";
        System.out.println(request_url);
        httpClient.get(request_url, new AsyncHttpResponseHandler() {
            String upload_url;

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    JSONObject jObject = new JSONObject(new String(response));

                    upload_url = jObject.getString("upload_url");
                    postToServer(upload_url);

                } catch (JSONException j) {
                    System.out.println("JSON Error");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("Get_serving_url", "There was a problem in retrieving the url : " + e.toString());
            }
        });
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
    private void postToServer(String upload_url){
        s.o("POST TO SERVER ENTERED!!!");
        RequestParams params = new RequestParams();
//        System.out.println("Check parameters");
//        System.out.println(startString);

        //String upload_url = "http://www." + Homepage.SITE + ".appspot.com/creategather";
        params.put("file",new ByteArrayInputStream(encodedImage));
        params.put("start_time",startString);
        params.put("users_invited", allNumbersString);
        s.o(allNumbersString);
        params.put("end_time", endString);
        params.put("name", title);
        params.put("gatherid", title);
        params.put("latitude", lat);
        params.put("longitude", lng);
        params.put("number", User.getInstance().getNumber());
        params.put("visibility", "private");
        params.put("description", description);


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
                System.out.println("Create_Success! Andrew debugging tag");
                Toast.makeText(context, "Gather Created Successfully!", Toast.LENGTH_SHORT).show();
//                finish();
                Intent intent = new Intent(context, MyGathers.class);

                //intent.putExtra(Homepage.NAME, title);
                startActivity(intent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                System.out.println("Create_Failure");
                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
            }
        });
    }

    //Stuff for place autocomplete
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            //CharSequence attributions = place.getAttributions();

//                mNameTextView.setText(Html.fromHtml(place.getName() + ""));
                mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
//                mIdTextView.setText(Html.fromHtml(place.getId() + ""));
//                mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));
//                mWebTextView.setText(place.getWebsiteUri() + "");
//                if (attributions != null) {
//                    mAttTextView.setText(Html.fromHtml(attributions.toString()));
//                }
            address = place.getAddress() + "";
            s.o(address);
        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    public void displayStartDate() {
        //Display the start date
        //picker increments Months at 0 so have to add 1 before it's displayed
        int displayMonth = startMonth + 1;
        TextView startDate = (TextView) findViewById(R.id.start_date_button);
        startDate.setText(displayMonth + "/" + startDay + "/" + startYear);
    }

    public void displayStartTime(){
        //Display the start date
        TextView startTime = (TextView) findViewById(R.id.start_time_button);
        startTime.setText(startHour + ":" + appendZeros(startMinute,2));
    }

    public void displayEndDate(){
        //Display the start date
        //picker increments Months at 0 so have to add 1 before it's displayed
        int displayMonth = endMonth + 1;
        TextView endDate = (TextView) findViewById(R.id.end_date_button);
        endDate.setText(displayMonth + "/" + endDay + "/" + endYear);
    }

    public void displayEndTime(){
        //Display the end date
        TextView endTime = (TextView) findViewById(R.id.end_time_button);
        endTime.setText(endHour + ":" + appendZeros(endMinute,2));
    }
}

