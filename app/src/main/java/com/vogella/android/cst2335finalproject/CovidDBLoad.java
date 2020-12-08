package com.vogella.android.cst2335finalproject;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * The purpose of this class is to load previously saved data whenever the load icon is clicked
 * @author Kris Charbonneau
 * @version 1.0
 */
public class CovidDBLoad extends AppCompatActivity {

    private ArrayList<CovidSavedData> covidSavedData = new ArrayList<>();
    MyListAdapter adt;
    ListView myList;
    SQLiteDatabase db;
    public static final String SAVE_ID = "SAVE_ID";
    private static final String CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS covid_t(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, COUNTRY VARCHAR(50), DATE VARCHAR(50), " +
            "PROVINCE VARCHAR(50), CASES INT, SAVE_ID VARCHAR(50));");

    /**
     * This is where the activity is initialized. Here, setContentView is called with a layout defining
     * the UI for the activity and findViewById is called on our widgets that we want to manipulate.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_db_load);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.covidSavedResults));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myList = findViewById(R.id.covidLoadedList);
        adt = new MyListAdapter();
        myList.setAdapter(adt);
        db = openOrCreateDatabase("CovidDB", MODE_PRIVATE, null);
        db.execSQL(CREATE_TABLE);

        Cursor result = db.rawQuery("SELECT DISTINCT SAVE_ID, COUNTRY, DATE FROM covid_t", null);


        if(result != null) {
            result.moveToFirst();

            while(result.moveToNext()) {
                String savedID = result.getString(0);
                String savedCountry = result.getString(1);
                String savedDate = result.getString(2);

                covidSavedData.add(new CovidSavedData(savedID, savedCountry, savedDate));
            }
            adt.notifyDataSetChanged();
        }

        myList.setOnItemClickListener((theList, item, pos, id) -> {
            CovidSavedData sd = covidSavedData.get(pos);

            Bundle dataToPass = new Bundle();
            dataToPass.putString(SAVE_ID, sd.getId());

            Intent nextActivity = new Intent(this, CovidDBSave.class);
            nextActivity.putExtras(dataToPass);
            startActivity(nextActivity);
        });
    }

    /**
     * This is a utility class for the ListView in this class only
     * @author Kris Charbonneau
     * @version 1.0
     */
    class MyListAdapter extends BaseAdapter {

        /**
         * The purpose of this method is to get the list's size
         * @return An integer corresponding to the list's size
         */
        @Override
        public int getCount() {
            return covidSavedData.size();
        }

        /**
         * The purpose of this method is to return the CovidSavedData object at the selected index
         * @param position , the index of the item in the list
         * @return A CovidSavedData object at selected position
         */
        @Override
        public CovidSavedData getItem(int position) {
            return covidSavedData.get(position);
        }

        /**
         * The purpose of this method is to return the index of the object
         * @param position , the index of the item in the list
         * @return A long value corresponding to the position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * The purpose of this method is to create and return a GUI representation for our list object
         * @param position , the index of the item in the list
         * @param convertView , the old View
         * @param parent , the parent View
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = convertView;

            newView = inflater.inflate(R.layout.covid_saved_listview_layout, parent, false);

            CovidSavedData sd = getItem(position);
            String savedID = sd.getId();
            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

            String savedDate = sdf.format(new Date(Long.parseLong(savedID)));
            TextView tvID = newView.findViewById(R.id.savedIDTxtView);
            tvID.setText("Data was saved at: " + savedDate + " ");

            String country = sd.getCountry();
            TextView tvProv = newView.findViewById(R.id.savedCountryTxtView);
            tvProv.setText(country);

            String date = sd.getDate();
            TextView tvCases = newView.findViewById(R.id.savedDateTxtView);
            tvCases.setText("Date" + date);

            return newView;
        }
    }
}