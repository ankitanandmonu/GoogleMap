package com.googlemapactivity;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentLocation extends FragmentActivity implements LocationListener {

    GoogleMap googleMap;
    GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);


        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
        gpsTracker = new GpsTracker(CurrentLocation.this);
        if (gpsTracker.canGetLocation()) {
            double latitudes = gpsTracker.getLatitude();
            double longitudes = gpsTracker.getLongitude();
            String ankit = String.valueOf(latitudes);
            String anand = String.valueOf(longitudes);

            Log.v("Location", "latitude" + latitudes + "," + "longitude" + longitudes);

            String cityName = "N/A";

            if (!ankit.equals("0.0") && !anand.equals(0.0)) {
                Geocoder gcd = new Geocoder(CurrentLocation.this,
                        Locale.getDefault());
                List<Address> addresses;
                try {
                    Log.v("Within", "Try");
                    addresses = gcd.getFromLocation(latitudes, longitudes, 1);
                    Log.v("addresses", "" + addresses.toString());
                    if (addresses.size() > 0)
                        System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();

                    Log.v("cityName", "" + cityName);
                } catch (IOException e) {
                    Log.v("Within", "Catch");

                    e.printStackTrace();
                }
                TextView tvLocation = (TextView) findViewById(R.id.tv_location);

                // Getting latitude of the current location


                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitudes, longitudes);

                // Showing the current location in Google Map
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                googleMap.addMarker(new MarkerOptions().position(latLng).title(cityName));

                // Setting latitude and longitude in the TextView tv_location
                tvLocation.setText("Latitude:" +  latitudes  + ", Longitude:"+ longitudes );
            }
        } else {
            gpsTracker.showSettingsAlert();
        }

    }
    public void next(View view){
        startActivity(new Intent(getApplicationContext(),GeoCoderActivity.class));
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
