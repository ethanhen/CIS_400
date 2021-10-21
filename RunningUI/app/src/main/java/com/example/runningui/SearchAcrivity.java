package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SearchAcrivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_acrivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
}