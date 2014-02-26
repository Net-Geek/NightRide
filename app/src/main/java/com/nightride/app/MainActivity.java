package com.nightride.app;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.net.Uri;
import android.content.res.Configuration;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;

// Activity that Displays Map, Location, and Request a Ride button to users.
public class MainActivity extends ActionBarActivity {

    Button btnRequestRide;
    Button btnLocation;

    // GPSTracker Object.
    GPSTracker gps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign map fragment (xml) to object.
        final GoogleMap mMap;
        Marker currentMarker;
        LatLng currentLatLng;

        mMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getMap();

        // Disable map zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Create class object.
        gps = new GPSTracker(MainActivity.this);

        // Check if GPS enabled.
        if (gps.canGetLocation()) {

            // Pull GPS lat + long.
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Create new LatLng from lat + long.
            LatLng latlng = new LatLng(longitude, latitude);

            // Create new marker + set position.
            Marker userLocation = mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .visible(false));

            // Update camera position + zoom to pulled GPS position.
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15.0f);
            mMap.animateCamera(yourLocation);

            // Toast Notification Lat + Long (testing and debugging purposes only).
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
        } else {
            /* Can't get location,
             GPS or Network is not enabled,
             Ask user to enable GPS/network in settings,
             set default location to center of campus.*/
            gps.showSettingsAlert();

            double latitude = 39.1297179;
            double longitude = -84.5163877;

            // Create new LatLng from lat + long.
            LatLng latlng = new LatLng(longitude, latitude);

            // Create new marker + set position.
            Marker userLocation = mMap.addMarker(
                    new MarkerOptions()
                            .position(new LatLng(latitude, longitude))
                            .visible(false));

            // Update camera position + zoom to pulled GPS position.
            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 15.0f);
            mMap.animateCamera(yourLocation);
        }

        // When the map is moved by the user.
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                /* Clear the map of markers and recenter the new marker.
                The marker is invisible and the fragment overlay creates
                a fluid view for the user.*/
                mMap.clear();
                Marker currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(position.target)
                        .visible(false)
                );

                // Toast Notification Lat + Long (testing and debugging purposes only).
                Toast.makeText(getApplicationContext(), currentMarker.getPosition() + "",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Assign Button (XML) to object.
        btnRequestRide = (Button) findViewById(R.id.request_button);

        // Request a Ride Button Listener.
        btnRequestRide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btnRequestRide.setPressed(true);

                Intent ConfirmRideIntent = new Intent(view.getContext(), ConfirmRide.class);
                startActivity(ConfirmRideIntent);

            // TODO: implement the intent for a finalization screen with reference and comment text fields
            // TODO: after finalization screen, POST user, location and finalization data to server
            }
        });

        // Assign Button (XML) to object.
        btnLocation = (Button) findViewById(R.id.locate_button);

        // Location Button Listener.
        btnLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                btnLocation.setPressed(true);

                if (gps.canGetLocation()){

                double latitude = gps.latitude;
                double longitude = gps.longitude;

                // Create new LatLng from lat + long.
                LatLng latlng = new LatLng(longitude, latitude);

                // Update camera position + zoom to pulled GPS position.
                LatLng locationCoordinate = new LatLng(latitude, longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLng(locationCoordinate);
                mMap.animateCamera(yourLocation);

            }
                else
                {
                    gps.showSettingsAlert();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // Inflate the menu; this adds items to the action bar.
        getMenuInflater().inflate(R.menu.main, menu);

        // If device is a tablet, disable phone-only features (ie. call nightride)
        if(isTablet(this)){
        menu.getItem(0).setVisible(false);
        }
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handles Options Menu Selection (User Login, Settings, About, Etc.)
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.user_login) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (id == R.id.call_night_ride) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:513-556-7433"));
            startActivity(callIntent);
        } else if (id == R.id.about_night_ride) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    // checks if devices is a tablet or a phone based on screenLayout and size
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
