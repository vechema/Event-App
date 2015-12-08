package com.aptmini.jreacs.connexus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.Set;


public class PickContacts extends ActionBarActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    String DEBUG_TAG = "pick contacts debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Bundle extras = data.getExtras();
//                    Set keys = extras.keySet();
//                    Iterator iterate = keys.iterator();
//                    while (iterate.hasNext()) {
//                        String key = iterate.next();
//                        Log.v(DEBUG_TAG, key + "[" + extras.get(key) + "]");
//                    }
                    Uri result = data.getData();
                    Log.v(DEBUG_TAG, "Got a result: "
                            + result.toString());
                    // get the contact id from the Uri
                    String id = result.getLastPathSegment();

                    // query for everything email
//                    cursor = getContentResolver().query(
//                            Email.CONTENT_URI, null,
//                            Email.CONTACT_ID + "=?",
//                            new String[]{id}, null);
//                    break;
            }

        } else {
            // gracefully handle failure
            Log.w("pickContacts", "Warning: activity result not ok");
        }
    }

}
