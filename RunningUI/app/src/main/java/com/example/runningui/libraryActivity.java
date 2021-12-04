package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class libraryActivity extends AppCompatActivity {

    private String filename = "";
    private String filepath = "";
    private String filecontent = "";
    private FileReader fr;
    private TextView load;

    private Button clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        filename = "pastruns.txt";
        filepath = "MyFileDir";

        load = (TextView) findViewById(R.id.load);
        clear = (Button) findViewById(R.id.clear);

        File externalFile = new File(getExternalFilesDir(filepath), filename);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileOutputStream fos = null;
                try{
                    fos = new FileOutputStream(externalFile);
                    fos.write(filecontent.getBytes());
                } catch (FileNotFoundException e){
                    e.printStackTrace();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });



        StringBuilder stringBuilder = new StringBuilder();

       // File externalFile = new File(getExternalFilesDir(filepath), filename);
        try {
            fr = new FileReader(externalFile);
            BufferedReader bf = new BufferedReader(fr);
            String line = bf.readLine();
            while(line != null){
                stringBuilder.append(line).append('\n');
                line = bf.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            String filecontents = stringBuilder.toString();
            load.setText(filecontents);
        }


    }
}