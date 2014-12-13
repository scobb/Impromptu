package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuLocation;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;
import com.example.steve.impromptu.UI.ScrollableMapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;


/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeLocation extends Fragment {

    private static final int MAXRESULTNUM = 20;
    private static final int DEFAULTZOOM = 15;
    private static final int SEARCHRADIUS = 10000;
    private static final LatLng defaultLocation = new LatLng(30.2864802, -97.74116620000001); //UT Austin ^___^

    //private TextView vLocationPrompt;
    private EditText vAddress;
    //private TextView vSearchAddress;
    private ImageView vSearchPlace;
    private GoogleMap vMap;
    public Event myEvent;
    private ImageView vOkay;
    private ImageView vCancel;
    private int mapType;
    private LatLng myLoc;
    private Vector<Marker> searchResultMarkers;
    private ImpromptuLocation returnVal;
    private TextView vSelectedLocation;
    private ScrollView vScrollView;
    private ImageView vMyLocation;

    private static View fragmentView;
    private FragmentActivity myContext;

    OnComposeLocationFinishedListener mCallback;

    // Container Activity must implement this interface
    public interface OnComposeLocationFinishedListener {
        public void onComposeLocationFinished();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        if (fragmentView != null) {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null)
                parent.removeView(fragmentView);
        }
        try {
            fragmentView = inflater.inflate(R.layout.fragment_compose_location, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        //this works just a little differently:
        ScrollableMapFragment mf = (ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragComposeLocation_map);
        vMap = mf.getMap();

        ActivityMain myActivity = (ActivityMain) getActivity();

        // get references to GUI widgets
        vAddress = (EditText) fragmentView.findViewById(R.id.fragComposeLocation_editText_address);
        vSearchPlace = (ImageView) fragmentView.findViewById(R.id.fragComposeLocation_imageView_search);
        vOkay = (ImageView) fragmentView.findViewById(R.id.fragComposeLocation_imageView_accept);
        vCancel = (ImageView) fragmentView.findViewById(R.id.fragComposeLocation_imageView_cancel);
        vSelectedLocation = (TextView) fragmentView.findViewById(R.id.fragComposeLocation_textView_selectedLocation);
        vScrollView = (ScrollView) fragmentView.findViewById(R.id.fragComposeLocation_scrollView);
        vMyLocation = (ImageView) fragmentView.findViewById(R.id.fragComposeLocation_imageView_myLocation);

        //set on-click listeners
        vSearchPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlace();
            }
        });

        vCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearSearchResultMarkers();
                mCallback.onComposeLocationFinished();

            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(returnVal == null)
                {
                    Toast.makeText(getActivity(), "No location selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Toast.makeText(getActivity(), "Selected: " + returnVal.toString(), Toast.LENGTH_SHORT).show();

                myEvent.setLocationName(returnVal.getName());
                myEvent.setFormattedAddress(returnVal.getFormattedAddress());
                myEvent.setLatitude(returnVal.getCoordinates().latitude);
                myEvent.setLongitude(returnVal.getCoordinates().longitude);

                clearSearchResultMarkers();
                mCallback.onComposeLocationFinished();
            }
        });

        vSelectedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(returnVal != null)
                {
                    clearSearchResultMarkers();

                    searchResultMarkers.add(vMap.addMarker(new MarkerOptions().title(returnVal.getName())
                            .snippet(returnVal.getFormattedAddress())
                            .position(returnVal.getCoordinates())));

                    vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(returnVal.getCoordinates(), DEFAULTZOOM));
                }
            }
        });

        vMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker){
                markerClick(marker);
                return false; //false means let the default behavior occur, also
            }
        });

        vMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                centerMapMyLocation(); //also includes a call to updateLocation()

                //TODO: reverse geocoding !!!!!

            }
        });


        ((ScrollableMapFragment) myContext.getSupportFragmentManager().findFragmentById(R.id.fragComposeLocation_map))
                .setListener(new ScrollableMapFragment.OnTouchListener() {

                    @Override
                    public void onTouch() {
                        vScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


        //initialize return value, pre-load with existing event if there is one
        myEvent = myActivity.getComposeEvent();
        returnVal = null;

          //TODO: refactor this check? different way of asking "is location null"?? Put add function to Event?
        if (myEvent.getLocationName() != null)
        {
            returnVal = new ImpromptuLocation();

            String formattedAdr = myEvent.getFormattedAddress();
            if (!(formattedAdr == null)) {
                returnVal.setFormattedAddress(formattedAdr);
            }

            String name = myEvent.getLocationName();
            if (!(name ==null)) {
                returnVal.setName(name);
            }

            Double lat = myEvent.getLatitude();
            Double lng = myEvent.getLongitude();
            if (!(lat == null) && !(lng == null)) {
                LatLng coord = new LatLng(lat, lng);
                returnVal.setCoordinates(coord);
            }
        }



        //some map initializations:
        if(searchResultMarkers == null)
        {
            searchResultMarkers = new Vector<Marker>(MAXRESULTNUM);
        }

        centerMapMyLocation();

        vSelectedLocation.setText("");

        //if a selection has already been made, start with it listed at the bottom
        if(returnVal != null) {
            selectLoc(returnVal); //set vSelectedLocation text
        }



        return fragmentView;
    }


    private void updateLocation() {
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        //Or use LocationManager.GPS_PROVIDER
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);


        if(lastKnownLocation == null)
        {
            myLoc = defaultLocation;
        }
        else
        {
            myLoc = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        }
    }

    private void centerMapMyLocation() {

        updateLocation();

        if(vMap != null) {

            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, DEFAULTZOOM));

            mapType = 0;
            //toggleMapType();
        }
    }


    @Override
    public void onAttach(Activity activity) {

        myContext=(FragmentActivity) activity;
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnComposeLocationFinishedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnComposeTimeFinishedListener");
        }
    }

    /*
    private void toggleMapType()
    {
        mapType++;
        mapType %= 4; //range of values 0 - 3


        switch(mapType)
        {
            case 0:
                vMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case 1:
                vMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case 2:
                vMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case 3:
                vMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

    }
    */

    private void searchPlace(){
        String query = vAddress.getText().toString();
        String[] queryChunks = query.split("[ ]+");

        String httpQuery = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";

        if (queryChunks.length > 0) {
            httpQuery += queryChunks[0];
        }

        for (int i = 1; i < queryChunks.length; i++)
        {
            httpQuery += "+" + queryChunks[i];
        }

        updateLocation();

        if(myLoc != null) {
            httpQuery += "&location=" + myLoc.latitude + "," + myLoc.longitude + "&radius=" + SEARCHRADIUS;
        }

        //TODO: move this key into strings.xml ...
        httpQuery += "&key=AIzaSyCRP8acNPFERUdMPouoFU_cM0sTfdT6tww";

        new HttpPlaceAsyncTask().execute(httpQuery);
    }

    private class HttpPlaceAsyncTask extends AsyncTask<String, Void, String> {
        public HttpPlaceAsyncTask() {

        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute displays the result of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            processJSON(result, true);
        }
    }

    private synchronized void processJSON(String result, boolean isPlaceQuery) {
        String errorMsg = "Sorry, an error has occurred.";
        JSONObject respJSON = null;

        int resultSize = 0;

        try {
            respJSON = new JSONObject(result);
            resultSize = respJSON.getJSONArray("results").length();
        } catch (JSONException e) {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        if(resultSize == 0){
            Toast.makeText(getActivity(), "No matches found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(resultSize > MAXRESULTNUM) {
            resultSize = MAXRESULTNUM;
        }

        clearSearchResultMarkers();

        JSONObject currResult;

        for(int i = 0; i < resultSize; i++)
        {
            try {
                currResult = respJSON.getJSONArray("results").getJSONObject(i);
                double lat = (Double) currResult.getJSONObject("geometry")
                        .getJSONObject("location").get("lat");
                double lon = (Double) currResult.getJSONObject("geometry")
                        .getJSONObject("location").get("lng");
                String formatAdd = currResult.getString("formatted_address");

                String locationName = "";
                if(isPlaceQuery) {
                    locationName = currResult.getString("name");
                }

                searchResultMarkers.add(vMap.addMarker(new MarkerOptions() //add marker to vector (and map)
                        .title(locationName)
                        .snippet(formatAdd)
                        .position(new LatLng(lat, lon))));

            } catch (JSONException e) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                clearSearchResultMarkers();
                vMap.setMyLocationEnabled(true);
                vMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULTZOOM));
                vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, DEFAULTZOOM));
                return;
            }
        }

        vMap.setMyLocationEnabled(true);
        vMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULTZOOM));
        if(searchResultMarkers.size() > 1) { //zoom out to show multiple results
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchResultMarkers.get(0).getPosition(), DEFAULTZOOM - 3));
        }
        else {
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchResultMarkers.get(0).getPosition(), DEFAULTZOOM));
        }
    }

    private void clearSearchResultMarkers(){

        if(searchResultMarkers != null) {
            for (int i = 0; i < searchResultMarkers.size(); i++) {
                searchResultMarkers.get(i).remove(); //remove marker from map
            }
            searchResultMarkers.clear();
        }
    }

    private void removeSearchResultMarkers() {
        if(searchResultMarkers != null) {
            for(int i = 0; i < searchResultMarkers.size(); i++) {
                searchResultMarkers.get(i).remove(); //remove from map
            }
        }
    }

    private void mapSearchResultMarkers() {

        //stupid, but can't add existing markers to the map. So, create add new ones with same info as old ones

        if(searchResultMarkers != null && vMap != null) {

            Vector<Marker> oldMarkers = searchResultMarkers;
            searchResultMarkers = new Vector<Marker>(oldMarkers.size());

            for(int i = 0; i < oldMarkers.size(); i++) {
                Marker m = oldMarkers.get(i);
                searchResultMarkers.add(vMap.addMarker(new MarkerOptions().title(m.getTitle())
                        .snippet(m.getSnippet()).position(m.getPosition())));
            }
        }
    }

    private void selectLoc(ImpromptuLocation il){
        returnVal = il;
        //Toast.makeText(getActivity(), il.getFormattedAddress() + " " + il.getCoordinates().toString(), Toast.LENGTH_SHORT).show();
        vSelectedLocation.setText("   " + il.getName() + "\n   " + il.getFormattedAddress());
    }

    private void markerClick(Marker m)
    {
         selectLoc(new ImpromptuLocation(m.getSnippet(), m.getTitle(), m.getPosition()));
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = readStream(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String readStream(InputStream is) throws IOException {

        StringBuilder ans = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        // ^^^ provide a size of the buffer????

        for (String currentLine = br.readLine(); currentLine != null; currentLine = br
                .readLine()) {
            ans.append(currentLine);
        }

        is.close();

        return ans.toString();
    }

    @Override
    public void onPause() {
        super.onPause();

        removeSearchResultMarkers();
    }

    public void onResume() {

        super.onResume();

        mapSearchResultMarkers();
    }
}