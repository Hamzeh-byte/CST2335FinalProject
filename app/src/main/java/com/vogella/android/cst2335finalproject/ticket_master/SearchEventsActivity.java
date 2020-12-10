package com.vogella.android.cst2335finalproject.ticket_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vogella.android.cst2335finalproject.R;

import java.util.ArrayList;

public class SearchEventsActivity extends AppCompatActivity {

    ProgressBar progressBar2;
    ListAdapter adapter;
    ListView listviewEvents;
    EditText textinputCity;
    Button buttonOpenFavoriteActivity,buttonSearch;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_events);

        //initialization
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.toolbar_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar2 = findViewById(R.id.progressBar2);
        listviewEvents= findViewById(R.id.listviewEvents);
        textinputCity= findViewById(R.id.textinputCity);
        buttonOpenFavoriteActivity = findViewById(R.id.buttonOpenFavoriteActivity);
        buttonSearch = findViewById(R.id.buttonSearch);

        sharedPreferences = getSharedPreferences("events", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //setting text to input which was previously given by user
        textinputCity.setText(sharedPreferences.getString("lastCity",""));

        //on click of button search, validate field if validated then search for events in given city
        buttonSearch.setOnClickListener(v -> {
            if(validate()){

                editor.putString("lastCity",textinputCity.getText().toString().trim());
                editor.commit();

                new EventsLoader().execute("https://app.ticketmaster.com/discovery/v2/events.json?apikey="
                       +getString(R.string.api_key)+"&city="+textinputCity.getText().toString().trim());
            }
        });

        //opening stored events activity
        buttonOpenFavoriteActivity.setOnClickListener(v -> {
            Intent intent= new Intent(SearchEventsActivity.this,SavedEventsActivity.class);
            startActivity(intent);
        });

    }

    //inflate menu to toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_menu, menu);

        return true;
    }

    //code for menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.event_help:

                String info = getString(R.string.search_help_activity);

                showDialog(info);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    //showing dialog to user
    private void showDialog(String info) {
        FragmentManager fm = getSupportFragmentManager();
        HelpDialogFragment helpDialogFragment = HelpDialogFragment.newInstance(info);
        helpDialogFragment.show(fm, "HelpDialogFragment");
    }

    //validating input given by user
    private boolean validate() {
        boolean flag = true;

        if(textinputCity.getText().toString().isEmpty())
        {
            flag = false;
            textinputCity.setError(getString(R.string.validate_input_city));
        }

        return flag;
    }

    //this async task takes string url , processes it and gives back the list of events
    class EventsLoader extends AsyncTask<String, Void, ArrayList<EventModel>> {


        ArrayList<EventModel> listEvents;

        //showing progress bar
        @Override
        protected void onPreExecute() {
            progressBar2.setVisibility(View.VISIBLE);
            listEvents = new ArrayList<>();
        }

        //fetching events
        @Override
        protected ArrayList<EventModel> doInBackground(String... strings) {
            try {
                JsonParser jp = new JsonParser();
                String url = strings[0];
                listEvents = jp.getListEventsFromJson(url);
                return listEvents;
            }catch (Exception e)
            {
               Toast.makeText(SearchEventsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
               return listEvents;
            }

        }

        //populating listview based on list
        @Override
        protected void onPostExecute(ArrayList<EventModel> list) {

            if(list== null || list.size() ==0)
            {
                Toast.makeText(SearchEventsActivity.this, getString(R.string.error_no_event_found), Toast.LENGTH_SHORT).show();
            }
            else {
                adapter = new ListAdapter(SearchEventsActivity.this, list);
                listviewEvents.setAdapter(adapter);
            }

            progressBar2.setVisibility(View.GONE);

        }

    }

}