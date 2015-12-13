package com.aptmini.jreacs.connexus;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Jo on 12/11/2015.
 */
public class BasicActivity extends ActionBarActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                s.o("Clicked search");
                search();
                return true;
            case R.id.action_my_gathers:
                myGathers();
                s.o("Clicked my gathers");
                return true;
            case R.id.action_my_squads:
                s.o("Clicked my squads");
                return true;
            case R.id.action_whats_happening:
                s.o("Clicked whats happening");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void myGathers()
    {
        Intent i = new Intent(this, MyGathers.class);
        startActivity(i);
    }

    public void search()
    {
        Intent i = new Intent(this, Search.class);
        startActivity(i);
    }
}
