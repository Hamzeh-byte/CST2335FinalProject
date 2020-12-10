package com.vogella.android.cst2335finalproject.ticket_master;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vogella.android.cst2335finalproject.R;

public class EventInfoActivity extends AppCompatActivity {

    ImageView imageBanner;
    TextView textURL,textPriceRange,textStartingDate,textName;
    Button buttonDB;
    EventsDB database;
    Bundle dataFromAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.info_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageBanner= findViewById(R.id.imageBanner);
        textURL = findViewById(R.id.textURL);
        textPriceRange = findViewById(R.id.textPriceRange);
        textStartingDate = findViewById(R.id.textStartingDate);
        textName = findViewById(R.id.textName);
        buttonDB = findViewById(R.id.buttonDB);

        database = new EventsDB(this);

        dataFromAdapter = getIntent().getExtras();

        String isFromNet = dataFromAdapter.getString("isFromNet");

        //loading image from url using glide library
        Glide.with(this).load(dataFromAdapter.getString("bannerUrl")).into(imageBanner);

        textURL.setText(dataFromAdapter.getString("url"));
        textName.setText(dataFromAdapter.getString("name"));
        textPriceRange.setText(dataFromAdapter.getString("priceRange"));
        textStartingDate.setText(dataFromAdapter.getString("startingDate"));

        //if record from internet then it can be saved to database
        if (isFromNet.equalsIgnoreCase("yes"))
        {
            buttonDB.setText("Save Event");
            buttonDB.setOnClickListener(v -> {
                EventModel event = new EventModel(
                        dataFromAdapter.getString("name"),
                        dataFromAdapter.getString("startingDate"),
                        dataFromAdapter.getString("priceRange"),
                        dataFromAdapter.getString("url"),
                        dataFromAdapter.getString("bannerUrl"));

                //returns -1 if db fails to insert the data
                long id = database.insertEvent(event);

                if(id == -1)
                {
                    Toast.makeText(EventInfoActivity.this, getString(R.string.msg_stored_event), Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(EventInfoActivity.this, "Event: " + event.getName() + " has been saved in favorites", Toast.LENGTH_SHORT).show();
                }

                buttonDB.setEnabled(false);
            });
            //otherwise, we already inserted it to database, so remove on click on buttonDb
        }else if(isFromNet.equalsIgnoreCase("no"))
        {
            buttonDB.setText("Delete Event");
            buttonDB.setOnClickListener(v -> {
                if(database.deleteEvent(dataFromAdapter.getLong("id"))){
                    Toast.makeText(EventInfoActivity.this, getString(R.string.msg_deleted), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
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

                String info = getString(R.string.help_info_activity);

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