package protoko.com.protoko;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    private LocationRequest mLocationRequest;
    private Marker mCurrLocationMarker;
    private Button btRute;
    private Button btRute_Hide;
    private String namaToko;
    private String alamatToko;
    private String lokasi = "";
    private String id = "";
    private double longitude;
    private double latitude;
    private LatLng lokasiNow;
    private LatLng toko;
    private double lat;
    private double longt;
    private boolean zoomToko = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGoogleAPIClient();
        checkPermissions();

        Intent i = getIntent();
        Bundle bd = i.getExtras();
        namaToko = (String) bd.get("namaToko");
        alamatToko = (String) bd.get("alamatToko");
        id = (String) bd.get("id");
        lokasi = (String) bd.get("lokasi");
        bd.remove("namaToko");
        bd.remove("alamatToko");
        bd.remove("id");
        bd.remove("lokasi");
        if(id.contains("daftar")) {
            setContentView(R.layout.activity_maps_pasang);
            setTitle("Tandai Lokasi");
        }else{
            String[] lok = lokasi.split(",");
            lat = Double.parseDouble(lok[0]);
            longt = Double.parseDouble(lok[1]);
            setContentView(R.layout.activity_maps);
            setTitle("Lokasi "+namaToko);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(!id.contains("daftar")) {
            toko = new LatLng(lat, longt);
            MarkerOptions marker = new MarkerOptions().position(toko).title(namaToko).snippet(alamatToko).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mMap.addMarker(marker);
            moveToCurrentLocation(toko);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    private void initGoogleAPIClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapsActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MapsActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        break;
                    case RESULT_CANCELED:
                        break;
                }
                break;
        }
    }

    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                }

            }
        }
    };

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };


    private void drawDirection(LatLng pos1, LatLng pos2){
        MarkerOptions marker = new MarkerOptions().position(toko).title(namaToko).snippet(alamatToko).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(marker);
        GoogleDirection.withServerKey("AIzaSyCpBAiRILdPC5Ibk1jl6Kn7Yst3wufdLt0")
                .from(pos1)
                .to(pos2)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                        PolylineOptions polylineOptions = DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.RED);
                        mMap.addPolyline(polylineOptions);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                    }
                });
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private void zoomAll(LatLng pos1, LatLng pos2){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pos1);
        builder.include(pos2);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        lokasiNow = new LatLng(location.getLatitude(), location.getLongitude());
        latitude = lokasiNow.latitude;
        longitude = lokasiNow.longitude;
        if(!id.contains("daftar")) {
            zoomAll(toko, lokasiNow);
        }else{
            moveToCurrentLocation(lokasiNow);
        }
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                }
            }
        }
    }

    private void clearMap(LatLng toko){
        mMap.clear();
        MarkerOptions marker = new MarkerOptions().position(toko).title(namaToko).snippet(alamatToko).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(marker);
    }

    public void klikRute(View view) {
        btRute = (Button)findViewById(R.id.btRute);
        btRute_Hide = (Button)findViewById(R.id.btRute_Hide);
        if(lokasiNow != null) {
            drawDirection(lokasiNow, toko);
            btRute.setVisibility(View.GONE);
            btRute_Hide.setVisibility(View.VISIBLE);
        }
    }

    public void klikToko(View view)
    {
        if(zoomToko){
            if(lokasiNow != null) {
                zoomAll(toko, lokasiNow);
                zoomToko = false;
            }else{
                moveToCurrentLocation(toko);
            }
        }else{
            moveToCurrentLocation(toko);
            zoomToko = true;
        }

    }

    public void klikRuteHapus(View view) {
        btRute = (Button)findViewById(R.id.btRute);
        btRute_Hide = (Button)findViewById(R.id.btRute_Hide);
        clearMap(toko);
        btRute_Hide.setVisibility(View.GONE);
        btRute.setVisibility(View.VISIBLE);
    }

    public void pasangLokasi(View view) {
        try {
            Intent semua = new Intent(this, SignupActivity.class);
            String lokasi = String.valueOf(lokasiNow.latitude) + "," + String.valueOf(lokasiNow.longitude);
            semua.putExtra("lokasi", lokasi);
            startActivity(semua);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }
}