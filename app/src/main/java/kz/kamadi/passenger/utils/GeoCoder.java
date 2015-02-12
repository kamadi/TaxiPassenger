package kz.kamadi.passenger.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kz.kamadi.passenger.model.TaxiLocation;

import kz.kamadi.passenger.app.VolleyApplication;
import kz.kamadi.passenger.ui.MainActivity;

/**
 * Created by Madiyar on 02.02.2015.
 */
public class GeoCoder {
    public interface VolleyCallback {
        void onSuccess(Object result);
    }

    public static void getPathInfo(LatLng startPoint,LatLng endPoint,final VolleyCallback callback){
        // Origin of route
        String str_origin = "origin="+startPoint.longitude+","+startPoint.latitude;

        // Destination of route
        String str_dest = "destination="+endPoint.longitude+","+endPoint.latitude;


        // Sensor enabled
        String sensor = "sensor=false&language=ru";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.e("URL",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            List<List<HashMap<String, String>>> routes = null;
            @Override
            public void onResponse(JSONObject response) {
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(response);
                Log.e("response",""+response.toString());
                callback.onSuccess(routes);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error.getMessage());
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
    }
    public static void getPathInfo(String startPoint,String endPoint,final VolleyCallback callback){
        // Origin of route
        String str_origin = "";
        String str_dest = "";
        try {
            str_origin = "origin="+ URLEncoder.encode(startPoint, "UTF-8");
            str_dest = "destination="+URLEncoder.encode(endPoint,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Destination of route



        // Sensor enabled
        String sensor = "sensor=false&language=ru";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
        Log.e("URL",url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {
            List<List<HashMap<String, String>>> routes = null;
            @Override
            public void onResponse(JSONObject response) {
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(response);
                Log.e("response",""+response.toString());
                callback.onSuccess(routes);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error",""+error.getMessage());
            }
        });
        VolleyApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
    }
    public static void getLocationName(LatLng location, final VolleyCallback callback) {

        StringBuilder stringbuilder = new StringBuilder("http://geocode-maps.yandex.ru/1.x/?geocode=");
        stringbuilder.append(location.longitude + "," + location.latitude);
        stringbuilder.append((new StringBuilder("&lang=ru")));
        stringbuilder.append("&format=json&kind=house&results=1");
        String url = stringbuilder.toString();
//        Log.e("URL", url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");
                            String name = jsonArray.getJSONObject(0).getJSONObject("GeoObject").getString("name");
                            callback.onSuccess(name);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });


        VolleyApplication.getInstance().getRequestQueue().add(jsObjRequest);
    }

    public static void getGeoCodeByName(String name,Context context,final VolleyCallback callback){
        TaxiLocationListener taxiLocationListener = new TaxiLocationListener(context);
        TaxiLocation taxiLocation = new TaxiLocation(taxiLocationListener.getLocation());

        StringBuilder stringbuilder = new StringBuilder("http://geocode-maps.yandex.ru/1.x/?geocode=");
        stringbuilder.append(name);
        stringbuilder.append((new StringBuilder("&lang=ru")));
        stringbuilder.append("&format=json&results=1&spn=0.01,0.01");
        stringbuilder.append("&ll=" + taxiLocation.getLongitude() + "," + taxiLocation.getLatitude());
        String url = stringbuilder.toString();
        Log.e("URL", url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonarray = response.getJSONObject("response").getJSONObject("GeoObjectCollection").getJSONArray("featureMember");
                    String as[] = jsonarray.getJSONObject(0).getJSONObject("GeoObject").getJSONObject("Point").getString("pos").split(" ");
                    TaxiLocation location = new TaxiLocation(Double.parseDouble(as[0]),Double.parseDouble(as[1]));
                    callback.onSuccess(location);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        VolleyApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
    }

    public static void getLocationsByName(String part, Context context, final VolleyCallback callback) {
        try {

            TaxiLocationListener taxiLocationListener = new TaxiLocationListener(context);
            TaxiLocation taxiLocation = new TaxiLocation(taxiLocationListener.getLocation());

            StringBuilder stringbuilder = new StringBuilder("http://suggest-maps.yandex.ua/suggest-geo?callback=&part=");
            stringbuilder.append(URLEncoder.encode(part, "UTF-8"));
            stringbuilder.append("&ll=" + taxiLocation.getLongitude() + "," + taxiLocation.getLatitude());
            stringbuilder.append("&lang=ru&search_type=all&fullpath=1&v=5&rspn=1&spn=0.01,0.01");
            String url = stringbuilder.toString();
//            Log.e("URL", url);
            SuggesTask suggesTask = new SuggesTask(callback);
            suggesTask.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static class SuggesTask extends AsyncTask<String, Void, JSONArray> {
        VolleyCallback callback;

        public SuggesTask(VolleyCallback callback) {
            this.callback = callback;
        }

        @Override
        protected JSONArray doInBackground(String... urls) {

            String response = "", url = "";
            JSONArray jsonArray = null;
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
                Log.e("response", response);
                jsonArray = new JSONArray(response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return jsonArray;
        }


        @Override
        protected void onPostExecute(JSONArray result) {
            ArrayList arrayList = null;
            try {

                JSONArray info = result.getJSONArray(1);



                arrayList = new ArrayList(info.length());

                for (int i = 0; i < info.length(); i++) {

                    arrayList.add(info.getJSONArray(i).get(1).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            callback.onSuccess(arrayList);

        }


    }
}

