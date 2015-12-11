package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;


public class PickContacts extends ActionBarActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    String DEBUG_TAG = "pick contacts debug";
    ArrayList<String> numbers = new ArrayList<String>();
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contacts);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //Go to the address book, where a contact will be picked.
    public void addContact(View view) {
        // user BoD suggests using Intent.ACTION_PICK instead of .ACTION_GET_CONTENT to avoid the chooser
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // BoD con't: CONTENT_TYPE instead of CONTENT_ITEM_TYPE
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        startActivityForResult(intent, 1);
    }

    //Once the contact is picked, handle the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                Cursor c = null;
                try {
                    c = getContentResolver().query(uri, new String[]{
                                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                                    ContactsContract.CommonDataKinds.Phone.TYPE },
                            null, null, null);

                    if (c != null && c.moveToFirst()) {
                        //Get the number and the type
                        String number = c.getString(0);
                        int type = c.getInt(1);
                        //show the number and the type
                        showSelectedNumber(type, number);
                        //take out all special characters and spaces from number
                        number = number.replaceAll("[^\\d]", "").trim();
                        //If the number is 11 characters long make it ten
                        if(number.length() ==  11){
                            number = number.substring(1,11);
                        }
                        //add the number to the result arraylist
                        addSelectedNumber(number);
                        updateNumberGrid();
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }
    }

    public void showSelectedNumber(int type, String number) {
        Toast.makeText(this, type + ": " + number, Toast.LENGTH_LONG).show();
    }

    //Add the selected number to the result arraylist
    public void addSelectedNumber(String number) {
        numbers.add(number);
    }

    public void updateNumberGrid(){
        GridView gridview = (GridView) findViewById(R.id.gridview_numbers);
        //gridview.setAdapter(new ImageAdapter(context,coverURLs));
        gridview.setAdapter(new NumbersTextAdapter(PickContacts.this, context, numbers));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                //When clicked - open up a new activity - view a single stream
                numbers.remove(position);
                updateNumberGrid();



                            /*Dialog imageDialog = new Dialog(context);
                            imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            imageDialog.setContentView(R.layout.thumbnail);
                            ImageView image = (ImageView) imageDialog.findViewById(R.id.thumbnail_IMAGEVIEW);

                            Picasso.with(context).load(coverURLs.get(position)).into(image);

                            imageDialog.show();*/
            }
        });
    }

    //Send set of numbers back to function that called it.
    public void sendBack(View view) {
        Intent intent = new Intent();
        intent.putStringArrayListExtra("numbers", numbers);
//        System.out.println(pictureFile.toString());
        setResult(RESULT_OK, intent);
//        System.out.println("button pressed");
        finish();
    }
}
