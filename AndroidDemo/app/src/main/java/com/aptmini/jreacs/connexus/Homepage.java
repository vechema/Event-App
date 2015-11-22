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
        System.out.println("Debugging start");

        //Get number of user
        String number = "7137756018";

        //Set the user singleton's number
        User.getInstance().setNumber(number);

        //Create the URL
        final String request_url = "http://www.apt2015final.appspot.com/login?number=" + number;
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
                        Intent intent = new Intent(context, CreateAGather.class);
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
            final String request_url = "http://www.apt2015final.appspot.com/signup?number=" + User.getInstance().getNumber() + "&name=" + username;
            System.out.println(request_url);

            AsyncHttpClient httpClient = new AsyncHttpClient();
            httpClient.get(request_url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    try {
                        System.out.println("success");
                        JSONObject jObject = new JSONObject(new String(response));

                        String result = jObject.getString("result");
                        System.out.println(result);

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
}



//    @Override
//    public void onResume(){
//        super.onResume();
//
//        //Setting the location
//        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (location == null){
//            Params.longitude = 0;
//            Params.latitude = 0;
//
//        } else {
//            Params.longitude = location.getLongitude();
//            Params.latitude = location.getLatitude();
//        }
//
//        System.out.println("********************");
//        System.out.println("Lng: " + Params.longitude);
//        System.out.println("Lat: " + Params.latitude);
//        System.out.println("********************");
//    }
//
//    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
//        // Find the TextView that is inside of the SignInButton and set its text
//        for (int i = 0; i < signInButton.getChildCount(); i++) {
//            View v = signInButton.getChildAt(i);
//
//            if (v instanceof TextView) {
//                TextView tv = (TextView) v;
//                tv.setText(buttonText);
//                tv.setTextSize(16);
//                return;
//            }
//        }
//    }
//
//    private GoogleApiClient buildGoogleApiClient() {
//        // When we build the GoogleApiClient we specify where connected and
//        // connection failed callbacks should be returned, which Google APIs our
//        // app uses and which OAuth 2.0 scopes our app requests.
//        return new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .build();
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(SAVED_PROGRESS, mSignInProgress);
//    }
//
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }
//
//    @Override
//    public void onClick(View v) {
//        if (!mGoogleApiClient.isConnecting()) {
//            // We only process button clicks when GoogleApiClient is not transitioning
//            // between connected and not connected.
//            if (!isOnline()) {
//                Toast.makeText(getApplicationContext(),
//                        "You need internet access to perform this action.", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            switch (v.getId()) {
//                case R.id.sign_in_button:
//                    resolveSignInError();
//                    break;
//                case R.id.sign_out_button:
//                    // We clear the default account on sign out so that Google Play
//                    // services will not return an onConnected callback without user
//                    // interaction.
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                    mGoogleApiClient.disconnect();
//                    mGoogleApiClient.connect();
//                    email = null;
//                    Toast.makeText(getApplicationContext(), "You are now signed out", Toast.LENGTH_SHORT).show();
//                    login_msg_shown = false;
//                    break;
//                case R.id.revoke_access_button:
//                    // After we revoke permissions for the user with a GoogleApiClient
//                    // instance, we must discard it and create a new one.
//                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//                    // Our sample has caches no user data from Google+, however we
//                    // would normally register a callback on revokeAccessAndDisconnect
//                    // to delete user data so that we comply with Google developer
//                    // policies.
//                    Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient);
//                    mGoogleApiClient = buildGoogleApiClient();
//                    mGoogleApiClient.connect();
//                    email = null;
//                    Toast.makeText(getApplicationContext(), "You've just revoked Connexus to access your basic account info.", Toast.LENGTH_LONG).show();
//                    login_msg_shown = false;
//                    break;
//            }
//        }
//    }
//
//    ImageView imageView = null;
//    private static boolean login_msg_shown = false;
//    public static String email = null;
//
//    /* onConnected is called when our Activity successfully connects to Google
//     * Play services.  onConnected indicates that an account was selected on the
//     * device, that the selected account has granted any requested permissions to
//     * our app and that we were able to establish a service connection to Google
//     * Play services.
//     */
//    @Override
//    public void onConnected(Bundle connectionHint) {
//        // Reaching onConnected means we consider the user signed in.
//        Log.i(TAG, "onConnected");
//
//        // Update the user interface to reflect that the user is signed in.
//        mSignInButton.setEnabled(false);
//        mSignOutButton.setEnabled(true);
//        mRevokeButton.setEnabled(true);
//
//        // Retrieve some profile information to personalize our app for the user.
//        final Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//        email = Plus.AccountApi.getAccountName(mGoogleApiClient);
//        System.out.println(email);
//        // Indicate that the sign in process is complete.
//        mSignInProgress = STATE_DEFAULT;
//
//
//        mStatus.setText(email + " is currently Signed In");
//
//        /*Button uploadButton = (Button) findViewById(R.id.open_image_upload_page);
//        uploadButton.setClickable(true);
//
//        uploadButton.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent= new Intent(context, ImageUpload.class);
//                        startActivity(intent);
//                    }
//                }
//        );*/
//    }
//
//    /* onConnectionFailed is called when our Activity could not connect to Google
//     * Play services.  onConnectionFailed indicates that the user needs to select
//     * an account, grant permissions or resolve an error in order to sign in.
//     */
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        // Refer to the javadoc for ConnectionResult to see what error codes might
//        // be returned in onConnectionFailed.
//
////        System.out.println("you have no connection");
//
////        TextView morePicsText= (TextView) findViewById(R.id.view_all_streams);
////        morePicsText.setVisibility(View.INVISIBLE);
//
//        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
//                + result.getErrorCode());
//
//        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
//            // An API requested for GoogleApiClient is not available. The device's current
//            // configuration might not be supported with the requested API or a required component
//            // may not be installed, such as the Android Wear application. You may need to use a
//            // second GoogleApiClient to manage the application's optional APIs.
//        } else if (mSignInProgress != STATE_IN_PROGRESS) {
//            // We do not have an intent in progress so we should store the latest
//            // error resolution intent for use when the sign in button is clicked.
//            mSignInIntent = result.getResolution();
//            mSignInError = result.getErrorCode();
//
//            if (mSignInProgress == STATE_SIGN_IN) {
//                // STATE_SIGN_IN indicates the user already clicked the sign in button
//                // so we should continue processing errors until the user is signed in
//                // or they click cancel.
//                resolveSignInError();
//            }
//        }
//
//        // In this sample we consider the user signed out whenever they do not have
//        // a connection to Google Play services.
//        onSignedOut();
//    }
//
//    /* Starts an appropriate intent or dialog for user interaction to resolve
//     * the current error preventing the user from being signed in.  This could
//     * be a dialog allowing the user to select an account, an activity allowing
//     * the user to consent to the permissions being requested by your app, a
//     * setting to enable device networking, etc.
//     */
//    private void resolveSignInError() {
//        if (mSignInIntent != null) {
//            // We have an intent which will allow our user to sign in or
//            // resolve an error.  For example if the user needs to
//            // select an account to sign in with, or if they need to consent
//            // to the permissions your app is requesting.
//
//            try {
//                // Send the pending intent that we stored on the most recent
//                // OnConnectionFailed callback.  This will allow the user to
//                // resolve the error currently preventing our connection to
//                // Google Play services.
//                mSignInProgress = STATE_IN_PROGRESS;
//                startIntentSenderForResult(mSignInIntent.getIntentSender(),
//                        RC_SIGN_IN, null, 0, 0, 0);
//            } catch (SendIntentException e) {
//                Log.i(TAG, "Sign in intent could not be sent: "
//                        + e.getLocalizedMessage());
//                // The intent was canceled before it was sent.  Attempt to connect to
//                // get an updated ConnectionResult.
//                mSignInProgress = STATE_SIGN_IN;
//                mGoogleApiClient.connect();
//            }
//        } else {
//            // Google Play services wasn't able to provide an intent for some
//            // error types, so we show the default Google Play services error
//            // dialog which may still start an intent on our behalf if the
//            // user can resolve the issue.
//            //showDialog(DIALOG_PLAY_SERVICES_ERROR);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        switch (requestCode) {
//            case RC_SIGN_IN:
//                if (resultCode == RESULT_OK) {
//                    // If the error resolution was successful we should continue
//                    // processing errors.
//                    mSignInProgress = STATE_SIGN_IN;
//                } else {
//                    // If the error resolution was not successful or the user canceled,
//                    // we should stop processing errors.
//                    mSignInProgress = STATE_DEFAULT;
//                }
//
//                if (!mGoogleApiClient.isConnecting()) {
//                    // If Google Play services resolved the issue with a dialog then
//                    // onStart is not called so we need to re-attempt connection here.
//                    mGoogleApiClient.connect();
//                }
//                break;
//        }
//    }
//
//    private void onSignedOut() {
//        // Update the UI to reflect that the user is signed out.
//        mSignInButton.setEnabled(true);
//        mSignOutButton.setEnabled(false);
//        mRevokeButton.setEnabled(false);
//
//        mStatus.setText("Signed out");
//        /*Button uploadButton = (Button) findViewById(R.id.open_image_upload_page);
//        uploadButton.setClickable(false);*/
//        email = null;
//
//        if (imageView != null) {
//            ((ViewGroup) imageView.getParent()).removeView(imageView);
//            imageView = null;
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        // The connection to Google Play services was lost for some reason.
//        // We call connect() to attempt to re-establish the connection or get a
//        // ConnectionResult that we can attempt to resolve.
//        mGoogleApiClient.connect();
//    }
//
//    public void viewAllImages(View view){
//        Intent intent= new Intent(this, DisplayImages.class);
//        startActivity(intent);
//    }
//
//    public void viewAllStreams(View view) {
//        Intent intent = new Intent(this, DisplayStreams.class);
//        startActivity(intent);
//    }
//
//    public void Testing(View view){
//        Intent intent= new Intent(this, Test.class);
//
//        startActivity(intent);
//    }
//
//    public void offlineUpload(View view) {
//        Intent intent = new Intent(this, OfflineUpload.class);
////        startActivity(intent);
//        startActivityForResult(intent, 1);
//    }
//
//    public void PostOfflinePhotos(){
//        int count = 0;
//        for (OfflinePhoto photo:OfflineUpload.offlinephotos){
//            getUploadURL(photo.encodedImage, photo.photoCaption, photo.lat, photo.lng, photo.stream_name);
//            count = count + 1;
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        OfflineUpload.offlinephotos.clear();
//        System.out.println(count);
//    }
//    private void getUploadURL(final byte[] encodedImage, final String photoCaption, final double lat, final double lng, final String stream_name){
//        AsyncHttpClient httpClient = new AsyncHttpClient();
//        String request_url="http://apt2015mini.appspot.com/mgetUploadURL";
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
//                    postToServer(encodedImage, photoCaption, upload_url, lat, lng, stream_name);
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
//
//    private void postToServer(byte[] encodedImage,String photoCaption, String upload_url, double lat, double lng, String stream_name){
//        System.out.println(upload_url);
//        RequestParams params = new RequestParams();
//        params.put("file",new ByteArrayInputStream(encodedImage));
//        params.put("photoCaption",photoCaption);
//        params.put("latitude", lat);
//        params.put("longitude", lng);
//        params.put("stream_name", stream_name);
//        System.out.println("STREAM NAME in post: " + stream_name);
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.post(upload_url, params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
//                Log.w("async", "success!!!!");
//                Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show();
////                finish();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
//                Log.e("Posting_to_blob", "There was a problem in retrieving the url : " + e.toString());
//            }
//        });
//    }
//}