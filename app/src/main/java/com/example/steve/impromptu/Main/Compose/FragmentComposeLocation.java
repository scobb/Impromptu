package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.steve.impromptu.Entity.Event;
import com.example.steve.impromptu.Entity.ImpromptuLocation;
import com.example.steve.impromptu.Main.ActivityMain;
import com.example.steve.impromptu.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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

    private static final int MAXRESULTNUM = 5;
    private static final int DEFAULTZOOM = 15;
    private static final LatLng defaultLocation = new LatLng(30.2864802, -97.74116620000001); //UT Austin ^___^

    private TextView vLocationPrompt;
    private EditText vAddress;
    private TextView vSearchPrompt;
    private GoogleMap vMap;
    public Event myEvent;
    private LinearLayout vOkay;
    private LinearLayout vCancel;
    private int mapType;
    private LatLng myLoc;
    private Vector<Marker> searchResultMarkers;
    private ImpromptuLocation[] searchResults = new ImpromptuLocation[MAXRESULTNUM];
    private ImpromptuLocation returnVal;

    private static View fragmentView;

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

        // Inflate the layout for this fragment


        ActivityMain myActivity = (ActivityMain) getActivity();

        myEvent = myActivity.getComposeEvent();

        returnVal = null;


        // get references to GUI widgets
        vLocationPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeLocation_textView_locationPrompt);
        vAddress = (EditText) fragmentView.findViewById(R.id.fragComposeLocation_editText_address);
        vSearchPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeLocation_textView_searchPrompt);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeLocation_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeLocation_linearLayout_cancel);

        //this works just a little differently:
        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.fragComposeLocation_map);
        vMap = mf.getMap();

        vSearchPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v){
                searchAddress();
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

                //TODO actually "return" the location

                if(returnVal == null)
                {
                    Toast.makeText(getActivity(), "No location selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(getActivity(), "Selected: " + returnVal.getFormattedAddress() + " , " + returnVal.getCoordinates(), Toast.LENGTH_SHORT).show();

                myEvent.setLocation("lol herp");

                clearSearchResultMarkers();
                mCallback.onComposeLocationFinished();
            }
        });


        //some map initializations:
        if(searchResultMarkers == null)
        {
            searchResultMarkers = new Vector<Marker>(MAXRESULTNUM);
        }

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

        if(vMap != null) {

            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, DEFAULTZOOM));

            mapType = 0;
            toggleMapType();
        }

        vMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker){
                markerClick(marker);
                return false; //false means let the default behavior occur, also
            }
        });



        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
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

    private void searchAddress()
    {
        String address = vAddress.getText().toString();
        String[] addressChunks = address.split("[ ]+");
        String addressQuery = "https://maps.googleapis.com/maps/api/geocode/json?address=";

        if (addressChunks.length > 0) {
            addressQuery += addressChunks[0];
        }

        for (int i = 1; i < addressChunks.length; i++) {
            addressQuery += "+";
            addressQuery += addressChunks[i];
        }

        //TODO: move this key into strings.xml
        //TODO: AND ADD "CENTER", change search type?
        addressQuery += "&key=AIzaSyCRP8acNPFERUdMPouoFU_cM0sTfdT6tww";


        new HttpAsyncTask().execute(addressQuery);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        public HttpAsyncTask() {
        }

        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

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

            for(int i = 0; i < resultSize; i++)
            {
                try {
                    double lat = (Double) respJSON.getJSONArray("results")
                            .getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").get("lat");
                    double lon = (Double) respJSON.getJSONArray("results")
                            .getJSONObject(i).getJSONObject("geometry")
                            .getJSONObject("location").get("lng");
                    String formatAdd = respJSON.getJSONArray("results").getJSONObject(i).getString("formatted_address");

                    searchResults[i] = new ImpromptuLocation(formatAdd, new LatLng(lat, lon)); //add Impromptu location to array

                    searchResultMarkers.add(vMap.addMarker(new MarkerOptions() //add marker to vector (and map)
                        .title("" + i) //hack I'm using: marker's title is its index in the vector
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
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchResults[0].getCoordinates(), DEFAULTZOOM));

        }
    }

    private void clearSearchResultMarkers(){
        //TODO: enable/disable "ok/done/submit/whatever" button? would disable here.
        for(int i = 0; i < searchResultMarkers.size(); i++) {
            searchResultMarkers.get(i).remove(); //remove marker from map
        }
        searchResultMarkers.clear();
    }

    private void selectLoc(ImpromptuLocation il){
        returnVal = il;
        Toast.makeText(getActivity(), il.getFormattedAddress() + " " + il.getCoordinates().toString(), Toast.LENGTH_SHORT).show();
    }

    private void markersAllRed() {
        //TODO: implement (if I even need to use it ...)
    }

    private void markerClick(Marker m)
    {
        //markersAllRed(); //change all marker colors to red
        //make marker blue ??

        //TODO: add proper try/catch here
        selectLoc(searchResults[Integer.parseInt(m.getTitle().toString())]);
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

    /*
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    */
}

        /*
        int hasGooglePlay = GooglePlayServicesUtil.isGooglePlayServicesAvailable(fragmentView.getContext());

        String toastMe = "Google Play Services are available";

        switch(hasGooglePlay)
        {
            case ConnectionResult.SUCCESS:
                toastMe = "Success";
             break;
            case ConnectionResult.SERVICE_MISSING:
                toastMe = "Service Missing";
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                toastMe = "Service Version Update Required";
                break;
            case ConnectionResult.SERVICE_DISABLED:
                toastMe = "Service Disabled";
                break;
            case ConnectionResult.SERVICE_INVALID:
                toastMe = "Service Invalid";
                break;
            default:
                toastMe = "Well, we at least entered the switch case";
                break;
        }

        Toast.makeText(getActivity(), toastMe, Toast.LENGTH_SHORT).show();
        */
