package com.googlemapactivity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class GeoCoderActivity extends FragmentActivity {

    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_coder);
        

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                // Getting the Latitude and Longitude of the touched location
                latLng = arg0;

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Creating a marker
                markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

                // Adding Marker on the touched location with address
                new ReverseGeocodingTask(getBaseContext()).execute(latLng);

            }
        });
    }

    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        Context mContext;

        public ReverseGeocodingTask(Context context){
            super();
            mContext = context;
        }

        // Finding address using reverse geocoding
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(mContext);
            double latitude = params[0].latitude;
            double longitude = params[0].longitude;

            List<Address> addresses = null;
            String addressText="5455454";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                addressText = String.format("%s, %s, %s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getSubLocality(),
                        address.getAdminArea(),
                        address.getCountryName(),
                        address.getPostalCode());
            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            TextView textView = (TextView) findViewById(R.id.textView);
            // Setting the title for the marker.
            // This will be displayed on taping the marker
            markerOptions.title(addressText);
            textView.setText(addressText);

            // Placing a marker on the touched position
            googleMap.addMarker(markerOptions);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}