package com.vogella.android.cst2335finalproject.ticket_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vogella.android.cst2335finalproject.R;

import java.util.ArrayList;

public class SavedEventsActivity extends AppCompatActivity {

    private ListView listviewEvents;
    private ListAdapter adapter;
    private EventsDB database;
    private ArrayList<EventModel> list;
    private TextView textNoFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.toolbar_saved);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listviewEvents = findViewById(R.id.listviewEvents);

        textNoFound = findViewById(R.id.textNoFound);

        database = new EventsDB(this);

        list = database.selectAllEvents();

        loadListView(list);
    }

    //onresume callback
    @Override
    protected void onResume() {
        super.onResume();
        list = database.selectAllEvents();
        loadListView(list);
    }

    //load list view method
    private void loadListView(ArrayList<EventModel> list) {
        if(list.size() == 0)
        {
            Toast.makeText(this, getString(R.string.msg_no_saved_item), Toast.LENGTH_SHORT).show();
            textNoFound.setVisibility(View.VISIBLE);
            listviewEvents.setVisibility(View.GONE);
            adapter.clear();
        }else {
            adapter = new ListAdapter(this, list);

            listviewEvents.setAdapter(adapter);
            textNoFound.setVisibility(View.GONE);
            listviewEvents.setVisibility(View.VISIBLE);
        }
    }

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

                String info = getString(R.string.help_saved_activity);

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

}