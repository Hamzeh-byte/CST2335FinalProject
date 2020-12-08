package com.vogella.android.cst2335finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * The purpose of the class is to create the fragment used to display location data
 * @author Kris Charbonneau
 * @version 1.0
 */
public class CovidDetailsFragment extends Fragment {


    private Bundle dataFromActivity;
    private String details;
    private AppCompatActivity parentActivity;


    /**
     *This method creates and returns the view hierarchy associated with the fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return A view with whatever widgets were added
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        details = dataFromActivity.getString(CovidSearchResult.ITEM_DETAILS);


        View result = inflater.inflate(R.layout.fragment_covid_details, container, false);

        TextView msg = result.findViewById(R.id.covidMessageHere);
        msg.setText(details);

        Button hideBtn = result.findViewById(R.id.covidHide);
        hideBtn.setOnClickListener( c -> {
            deleteFragment();
            if (parentActivity instanceof CovidEmptyActivity) parentActivity.finish();
        });


        return result;
    }

    /**
     * This method is called once the fragment is associated with an activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be CovidSearchResult for a tablet, or CovidEmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }

    /**
     * Utility method for deleting a fragment
     * @author Kris Charbonneau
     * @Version 1.0
     */
    public void deleteFragment(){
        parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}