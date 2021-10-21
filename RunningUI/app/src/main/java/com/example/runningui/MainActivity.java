package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.gesture.GestureLibrary;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Button searchbutton;
    private Button librarybutton;
    private Button settingsbutton;
    private Button mapsbutton;
    private LinearLayout Bottombat;
    private WebView wb;
    private TextView txt_acceleration;
    private TextView txt_accely;
    private TextView txt_accelz;
    private TextView txt_bpm;
    private ProgressBar progressBar;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPrevValue;
    private double changeIAccel;
    public double noy;
    public int lasti;

    public long[][][] stepdata = new long[5][10000][1];

    public long[][] timearray = new long[5][10000];
    public int array = 0;
    public int a = 0;

    public int[] i = new int[5];
    public int[] finali = new int[5];

    private SensorEventListener sensorEventListner = new SensorEventListener() {

    public float[] ydata = new float[]{0,0,0,0,0};
    public long[] tdata = new long[]{0,0,0,0,0};
    public int counter = 0;

        //Print the accelerometer values
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Calendar calendar = Calendar.getInstance();
            long time = (long) calendar.getTimeInMillis();
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float ycorrected = y - (float)9.8;
            accelerationCurrentValue = Math.sqrt((x*x) + (y*y) + (z*z));
            changeIAccel = Math.abs(accelerationCurrentValue - accelerationPrevValue);
            accelerationPrevValue = accelerationCurrentValue;
            txt_acceleration.setText("X  = " + String.format("%.3g%n", x));
            txt_accely.setText("Y = " + String.format("%.3g%n", y));
            txt_accelz.setText("Z = " + String.format("%.3g%n", x));
            progressBar.setProgress((int)changeIAccel);

            float tempdata =  y - (float)9;
            long temptime = time;


            if(ydata[counter] < tempdata){
             //   txt_bpm.setText(String.valueOf(1));
                ydata[counter] = tempdata;
                tdata[counter] = temptime;

            }
            else if (ydata[counter] >= tempdata) {
               // txt_bpm.setText(String.valueOf(2));
                counter = counter + 1;
            }
            if(counter == 4){
                float time2 = tdata[4] - tdata[0];
                float bpm = (5/ time2)*60;
                txt_bpm.setText(String.valueOf(tdata[0]));
                counter = 0;
            }


    /*        stepdata[array][i[a]][0] = (long)y;
            timearray[array][i[a]] = time;
            if (i[a] > 0) {
                if (y < stepdata[array][i[a] - 1][0]) {
                    if (array < 4) {
                        array = array + 1;
                    } else {
                        array = 0;
                        a = 0;
                        long timedif = time - timearray[0][i[0]];
                        txt_bpm.setText(String.valueOf(timedif));
                        i[0] = 0;
                        i[1] = 0;
                        i[2] = 0;
                        i[3] = 0;
                        i[4] = 0;
                    }

                }
            }
            i[a] = i[a] + 1;
            if(i[a] == 9999)
                i[a] = 0;
*/
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    //main section that stuff is declarted in
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout li = (LinearLayout )findViewById(R.id.linearLayout);
      //  li.setBackgroundColor(Color.parseColor("#5e29e8"));
        WebView wb = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wb.setWebViewClient(new Callback());
        wb.loadUrl("http://maps.google.com");

        txt_acceleration= (TextView) findViewById(R.id.txt_accel);
        txt_accely = (TextView) findViewById(R.id.txt_accely);
        txt_accelz = (TextView) findViewById(R.id.txt_accelz);
        txt_bpm = (TextView) findViewById(R.id.bpm);

        progressBar = (ProgressBar) findViewById(R.id.progressaccel);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        //Ui button listners

        searchbutton = (Button) findViewById(R.id.searchbutton);
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivitySearch();
            }
        });
        librarybutton = (Button) findViewById(R.id.librarybutton);
        librarybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityLibrary();
            }
        });

        settingsbutton = (Button) findViewById(R.id.settingsbutton);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivitySettings();
            }
        });
        mapsbutton = (Button) findViewById(R.id.mapsbutton);
        mapsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivityMaps();
            }
        });




    }
    //page openers
    public void openActivitySearch(){

        Intent intent = new Intent(this, SearchAcrivity.class);
        startActivity(intent);
    }
    public void openActivityLibrary(){

        Intent intent = new Intent(this, libraryActivity.class);
        startActivity(intent);
    }
    public void openActivitySettings(){

        Intent intent = new Intent(this, settingsActivity.class);
        startActivity(intent);
    }
    public void openActivityMaps(){

        Intent intent = new Intent(this,MapsActivity.class);
        startActivity(intent);
    }

    //allows the webclient to work
    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return false;
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListner, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListner);
    }


}