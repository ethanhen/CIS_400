package com.example.runningui;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.runningui.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int TIME = 5;
    private static final int ACCESS_PERMISSION = 99;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    LocationRequest locationRequest;
    private Button lbutton;
    double[][] points = new double[2][500];
    LatLng[] places = new LatLng[500];
    int runlog = 0;
    private Button path;
    private Context context;
    private String filename = "";
    private String filepath = "";
    private String filecontent = "";
    private String fc = "";
    private String cords = "";
    private FileReader fr;



    FusedLocationProviderClient fusedLocationProviderClient;

    LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationRequest = LocationRequest.create();

        // when do we want to update
        locationRequest.setInterval(30000);

        // how often do we want to update when maximum power
        locationRequest.setFastestInterval(1000 * TIME);

        // do we want to make it highly accurate
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        filename = "pastruns.txt";
        filepath = "MyFileDir";


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lbutton = (Button) findViewById(R.id.lbutton);
        lbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });

        path = (Button) findViewById(R.id.drawpath);
        path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PolylineOptions polylineOptions = new PolylineOptions().add(places[0]);
                cords = "";
                for (int i = 1; i < runlog; i++){
                    polylineOptions.add(places[i]);
                    cords = cords + String.valueOf(points[0][i]) + "," + String.valueOf(points[1][i]);
                }
                filecontent = cords;
               // Polyline rout = new PolylineOptions()(
                //.add(places[0]));
                Polyline polyline = mMap.addPolyline(polylineOptions);

                filename = "pastruns.txt";
                filepath = "MyFileDir";

                StringBuilder stringBuilder = new StringBuilder();

                File externalFile = new File(getExternalFilesDir(filepath), filename);
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
                    filecontent = filecontent + (filecontents);
                }
                //File externalFile = new File(getExternalFilesDir(filepath), filename);
                FileOutputStream fos = null;
                try{
                    fos = new FileOutputStream(externalFile);
                    DateFormat df = new SimpleDateFormat("d MMM yyyy HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    String filecontents;
                    fc = "\n" + date + ": \n" + filecontent;
                    fos.write(fc.getBytes());
                } catch (FileNotFoundException e){
                    e.printStackTrace();

                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                updateValues(location);
            }
        };


    }

    private void startTracking() {

       // fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        updateGPS();
        }

        private void updateGPS()
        {
            // get permission from the user to access GPS
            // get the last location from the fused client
            // update the UI to display the location coordinates
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);

            // check if the permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                //get the last location retrieved
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //yay..got the permission, now display
                        updateValues(location);

                    }
                });

            }
            else
            {
                // if permission is not granted, check for build variant
                // if it is over 23, ask for user permission at runtime
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_PERMISSION);
                }
            }
        }

        private void updateValues(Location location) {
            LatLng test = new LatLng(location.getLatitude(),location.getLongitude());
            points[0][runlog] = location.getLatitude();
            points[1][runlog] = location.getLongitude();
            places[runlog] = test;
            runlog++;
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(test).title(String.valueOf(runlog)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(test));

        }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(43.0481, -76.1474);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Syracuse"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}