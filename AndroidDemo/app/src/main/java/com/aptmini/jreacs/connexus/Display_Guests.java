package com.aptmini.jreacs.connexus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class Display_Guests extends BasicActivity {
    ArrayList<String> numbers = new ArrayList<String>();
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display__guests);
        Intent intent = getIntent();
        numbers = intent.getStringArrayListExtra(s.GUESTS);

        String guest_category = intent.getStringExtra(s.GUEST_CATEGORY);

        // Create the text view
        TextView guestText = (TextView) findViewById(R.id.GuestText);
        //textView.setTextSize(40);
        guestText.setText(guest_category);

        updateNumberGrid();
    }

    public void updateNumberGrid(){
        GridView gridview = (GridView) findViewById(R.id.gridview_numbers);
        //gridview.setAdapter(new ImageAdapter(context,coverURLs));
        gridview.setAdapter(new NumbersTextAdapter(Display_Guests.this, context, numbers));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

            }
        });
    }

}
