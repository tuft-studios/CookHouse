package com.atta.cookhouse.newaddress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.atta.cookhouse.MapsActivity;
import com.atta.cookhouse.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NewAddressActivity extends AppCompatActivity  implements OnMapReadyCallback{

    String district, street, lat, lon;

    float latitude, longitude;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = new Intent(NewAddressActivity.this, MapsActivity.class);
        startActivityForResult(intent,1);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                if (!data.getBooleanExtra("error", false)){
                    district = data.getStringExtra("district");
                    street = data.getStringExtra("street");
                    lat = data.getStringExtra("latitude");
                    latitude = Float.parseFloat(lat);
                    lon = data.getStringExtra("longitude");
                    longitude = Float.parseFloat(lon);
                    //addressText.setText(data.getStringExtra("address"));

                    Toast.makeText(NewAddressActivity.this, district, Toast.LENGTH_LONG).show();
                    if (lat != null & lon != null ){
                        LatLng myLatLng = new LatLng(latitude, longitude);

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16.0f));
                    }
                }else {

                    String errorMessage = data.getStringExtra("error_message");
                    Toast.makeText(NewAddressActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(NewAddressActivity.this, "fill your data manually", Toast.LENGTH_LONG).show();


                LatLng myLatLng = new LatLng(29.960426, 31.257656);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 16.0f));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (lat != null & lon != null ){
            LatLng myLatLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(myLatLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        }else {

            LatLng myLatLng = new LatLng(29.960426, 31.257656);
            mMap.addMarker(new MarkerOptions().position(myLatLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        }
    }
}
