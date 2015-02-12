package kz.kamadi.passenger.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;


import kz.kamadi.passenger.R;
import kz.kamadi.passenger.map.CustomMapFragment;
import kz.kamadi.passenger.map.GoogleMapView;


import kz.kamadi.passenger.model.TaxiLocation;


import kz.kamadi.passenger.utils.GeoCoder;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {

    public static final String START = "START";
    public static final String END = "END";
    public static final String ALL = "ALL";
    public static final int REQUEST_CODE = 1;
    private GoogleMap map;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static MainActivity me;
    private GoogleMapView mapView;
    CustomMapFragment customMapFragment;
    private TextView locationTxt;
    private String startLocation;
    private ImageButton btnCurrentPosition;
    private Button btnMapAddress;
    private Marker startMarker, endMarker;
    private ImageButton mapFindButton;
    private LatLng center;
    private TaxiLocation start, end;
    private Boolean listenLocation = true;
    private Button cancel;
    private LinearLayout routeInfo;
    private TextView txtPrice, txtDistance, txtDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        initView();
        initMap();
        initButton();
        locationTxt = (TextView) findViewById(R.id.CurrentLocationText);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    private void initView() {
        routeInfo = (LinearLayout) findViewById(R.id.RouteLayout);
        txtPrice = (TextView) findViewById(R.id.RoutePriceText);
        txtDistance = (TextView) findViewById(R.id.RouteDistanceText);
        txtDuration = (TextView) findViewById(R.id.RouteDurationText);
    }

    @Override
    protected void onResume() {
//
        super.onResume();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments

    }

    public void initButton() {

        btnCurrentPosition = (ImageButton) findViewById(R.id.CurrentPositionButton);
        btnMapAddress = (Button) findViewById(R.id.MapStartPointButton);
        mapFindButton = (ImageButton) findViewById(R.id.MapFindButton);
        cancel = (Button) findViewById(R.id.MapEndPointButton);
        btnCurrentPosition.setOnClickListener(this);
        btnMapAddress.setOnClickListener(this);
        mapFindButton.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    private void initMap() {
        customMapFragment = (CustomMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = customMapFragment.getMap();
        map.getUiSettings().setZoomControlsEnabled(true);
        mapView = new GoogleMapView(this, customMapFragment);
        createPassengerShape();

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (listenLocation) {
                    center = map.getCameraPosition().target;

                    GeoCoder.getLocationName(center, new GeoCoder.VolleyCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            if (startMarker == null) {
                                startLocation = (String) result;
                            }
                            setLocationText((String) result);
                        }
                    });
                }
            }
        });


    }

    private void createPassengerShape() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_red);
        customMapFragment.getMapWrapperLayout().setCurrentImage(bitmap);

    }

    private void createEndPointShape() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.finish);
        customMapFragment.getMapWrapperLayout().setCurrentImage(bitmap);
    }

    public void setLocationText(String text) {

        locationTxt.setText(text);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.CurrentPositionButton: {
                mapView.goCurrentPosition();
                break;
            }
            case R.id.MapStartPointButton: {
                Intent intent = new Intent(this, Taxi_Select_Points.class);
                intent.putExtra("startLocation", startLocation);
                startActivityForResult(intent, REQUEST_CODE);


                break;
            }
            case R.id.MapFindButton: {
                if (startMarker != null) {
                    customMapFragment.getMapWrapperLayout().setCurrentImage(null);
                    customMapFragment.getView().invalidate();
                    endMarker = mapView.createMarker(new TaxiLocation(center.longitude, center.latitude), R.drawable.finish);
                    listenLocation = false;

//                    drawPath(new LatLng(43.251604, 76.911976),
//                            new LatLng(center.longitude, center.latitude));
                    GeoCoder.getPathInfo(new LatLng(start.getLongitude(), start.getLatitude()),
                            new LatLng(center.longitude, center.latitude), new GeoCoder.VolleyCallback() {
                                @Override
                                public void onSuccess(Object result) {
                                    drawPath(result);
                                }
                            });
                }
                break;
            }
            case R.id.MapEndPointButton: {
                clearScreen();
                break;
            }
        }
    }

    private void clearScreen() {
        map.clear();
        startMarker = null;
        listenLocation = true;
        createPassengerShape();
        customMapFragment.getView().invalidate();
        routeInfo.setVisibility(View.GONE);
        txtDistance.setText("");
        txtDuration.setText("");
        mapView.goCurrentPosition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            switch (data.getStringExtra("TYPE")) {
                case START: {
                    createPassengerShape();
                    goToLocationByName(data.getStringExtra("startLocation"));
                    break;
                }
                case END: {
                    createEndPointShape();
                    createStartMarker(data.getStringExtra("startLocation"));
                    break;
                }
                case ALL: {
                    createEndPointShape();
                    createStartMarker(data.getStringExtra("startLocation"));
                    goToLocationByName(data.getStringExtra("endLocation"));

//                    GeoCoder.getPathInfo(data.getStringExtra("startLocation"),
//                            data.getStringExtra("endLocation"), new GeoCoder.VolleyCallback() {
//                                @Override
//                                public void onSuccess(Object result) {
//                                    customMapFragment.getMapWrapperLayout().setCurrentImage(null);
//                                    customMapFragment.getView().invalidate();
//                                    listenLocation = false;
//                                    drawPath(result);
//                                }
//                            });
                    break;
                }
            }
        }
    }

    private void createStartMarker(String location){
        GeoCoder.getGeoCodeByName(location, this, new GeoCoder.VolleyCallback() {
            @Override
            public void onSuccess(Object result) {
                start= (TaxiLocation) result;
                startMarker = mapView.createMarker(start, R.drawable.user_red);
            }
        });
    }

    private void goToLocationByName(String name){
        GeoCoder.getGeoCodeByName(name, this, new GeoCoder.VolleyCallback() {
            @Override
            public void onSuccess(Object result) {
                TaxiLocation taxiLocation = (TaxiLocation) result;
                mapView.goToLocation(taxiLocation.getLatitude(), taxiLocation.getLongitude());
            }
        });
    }
    private void drawPath(Object result) {
        String[] info = mapView.drawPath(result);
        routeInfo.setVisibility(View.VISIBLE);
        txtDistance.setText(info[0]);
        txtDuration.setText(info[1]);

    }

}
