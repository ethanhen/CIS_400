package com.example.runningui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.gesture.GestureLibrary;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.*;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    public FusedLocationProviderClient fusedLocationClient;
    private Button searchbutton;
    private Button librarybutton;
    private Button settingsbutton;
    private Button mapsbutton;
    private LinearLayout Bottombat;
    private Button playbutton;
    private WebView wb;
    private TextView txt_acceleration;
    private TextView txt_accely;
    private TextView txt_accelz;
    private TextView txt_bpm;
    private TextView txt_manualbpm;
    private ProgressBar progressBar;
    private Button Manuallbpm;
    private int mbpm;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue;
    private double accelerationPrevValue;
    private double changeIAccel;
    public double noy;
    public int lasti;
    private double abpm = 0;
    public long cavg = 0;
    private long last, now, diff, entries, sum = 0;
    int kek = 0;

    public long[][][] stepdata = new long[5][10000][1];

    public long[][] timearray = new long[5][10000];
    public int array = 0;
    public int a = 0;
    public float davg;

    public int[] i = new int[5];
    public int[] finali = new int[5];
    public long[] bpmtimes = new long[500];
    public int bpmplace = 0;
    public String[] spotify = new String[200];

    private SensorEventListener sensorEventListner = new SensorEventListener() {

        public float[] ydata = new float[]{0, 0, 0, 0, 0};
        public long[] tdata = new long[]{0, 0, 0, 0, 0};
        public int counter = 0;

        //Print the accelerometer values
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Calendar calendar = Calendar.getInstance();
            long time = (long) calendar.getTimeInMillis();
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float ycorrected = y - (float) 9.8;
            accelerationCurrentValue = Math.sqrt((x * x) + (y * y) + (z * z));
            changeIAccel = Math.abs(accelerationCurrentValue - accelerationPrevValue);
            accelerationPrevValue = accelerationCurrentValue;
            txt_acceleration.setText("X  = " + String.format("%.3g%n", x));
            txt_accely.setText("Y = " + String.format("%.3g%n", y));
            txt_accelz.setText("Z = " + String.format("%.3g%n", x));
            progressBar.setProgress((int) changeIAccel);

            float tempdata = y - (float) 9;
            long temptime = time;


            if (ydata[counter] < tempdata) {
                //   txt_bpm.setText(String.valueOf(1));
                ydata[counter] = tempdata;
                tdata[counter] = temptime;

            } else if (ydata[counter] >= tempdata) {
                // txt_bpm.setText(String.valueOf(2));
                counter = counter + 1;
            }
            if (counter == 4) {
                float time2 = tdata[4] - tdata[0];
                float bpm = (5 / time2) * 60;
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

        LinearLayout li = (LinearLayout) findViewById(R.id.linearLayout);
        //  li.setBackgroundColor(Color.parseColor("#5e29e8"));
        WebView wb = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = wb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wb.setWebViewClient(new Callback());
        wb.loadUrl("http://maps.google.com");

        txt_acceleration = (TextView) findViewById(R.id.txt_accel);
        txt_accely = (TextView) findViewById(R.id.txt_accely);
        txt_accelz = (TextView) findViewById(R.id.txt_accelz);
        txt_bpm = (TextView) findViewById(R.id.bpm);

        progressBar = (ProgressBar) findViewById(R.id.progressaccel);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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
        playbutton = (Button) findViewById(R.id.playbutton);
        playbutton.setOnClickListener(new View.OnClickListener() {

            //spotify array
            @Override
            public void onClick(View view) {
                spotify[50] = "https://open.spotify.com/playlist/65gJV5cMxgngB65vL8Ogry?si=531a17e34da3426c";
                spotify[51] = "https://open.spotify.com/playlist/4BijKLyCb5IDorZcKZz1Yg?si=2e8da6e9a8124550";
                spotify[52] = "https://open.spotify.com/playlist/4uH1ajYhtn8vZO1OEeKTxY?si=269f1d694a914d6c";
                spotify[53] = "https://open.spotify.com/playlist/193ipXVYL44kZ4LUkCiw5z?si=31fc61cef6724cae";
                spotify[54] = "https://open.spotify.com/playlist/1vDuAKpQbgFKX7kmQwBvp8?si=9c73643346414821";
                spotify[55] = "https://open.spotify.com/playlist/78xhWQze22Ru1NK3SAzK8f?si=21d34c80e5bb4834";
                spotify[56] = "https://open.spotify.com/playlist/2630yOes1ilHnLvQEQ3H3l?si=ab80219545f94f03";
                spotify[57] = "https://open.spotify.com/playlist/6sZqPZHfurivbxtST59Egj?si=6fde15d392fe433e";
                spotify[58] = "https://open.spotify.com/playlist/6SpjCUQyI6W7vWtvBARWMF?si=458fca792fac4e78";
                spotify[59] = "https://open.spotify.com/playlist/0aITx1lIgkkt03XUnfPUrt?si=7352ea3bd8d147a3";
                spotify[60] = "https://open.spotify.com/playlist/0aITx1lIgkkt03XUnfPUrt?si=7352ea3bd8d147a3";
                spotify[61] = "https://open.spotify.com/playlist/68flIyATDBfiBQFntmNkKp?si=6e9f57b0f6034ac6";
                spotify[62] = "https://open.spotify.com/playlist/68flIyATDBfiBQFntmNkKp?si=6e9f57b0f6034ac6";
                spotify[63] = "https://open.spotify.com/playlist/68flIyATDBfiBQFntmNkKp?si=6e9f57b0f6034ac6";
                spotify[64] = "https://open.spotify.com/playlist/68flIyATDBfiBQFntmNkKp?si=6e9f57b0f6034ac6";
                spotify[65] = "https://open.spotify.com/playlist/68flIyATDBfiBQFntmNkKp?si=6e9f57b0f6034ac6";
                spotify[66] = "https://open.spotify.com/playlist/0ajcZuQQK536fWbsdy8s6y?si=8683dbde36d54de9";
                spotify[67] = "https://open.spotify.com/playlist/1DsWvPjSFQwRpP1WSDXd2U?si=af608c2a5e5d4d9b";
                spotify[68] = "https://open.spotify.com/playlist/50HRmTSW3pHbZM8Em0NF0G?si=08123e893d9946c4";
                spotify[69] = "https://open.spotify.com/playlist/2bNqY0rwu35bzIVnAdLssl?si=4d9832cca7844a0e";
                spotify[70] = "https://open.spotify.com/playlist/1HEv0eWyF2zEnfRgTfGABY?si=6238db478b104ddd";
                spotify[71] = "https://open.spotify.com/playlist/65ZJ2rTPh7IvbR7GCBzVfA?si=53d1b67e96f74858";
                spotify[72] = "https://open.spotify.com/playlist/65ZJ2rTPh7IvbR7GCBzVfA?si=53d1b67e96f74858";
                spotify[73] = "https://open.spotify.com/playlist/65ZJ2rTPh7IvbR7GCBzVfA?si=53d1b67e96f74858";
                spotify[74] = "https://open.spotify.com/playlist/65ZJ2rTPh7IvbR7GCBzVfA?si=53d1b67e96f74858";
                spotify[75] = "https://open.spotify.com/playlist/65ZJ2rTPh7IvbR7GCBzVfA?si=53d1b67e96f74858";
                spotify[76] = "https://open.spotify.com/playlist/608mdyD6W7yiuV3voZnAfK?si=c7251296b8ed426d";
                spotify[77] = "https://open.spotify.com/playlist/0o2QZqJqwgfCYP2RYbdBhp?si=a041ce1f8ebf4bdf";
                spotify[78] = "https://open.spotify.com/playlist/6tIEA7F9Dam4cG9sGkjKPK?si=0c0ed162cad94eff";
                spotify[79] = "https://open.spotify.com/playlist/4zF5SxUc41DzhobxPBAIXI?si=c1ec7287c0af43cd";
                spotify[80] = "https://open.spotify.com/playlist/4zF5SxUc41DzhobxPBAIXI?si=c1ec7287c0af43cd";
                spotify[81] = "https://open.spotify.com/playlist/7nH6If3XrqbLZwHcj3GSih?si=4c5beec8a1424b1f";
                spotify[82] = "https://open.spotify.com/playlist/68UHBpxVlhp95RSvIjSK47?si=778d6a39800c4edb";
                spotify[83] = "https://open.spotify.com/playlist/11MIVXfEoNZkKJi8UwQw56?si=b743dd7b912a41c7";
                spotify[84] = "https://open.spotify.com/playlist/5tpzWmuWWPlEqnJRHkTCk5?si=7d2e5937f9334d26";
                spotify[85] = "https://open.spotify.com/playlist/66HanNpKb8OmdnRseO4JYL?si=fdd86ab2a3184faf";
                spotify[86] = "https://open.spotify.com/playlist/6BxEVuG0Zqmp80SJiyBeQx?si=2b13b936830e4c57";
                spotify[87] = "https://open.spotify.com/playlist/69z1ZXsO3UuXl39fm3ioFW?si=2a2ec177f0be40ef";
                spotify[88] = "https://open.spotify.com/playlist/0YePWMxDzSexOAYUH2MDRP?si=8945d63c9f1a4352";
                spotify[89] = "https://open.spotify.com/playlist/6pvQSSdyNNHUDiVv99QRgg?si=fbdc8e176f814897";
                spotify[90] = "https://open.spotify.com/playlist/5wfZlt9SqK0YukbmcslAdK?si=e8dfc6e8f2b1476a";
                spotify[91] = "https://open.spotify.com/playlist/4DzD3XKFsE9B8fBRiBo4By?si=633f60835b7a437d";
                spotify[92] = "https://open.spotify.com/playlist/4zv2ILMFkYLLImInyteZNl?si=fdeb0618b1824c81";
                spotify[93] = "https://open.spotify.com/playlist/76USDVEUS8cCYP5YETu2EX?si=b715548442f94cd2";
                spotify[94] = "https://open.spotify.com/playlist/685fdLPyWMGp2Sb7p7WEJB?si=667c1bd536a34339";
                spotify[95] = "https://open.spotify.com/playlist/36uZkPOqVxSom4gSSqS5Wc?si=415cb7c14ee24120";
                spotify[96] = "https://open.spotify.com/playlist/57B7CBCnU1dftMlfWR00Pr?si=41ab90b07b024f2d";
                spotify[97] = "https://open.spotify.com/playlist/6NKvnGv3GfEPwoM9TxM6qK?si=3716d4e45cfe4b46";
                spotify[98] = "https://open.spotify.com/playlist/2EiIXcUDF61AUHTrezdef1?si=dbd6eaae4e044395";
                spotify[99] = "https://open.spotify.com/playlist/1G4aflzKBcwZ7uKXVe0gSq?si=c656feb378b4447b";
                spotify[100] = "https://open.spotify.com/playlist/5JpANhLlGcgZcLFcrNhL7j?si=667857f321d34c60";
                spotify[101] = "https://open.spotify.com/playlist/3BnwyzaXEB1VGpAhcZ6Mz2?si=ca2bf3886b774b37";
                spotify[102] = "https://open.spotify.com/playlist/3XWE4HsrOjtqFIbCcIOa30?si=ac8badd89b284ba4";
                spotify[103] = "https://open.spotify.com/playlist/5efuqQTxE1N5GGz1IJ9ZiK?si=8e751f23e1f14fe8";
                spotify[104] = "https://open.spotify.com/playlist/2XUF39xL3FDHWPMAZq7113?si=4177a26b39ff44fa";
                spotify[105] = "https://open.spotify.com/playlist/78SAn3Cur8C15PpAiny4iH?si=0f9cf1db14bb4efb";
                spotify[106] = "https://open.spotify.com/playlist/0EEsMCWc85qZoXqL4b7w82?si=304b9ea251f64673";
                spotify[107] = "https://open.spotify.com/playlist/68zrI7yhJyg58ZT8PnM11e?si=228003dcb8df4450";
                spotify[108] = "https://open.spotify.com/playlist/1J8lAlC9Dkw0P5tUx1xTdp?si=3c6ffd0d2b4847a1";
                spotify[109] = "https://open.spotify.com/playlist/3J49qgQJELelSoVffwxaAM?si=7e4c8eff3bcc43f0";
                spotify[110] = "https://open.spotify.com/playlist/2pX7htNxQUGZSObonznRyn?si=ddd12eeb37064f27";
                spotify[111] = "https://open.spotify.com/playlist/4bBtI2sOjWgHlchI16larn?si=caf2bb9774954fed";
                spotify[112] = "https://open.spotify.com/playlist/3Cp9vKD5usCecCfkAoDQof?si=451ff7e509cb4b5f";
                spotify[113] = "https://open.spotify.com/playlist/1Ur4XPxvftB0I1Pv96T1YC?si=c7f74c9ef7e24e05";
                spotify[114] = "https://open.spotify.com/playlist/1v619l74UZWox3QqQomJeS?si=8fcb854522a242b2";
                spotify[115] = "https://open.spotify.com/playlist/78qmqXAefQPCbQ5JqfwWgz?si=53f458cac997488d";
                spotify[116] = "https://open.spotify.com/playlist/04mDOLMwaYQ7Ioi35ntIRt?si=3da3eada6d0149ac";
                spotify[117] = "https://open.spotify.com/playlist/0XxHG3hZkoTDq6089zTkYY?si=96629124824845cf";
                spotify[118] = "https://open.spotify.com/playlist/5mFTEpLie2khN91pXerfEL?si=c9b820e55b26404b";
                spotify[119] = "https://open.spotify.com/playlist/0wMHR2zXYL557p5APMdhYJ?si=017584a89d204f0d";
                spotify[120] = "https://open.spotify.com/playlist/1vdkPd9esYFohPkUxcrUDa?si=ca79a32389df43b1";
                spotify[121] = "https://open.spotify.com/playlist/6sxJa6OeQkyTnLGsWiNtyg?si=2bfcbb050e3745f5";
                spotify[122] = "https://open.spotify.com/playlist/4d91ajgsvITPZRXAvPq9Ps?si=c717f72c40e14e5a";
                spotify[123] = "https://open.spotify.com/playlist/57VSJhskCsfCt4OvcVe9ze?si=6c8b74744d4c41e3";
                spotify[124] = "https://open.spotify.com/playlist/6K0KVXCGiceFfSufhI8iHn?si=c214676c7178456b";
                spotify[125] = "https://open.spotify.com/playlist/7gzwK6WlYXJ2Zgyaj4H72g?si=bb0cc9b8a1ed4cd6";
                spotify[126] = "https://open.spotify.com/playlist/40sEaKuAzr73yKWuOVOPK5?si=1e6023657dae4b39";
                spotify[127] = "https://open.spotify.com/playlist/3xZStfOWUnH2yATj9LJjeo?si=fcff389e244d4989";
                spotify[128] = "https://open.spotify.com/playlist/2YfLZdggxaoGYdlpNePEOR?si=edf190cbf79a4774";
                spotify[129] = "https://open.spotify.com/playlist/03vDNXeyGriyzh3kD8WIEJ?si=cc67e3f886234b04";
                spotify[130] = "https://open.spotify.com/playlist/31awQeCm5tlfFvgMvvP3d8?si=483681500c0645c3";
                spotify[131] = "https://open.spotify.com/playlist/4qsptYEBDbhBN6AtyTT3zZ?si=05a07a4d869a4a85";
                spotify[132] = "https://open.spotify.com/playlist/3d7r8CqvUlYxEtuLgF7iqm?si=097a73f6d2004aba";
                spotify[133] = "https://open.spotify.com/playlist/31awQeCm5tlfFvgMvvP3d8?si=7e29b6aa70524bc4";
                spotify[134] = "https://open.spotify.com/playlist/6VJbFJqWYrHa9Gu2ZFgONY?si=0b7bcc2cd0d54cd6";
                spotify[135] = "https://open.spotify.com/playlist/03v0mwAof1feGVGHu1neHD?si=d6a39edf15d74cf4";
                spotify[136] = "https://open.spotify.com/playlist/5B4LRi73dXJNKAviN4wIyG?si=6bb43968f85e4c94";
                spotify[137] = "https://open.spotify.com/playlist/0Ng0eAlNn0pIk8MHH5YoHV?si=678d67db57534f0f";
                spotify[138] = "https://open.spotify.com/playlist/0zZjq3ysEUn38Q3YSJvtKW?si=ab3b176fc1f74db9";
                spotify[139] = "https://open.spotify.com/playlist/0zZjq3ysEUn38Q3YSJvtKW?si=ab3b176fc1f74db9";
                spotify[140] = "https://open.spotify.com/playlist/4D6RK0n83UVNdG9GB7jVO0?si=7f5cf323c7ff4255";
                spotify[141] = "https://open.spotify.com/playlist/1BN2tPiOFUa1nJcYjOnj8D?si=607d7b25863048a7";
                spotify[142] = "https://open.spotify.com/playlist/5tXmbTfhT0Ub4TGOo5rg2R?si=e875c4fb8d094259";
                spotify[143] = "https://open.spotify.com/playlist/6wcvPqseXtmBwiz1RBAUlR?si=0932c5d92c7f4316";
                spotify[144] = "https://open.spotify.com/playlist/2U8AHdDetTffLVJt3Getpy?si=e8b786d85fbb4c78";
                spotify[145] = "https://open.spotify.com/playlist/623d39jaHt21LDcPTFKvpp?si=a13e05625af0408f";
                spotify[146] = "https://open.spotify.com/playlist/3Gy5KEBK8s1wG02tqoCSj3?si=96aa1d8402d749f8";
                spotify[147] = "https://open.spotify.com/playlist/3Gy5KEBK8s1wG02tqoCSj3?si=96aa1d8402d749f8";
                spotify[148] = "https://open.spotify.com/playlist/7wzgPWHbeZREJmh2ZrW3O3?si=442de286f9a946f3";
                spotify[149] = "https://open.spotify.com/playlist/4SoBjarrjgCQRw2TdzxpW5?si=9376726930604c78";
                spotify[150] = "https://open.spotify.com/playlist/37i9dQZF1DX0hWmn8d5pRe?si=b92ee20bdc504d90";
                spotify[151] = "https://open.spotify.com/playlist/5GruJpyX6ynDMzKNRCPfVo?si=231bcbb5978e4cbe";
                spotify[152] = "https://open.spotify.com/playlist/677CxfmrEndNLQpyHVxPij?si=9e246e0a2794493d";
                spotify[153] = "https://open.spotify.com/playlist/5WGGGKI1JI9BTTm4pf6di9?si=b8eca74291e14803";
                spotify[154] = "https://open.spotify.com/playlist/338kcf6MUBXExlErZyl9CH?si=23f5512a151444a9";
                spotify[155] = "https://open.spotify.com/playlist/2XmsyNtDTqQspYsDdhzcQm?si=aae3e32e61854e51";
                spotify[156] = "https://open.spotify.com/playlist/6lcA7HsSnoumw2sS9uoB3d?si=89370e4054b642b3";
                spotify[157] = "https://open.spotify.com/playlist/25EX0lut7fars7t4jWzYmV?si=8b7dc29154194011";
                spotify[158] = "https://open.spotify.com/playlist/2cd4Aimgk6bJznCTlJJ7BA?si=f98061a7dc9a4955";
                spotify[159] = "https://open.spotify.com/playlist/1r0RDaG9S4ePtt6wCNjz7w?si=156d364e4a604fb4";
                spotify[160] = "https://open.spotify.com/playlist/5OrCcllsLRMR8dxGGwF5fr?si=22d9c9e4bc69427e";
                spotify[161] = "https://open.spotify.com/playlist/5B5UlAeifSNWuFGG2CIxeL?si=7bdb99b236624cca";
                spotify[162] = "https://open.spotify.com/playlist/7v6gYB1qTFk1yXAHxtdGFV?si=ed2cf2f0e0d0479f";
                spotify[163] = "https://open.spotify.com/playlist/0PugMGd6bUknK3lf5GLH5F?si=a7c6a9e3075b48c7";
                spotify[164] = "https://open.spotify.com/playlist/0tqnnFGXevj2X6GxKHuCMy?si=db76b67558074c0a";
                spotify[165] = "https://open.spotify.com/playlist/63CKrKT6wpMFZNgAfXhJrC?si=70620f1fe0e04df7";
                spotify[166] = "https://open.spotify.com/playlist/5Oba1ZDJzRBpBVEy6Xxhle?si=598e3bb7890e4658";
                spotify[167] = "https://open.spotify.com/playlist/0WbmGSWD2o96HZGo00rah0?si=2a36c1386a53463c";
                spotify[168] = "https://open.spotify.com/playlist/2cd4Aimgk6bJznCTlJJ7BA?si=d740cfefd0974dfe";
                spotify[169] = "https://open.spotify.com/playlist/4P0ErOvOYd2GAXTRIHv00m?si=0caa121abc4649b9";
                spotify[170] = "https://open.spotify.com/playlist/1K1s38nBas4LZlncUsSAkw?si=11b2b8b879c241e9";
                spotify[171] = "https://open.spotify.com/playlist/5z7tu6gpMhsMkja9jCiooJ?si=b5a2273ea20040fc";
                spotify[172] = "https://open.spotify.com/playlist/1KmJIFLRsijtGyxWIaTYny?si=a4286d8c83d9417e";
                spotify[173] = "https://open.spotify.com/playlist/2VX9RDdCGCHvEOozBXg1uD?si=2ca1c1ef46d0412f";
                spotify[174] = "https://open.spotify.com/playlist/2VX9RDdCGCHvEOozBXg1uD?si=2ca1c1ef46d0412f";
                spotify[175] = "https://open.spotify.com/playlist/1K1s38nBas4LZlncUsSAkw?si=9a85659d42814595";
                spotify[176] = "";
                spotify[177] = "";
                spotify[178] = "";
                spotify[179] = "";
                spotify[180] = "";
                spotify[181] = "";
                spotify[182] = "";
                spotify[183] = "";
                spotify[184] = "";
                spotify[185] = "";
                spotify[186] = "";
                spotify[187] = "";
                spotify[188] = "";
                spotify[189] = "";
                spotify[190] = "";
                spotify[191] = "";
                spotify[192] = "";
                spotify[193] = "";
                spotify[194] = "";
                spotify[195] = "";
                spotify[196] = "";
                spotify[197] = "";
                spotify[198] = "";
                spotify[199] = "";


              // Intent check = getIntent();
            //   String state = check.getStringExtra("state");
             //  String number = check.getStringExtra("bpm");

            //   if (state == "true"){
            //       davg = Integer.parseInt(number);
            //   }

                Uri uri = Uri.parse(spotify[(int) davg]); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        Manuallbpm = (Button) findViewById(R.id.manualbpmbutton);
        Manuallbpm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                long time = (long) calendar.getTimeInMillis();
                bpmtimes[bpmplace] = time;
                now = time;

                if (kek != 0) {
                    diff = now - last;
                    last = now;
                    sum = sum + diff;
                    entries++;
                    cavg = (sum / entries);
                    davg = (60 / (float) cavg) * 1000;

                    Manuallbpm.setText("Manual BPM:" + String.valueOf((int) davg));
                } else {
                    last = now;
                }

                if (diff > 10000) {
                    diff = 0;
                    last = 0;
                    sum = 0;
                    entries = 0;
                    kek = -1;

                }
                kek++;


            }
        });


    }

    //page openers
    public void openActivitySearch() {

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