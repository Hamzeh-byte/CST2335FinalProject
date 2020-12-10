package com.vogella.android.cst2335finalproject.ticket_master;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class EventsDB extends SQLiteOpenHelper {

    public static int DB_VERSION = 1;
    public static final String DB_NAME = "ticket_master_db";

    public static final String events = "favEvents";
    public static final String id = "id";
    public static final String name = "name";
    public static final String startingDate = "startingDate";
    public static final String priceRange = "priceRange";
    public static final String url = "url";
    public static final String bannerUrl = "bannerUrl";

    public static final String queryGetAllEvents = "SELECT * FROM "+ events +" ORDER BY "+id+" ASC";


    //constructor for EventsDB with context
    public EventsDB(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //creating table
        String query = "Create table "+ events +" ( "+ id +" INTEGER PRIMARY KEY AUTOINCREMENT,"+ name + " VARCHAR2(100) UNIQUE, "+ startingDate
                +" VARCHAR2(100),"+priceRange+" VARCHAR2(100) ,"+ url + " VARCHAR2(100),"+ bannerUrl +" VARCHAR2(100) );";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //do nothing as we don't have anything to update inside database
    }

    public long insertEvent(EventModel event){

        ContentValues values = new ContentValues();

        values.put(name, event.getName());
        values.put(startingDate, event.getStartingDate());
        values.put(priceRange, event.getPriceRange());
        values.put(url, event.getUrl());
        values.put(bannerUrl, event.getBannerUrl());

        SQLiteDatabase db = this.getWritableDatabase();

        return db.insert(events, null, values);
    }

    public boolean deleteEvent(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "id=?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        int result= db.delete(events, whereClause, whereArgs);

        if(result == 0)
        {
            return false;
        }else
        {
            return true;
        }
    }

    public ArrayList<EventModel> selectAllEvents(){

        ArrayList<EventModel> favoriteEvents = new ArrayList<EventModel>();


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(queryGetAllEvents, null);

        if (cursor.moveToFirst()) {
            do {

                Long id= Long.parseLong(cursor.getString(cursor.getColumnIndex(EventsDB.id)));
                String name= cursor.getString(cursor.getColumnIndex(EventsDB.name));
                String startingDate = cursor.getString(cursor.getColumnIndex(EventsDB.startingDate));
                String priceRange = cursor.getString(cursor.getColumnIndex(EventsDB.priceRange));
                String url = cursor.getString(cursor.getColumnIndex(EventsDB.url));
                String BannerUrl = cursor.getString(cursor.getColumnIndex(bannerUrl));

                favoriteEvents.add(new EventModel(id,name,startingDate,priceRange,url,BannerUrl));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteEvents;
    }
}
