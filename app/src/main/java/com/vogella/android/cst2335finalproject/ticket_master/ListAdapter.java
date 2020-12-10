package com.vogella.android.cst2335finalproject.ticket_master;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vogella.android.cst2335finalproject.R;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ListAdapter extends ArrayAdapter<EventModel> {

    public ListAdapter(@NonNull Context context, ArrayList<EventModel> resource) {
        super(context,0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        EventModel eventModel = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_event_layout, parent, false);
        }

        TextView textName = convertView.findViewById(R.id.textName);

        //setting title
        textName.setText(eventModel.getName());



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), EventInfoActivity.class);

                //flag to check if this record is from internet or from database
                if(eventModel.getId() == 0)
                {
                    i.putExtra("isFromNet","yes");
                }else
                {
                    i.putExtra("isFromNet","no");
                }

                //adding additional info to pass onto info activity
                i.putExtra("id",eventModel.getId());
                i.putExtra("name", eventModel.getName());
                i.putExtra("priceRange", eventModel.getPriceRange());
                i.putExtra("startingDate", eventModel.getStartingDate());
                i.putExtra("bannerUrl",eventModel.getBannerUrl());
                i.putExtra("url",eventModel.getUrl());
                //must add flag
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

}
