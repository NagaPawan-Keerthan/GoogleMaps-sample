package com.example.lbs;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    final private int REQUEST_COARSE_ACCESS = 123;
    boolean permissionGranted = false;
    LocationManager lm;
    LocationListener locationListener;
    private GoogleMap mMap;
    Button b1,b2;
    private TextView t,T1,T2,T3;
    private LatLng latLng;
    private EditText elat, elon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        t=(TextView) findViewById(R.id.t1);
        T1=(TextView) findViewById(R.id.t2);
        T2=(TextView) findViewById(R.id.t3);
        T3=(TextView) findViewById(R.id.t4);
        elat=(EditText) findViewById(R.id.e1);
        elon=(EditText) findViewById(R.id.e2);
        latLng = new LatLng(-34,151);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        b1=(Button) findViewById(R.id.button2);
        b2=(Button) findViewById(R.id.button3);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_ACCESS);
            return;
        }else{
            permissionGranted = true;
        }

        // continue from previous slide
        if(permissionGranted) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Add a marker in Sydney and move the camera
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title("New Marker" + 1);
                    mMap.addMarker(marker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //System.out.println(point.latitude+"---"+ point.longitude);
                }

        });
    }
    @Override
    public void onPause() {
        super.onPause(); //---remove the location listener---
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_COARSE_ACCESS);
            return;
        }else{
            permissionGranted = true;
        }
        if(permissionGranted) {
            lm.removeUpdates(locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_ACCESS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                } else {
                    permissionGranted = false;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                LatLng p = new LatLng(
                        (int) (loc.getLatitude()),
                        (int) (loc.getLongitude()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(7));
            } else {
                System.out.println("loc is null");
            }

        }
        public void onProviderDisabled(String provider) {
        }
        public void onProviderEnabled(String provider) {
        }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
        }
    }

    public void getLocation(View view){
        double latitude=latLng.latitude;
        double longitude=latLng.longitude;

        if(!(elon.getText().toString().isEmpty()) || elat.getText().toString().isEmpty()){
            latitude=Double.parseDouble(elat.getText().toString());
            longitude=Double.parseDouble(elon.getText().toString());
        latLng = new LatLng(latitude,longitude);

        }
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this,Locale.getDefault());
        String address= null;
        String city= null;
        String state= null;
        String country= null;
        String postalcode=null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            address=addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country=addresses.get(0).getCountryName();
            postalcode=addresses.get(0).getPostalCode();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in :"+address+city+state+country+postalcode));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        t.setText(address);
        t.setText(city);
        T1.setText(state);
        T2.setText(country);
        T3.setText(postalcode);
    }
}










