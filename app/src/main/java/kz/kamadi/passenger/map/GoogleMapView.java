package kz.kamadi.passenger.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kz.kamadi.passenger.model.TaxiLocation;
import kz.kamadi.passenger.utils.GeoCoder;

/**
 * Created by Madiyar on 02.02.2015.
 */
public class GoogleMapView {
    private CustomMapFragment mapFragment;
    private Context context;
    private GoogleMap map;


    public GoogleMapView(Context context, CustomMapFragment customMapFragment) {
        this.context = context;
        mapFragment = customMapFragment;
        map = mapFragment.getMap();
        initMap();
    }


    private void initMap() {
        goCurrentPosition();
    }

    public void goCurrentPosition() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                .zoom(16).build();

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public void goToLocation(double latitude, double longitude) {


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(16).build();

        map.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    public Marker createMarker(TaxiLocation taxiLocation, int resource) {

        return map.addMarker(new MarkerOptions()
                .position(new LatLng(taxiLocation.getLatitude(), taxiLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromResource(resource)));
    }




    public String[] drawPath(Object routes) {
        final String[] info = new String[2];


        List<List<HashMap<String, String>>> result = (List<List<HashMap<String, String>>>) routes;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                if (j == 0) {    // Get distance from the list
                    info[0] = point.get("distance");
                    continue;
                } else if (j == 1) { // Get duration from the list
                    info[1] = point.get("duration");

                    continue;
                }


                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));

                LatLng position = new LatLng(lat, lng);
                points.add(position);

            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(15);
            lineOptions.color(Color.BLACK);

        }


        // Drawing polyline in the Google Map for the i-th route
        map.addPolyline(lineOptions);


        return info;

    }


}
