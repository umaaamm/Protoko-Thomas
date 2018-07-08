package protoko.com.protoko;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AllProductResultActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;

    public static double gpsLat, gpsLong;

    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private boolean refresh = true;
    private boolean closest = false;

    private ProgressBar pb;

    private TextView tvJumlahPromo;

    private RecyclerView.Adapter adapter ;

    private Spinner spUrut;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout tbUrut;

    private FloatingActionButton fbAtas;

    private boolean itemSelected = false;

    private final List<PromoUpload> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);
        adapter = new RecyclerViewAdapter(getApplicationContext(), list);

        googleApiClient  = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        setUpGClient();
        pb = (ProgressBar)findViewById(R.id.pb);
        tbUrut = (LinearLayout)findViewById(R.id.tbUrut);
        fbAtas = (FloatingActionButton)findViewById(R.id.fbUp);

        tvJumlahPromo = (TextView)findViewById(R.id.tvJumlahPromo);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        recyclerView.setHasFixedSize(true);

        spUrut = (Spinner)findViewById(R.id.spUrut);

        spUrut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String itemSelect = parent.getItemAtPosition(position).toString();
                if(itemSelected) {
                    if (itemSelect.contains("Promo Terbaru")) {
                        Collections.sort(list, new Comparator<PromoUpload>() {
                            @Override
                            public int compare(PromoUpload o1, PromoUpload o2) {
                                Long id1 = Long.valueOf(o1.getId());
                                Long id2 = Long.valueOf(o2.getId());
                                return id1.compareTo(id2);
                            }
                        });
                        adapter.notifyDataSetChanged();
                        closest = false;
                    }
                }
                    if (itemSelect.contains("Promo Terlama")) {
                        Collections.sort(list, new Comparator<PromoUpload>() {
                            @Override
                            public int compare(PromoUpload o1, PromoUpload o2) {
                                Long id1 = Long.valueOf(o1.getId());
                                Long id2 = Long.valueOf(o2.getId());
                                return id2.compareTo(id1);
                            }
                        });
                        adapter.notifyDataSetChanged();
                        itemSelected = true;
                        closest = false;
                    } else if (itemSelect.contains("Rendah ke Tinggi")) {
                        Collections.sort(list, new Comparator<PromoUpload>() {
                            @Override
                            public int compare(PromoUpload o1, PromoUpload o2) {
                                return o1.getHargaBaru() - o2.getHargaBaru();
                            }
                        });
                        adapter.notifyDataSetChanged();
                        itemSelected = true;
                        closest = false;
                    } else if (itemSelect.contains("Tinggi ke Rendah")) {
                        Collections.sort(list, new Comparator<PromoUpload>() {
                            @Override
                            public int compare(PromoUpload o1, PromoUpload o2) {
                                return o2.getHargaBaru() - o1.getHargaBaru();
                            }
                        });
                        adapter.notifyDataSetChanged();
                        itemSelected = true;
                        closest = false;
                    } else if (itemSelect.contains("Jarak Terdekat")) {
                        Collections.sort(list, new Comparator<PromoUpload>() {
                            @Override
                            public int compare(PromoUpload o1, PromoUpload o2) {
                                int sComp = o1.getMeter() - o2.getMeter();
                                if(sComp!=0){
                                    return sComp;
                                }else {
                                    Long id1 = Long.valueOf(o1.getId());
                                    Long id2 = Long.valueOf(o2.getId());
                                    return id1.compareTo(id2);
                                }
                            }
                        });
                        adapter.notifyDataSetChanged();
                        itemSelected = true;
                        closest = true;
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(AllProductResultActivity.this, 2));

        databaseReference = FirebaseDatabase.getInstance().getReference(UploadActivity.Database_Path);

        databaseReference.orderByChild("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    i++;
                    tvJumlahPromo = (TextView)findViewById(R.id.tvJumlahPromo);
                    PromoUpload promoUpload = postSnapshot.getValue(PromoUpload.class);
                    long durasi = TimeUnit.DAYS.toMillis(Integer.parseInt(promoUpload.getDurasi()));
                    String time = promoUpload.getTimeStamp();
                    long dif = (Long.parseLong(time) + durasi) - System.currentTimeMillis();

                    if((dif > 0)&&(refresh)) {
                        list.add(promoUpload);
                        tvJumlahPromo.setText(list.size()+ " Promo");
                    }
                    if(i == dataSnapshot.getChildrenCount()){
                        refresh = false;
                    }
                }

                adapter = new RecyclerViewAdapter(getApplicationContext(), list);
                pb.setVisibility(View.GONE);
                tbUrut.setVisibility(View.VISIBLE);
                fbAtas.setVisibility(View.VISIBLE);
                try {
                    recyclerView.setAdapter(adapter);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                refresh = false;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void ScrollAtas(View view) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getBaseContext()){
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(0);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    private void refreshItems() {
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        try {
            adapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            gpsLat = mylocation.getLatitude();
            gpsLong = mylocation.getLongitude();
            if(adapter.getItemCount()!=0) {
                if(closest){
                    Collections.sort(list, new Comparator<PromoUpload>() {
                        @Override
                        public int compare(PromoUpload o1, PromoUpload o2) {
                            int sComp = o1.getMeter() - o2.getMeter();
                            if(sComp!=0){
                                return sComp;
                            }else {
                                Long id1 = Long.valueOf(o1.getId());
                                Long id2 = Long.valueOf(o2.getId());
                                return id1.compareTo(id2);
                            }
                        }
                    });
                }
                try {
                    adapter.notifyDataSetChanged();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void getMyLocation(){
        if(googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(AllProductResultActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(AllProductResultActivity.this,
                                                    android.Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        status.startResolutionForResult(AllProductResultActivity.this,REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException ignored) {

                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(AllProductResultActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(AllProductResultActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }
}