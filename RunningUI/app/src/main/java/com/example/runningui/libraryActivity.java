package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class libraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}