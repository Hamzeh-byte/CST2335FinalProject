package com.vogella.android.cst2335finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ImageButton btn_covid19 = (ImageButton)findViewById(R.id.btn_covid19);
        btn_covid19.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, .class);
//            startActivity(nextPage);
        });

        ImageButton btn_recipe = (ImageButton)findViewById(R.id.btn_recipe);
        btn_recipe.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, .class);
//            startActivity(nextPage);
        });


        ImageButton btn_ticketevent = (ImageButton)findViewById(R.id.btn_ticketevent);
        btn_ticketevent.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, .class);
//            startActivity(nextPage);
        });

        ImageButton btn_audiodb = (ImageButton)findViewById(R.id.btn_audiodb);
        btn_audiodb.setOnClickListener(bt -> {
//            Intent nextPage = new Intent(MainActivity.this, .class);
//            startActivity(nextPage);
        });
    }
}