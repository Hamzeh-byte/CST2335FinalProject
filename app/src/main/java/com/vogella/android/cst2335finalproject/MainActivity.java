package com.vogella.android.cst2335finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button covidButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        covidButton = findViewById(R.id.covidButton);
        Intent goToCovid = new Intent(this, CovidMain.class);

        covidButton.setOnClickListener(click -> startActivity(goToCovid));

    }
}