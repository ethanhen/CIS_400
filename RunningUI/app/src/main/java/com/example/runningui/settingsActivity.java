package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class settingsActivity extends AppCompatActivity {

    private String mb = "false";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Switch s = (Switch) findViewById(R.id.bpmswitch);

        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mb = "true";
                } else {
                    mb = "false";
                }
            }
        });

        Button savebutton = (Button) findViewById(R.id.save);
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settingsActivity.this, MainActivity.class);
                EditText mEdit   = (EditText)findViewById(R.id.typedbpm);
                String t = mEdit.getText().toString();
                intent.putExtra("state", (String) mb);
                intent.putExtra("bpm", t);
                startActivity(intent);
            }
        });

    }


}

