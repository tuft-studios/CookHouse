package com.atta.cookhouse;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atta.cookhouse.model.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener  {

    private GoogleMap mMap;

    String errorMessage;

    // Session Manager Class
    SessionManager session;

    private static final int FINE_LOCATION_REQUEST_CODE = 101;
    private static final int COARSE_LOCATION_REQUEST_CODE = 102;

    LatLng myLatLng, placeLatLngLoc;

    Button confirmButton, skipBtn;

    Place myPlace;

    boolean placeSelected;

    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    FINE_LOCATION_REQUEST_CODE);
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                    COARSE_LOCATION_REQUEST_CODE);
            return;
        }

        confirmButton = findViewById(R.id.confirm_trip);
        confirmButton.setOnClickListener(this);
        skipBtn = findViewById(R.id.skip_maps);
        skipBtn.setOnClickListener(this);

        placeAutocompleteFragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //placeAutocompleteFragment.setFilter(new AutocompleteFilter.Builder().setCountry("ID").build());

        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                myLatLng = place.getLatLng();
                myPlace = place;
                placeSelected = true;
                if(marker!=null){
                    marker.remove();
                }

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLngLoc, 16.0f));

                placeLatLngLoc = mMap.getCameraPosition().target;
                //marker = mMap.addMarker(new MarkerOptions().position(latLngLoc).title(place.getName().toString()));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MapsActivity.this, ""+status.toString(), Toast.LENGTH_SHORT).show();
                Log.i("error",status.toString());
            }
        });
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
            return;
        }
        buildGoogleApiClient();

        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //get latlng at the center by calling
                myLatLng = mMap.getCameraPosition().target;
                if (placeLatLngLoc != null){

                    double distanceInMetersOne = distance(myLatLng.latitude, myLatLng.longitude,
                            placeLatLngLoc.latitude, placeLatLngLoc.longitude);

                    if(distanceInMetersOne >= .5){
                        placeSelected = false;
                    }
                }

            }
        });
    }


    protected void requestPermission(String permissionType, int requestCode) {
        int permission = ContextCompat.checkSelfPermission(this,
                permissionType);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permissionType}, requestCode
            );
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));


        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public List<Address> getAddress(LatLng latLng){

        List<Address> addresses = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0){
                String address = addresses.get(0).getAddressLine(0);
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String destrict = addresses.get(0).getLocality();
                String knownName = addresses.get(0).getFeatureName();
                String city = addresses.get(0).getSubAdminArea();
                //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_SHORT).show();
            }

        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service not available";
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid location used";
        }

        return addresses;


    }

    @Override
    public void onClick(View view) {
        if (view == confirmButton) {

/*
            List<Address> addresses;
            String street, district;


            if (!placeSelected){

                addresses = getAddress(myLatLng);
            }else
                addresses = getAddress(placeLatLngLoc);

            if (addresses == null || addresses.size()  == 0) {

                intent.putExtra("error", true);
                if (errorMessage.isEmpty()) {
                    errorMessage = "no address found";
                }

                intent.putExtra("error_message", errorMessage);
            } else {
                Address address = addresses.get(0);
                street = address.getThoroughfare();

                if (!placeSelected){
                    if (address.getSubAdminArea() != null) {


                        district = address.getSubAdminArea();

                    }else {

                        district = address.getAdminArea();

                    }
                }else {
                    district = String.valueOf(myPlace.getName());
                }



                intent.putExtra("district", district);
                intent.putExtra("street", street);
                String lat = String.valueOf(myLatLng.latitude);
                intent.putExtra("latitude", lat);
                String lon = String.valueOf(myLatLng.longitude);
                intent.putExtra("longitude", lon);

            }*/


            Intent intent = new Intent();

            intent.putExtra("error_message", errorMessage);

            String lat = String.valueOf(myLatLng.latitude);
            intent.putExtra("latitude", lat);
            String lon = String.valueOf(myLatLng.longitude);
            intent.putExtra("longitude", lon);


            setResult(Activity.RESULT_OK, intent);
            finish();
        }else if (view == skipBtn){

            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) *
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist; // output distance, in MILES
    }
}
