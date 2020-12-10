package com.vogella.android.cst2335finalproject.ticket_master;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class JsonParser {

    ArrayList<EventModel> listEvents;

    //getting json response as a string
    public String getResponse(String apiURL) {
        String response = null;
        try {
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertInputStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e("JsonParser", "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e("JsonParser", "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e("JsonParser", "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e("JsonParser", "Exception: " + e.getMessage());
        }
        return response;
    }

    //converting input stream into string
    private String convertInputStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }


    //parsing json and converting it to list of objects
    public ArrayList<EventModel> getListEventsFromJson(String url) {

        listEvents = new ArrayList<>();

        String jsonStr = getResponse(url);

        if (jsonStr != null) {
            try {
                JSONObject root = new JSONObject(jsonStr);
                if(!root.has("_embedded"))
                {
                    return listEvents;
                }
                JSONObject _embedded = root.getJSONObject("_embedded");
                JSONArray events = _embedded.getJSONArray("events");

                // saving All present Events into list
                for (int i = 0; i < events.length(); i++) {
                    JSONObject c = events.getJSONObject(i);

                    //getting url of event
                    String url_event = c.getString("url");
                    //getting image banner url
                    String banner_url = c.getJSONArray("images").getJSONObject(0).getString("url");
                    String name =c.getString("name");
                    //getting starting date of the event
                    String startingDate = c.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    //getting price range if available
                    StringBuilder stringBuilder= new StringBuilder();
                    stringBuilder.append("min: ");
                    if(c.has("priceRanges")) {
                        JSONArray priceRanges = c.getJSONArray("priceRanges");
                        JSONObject price = priceRanges.getJSONObject(0);
                        stringBuilder.append("$"+price.getDouble("min"));
                        stringBuilder.append(" max: ");
                        stringBuilder.append("$ "+price.getDouble("max"));
                    }else
                    {
                        stringBuilder.append("price range is not available");
                    }
                    String priceRange = stringBuilder.toString();

                    EventModel event = new EventModel(0,name,startingDate,priceRange,url_event,banner_url);
                    listEvents.add(event);

                }

                return listEvents;

            } catch (JSONException e) {
            }

        }

        return null;

    }


}
