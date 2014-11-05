package com.example.steve.impromptu.Main.Compose;

import android.app.Activity;
import android.app.Fragment;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.steve.impromptu.Main.ActivityMain;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.GoogleMap;
import com.example.steve.impromptu.R;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;

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

/**
 * Created by jonreynolds on 10/16/14.
 */
public class FragmentComposeLocation extends Fragment {

    private TextView vLocationPrompt;
    private EditText vAddress;
    private TextView vSearchPrompt;
    private GoogleMap vMap;
    public Event myEvent;
    private LinearLayout vOkay;
    private LinearLayout vCancel;
    private int mapType;
    private String postalCodeString = "nothing yet";

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
        myEvent = myActivity.getNewEvent();

        // get references to GUI widgets
        vLocationPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeLocation_textView_locationPrompt);
        vAddress = (EditText) fragmentView.findViewById(R.id.fragComposeLocation_editText_address);
        vSearchPrompt = (TextView) fragmentView.findViewById(R.id.fragComposeLocation_textView_searchPrompt);
        vOkay = (LinearLayout) fragmentView.findViewById(R.id.fragComposeLocation_linearLayout_okay);
        vCancel = (LinearLayout) fragmentView.findViewById(R.id.fragComposeLocation_linearLayout_cancel);

        //this works just a little differently:
        //vMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragComposeLocation_map)).getMap();
        //vMap = ((MapFragment) fragmentView.findViewById(R.id.fragComposeLocation_map)).getMap();
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

                // TODO make sure enough info is filled out

                Toast.makeText(getActivity(), "Select cancel", Toast.LENGTH_SHORT).show();

            }
        });

        vOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO actually "return" the location, make sure info is filled out

                myEvent.setLocation(postalCodeString);

                mCallback.onComposeLocationFinished();
            }
        });


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

        //some map initializations:

        if(vMap != null) {

            LatLng sydney = new LatLng(-33.867, 151.206);

            vMap.setMyLocationEnabled(true);
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

            vMap.addMarker(new MarkerOptions()
                    .title("Sydney")
                    .snippet("The most populous city in Australia.")
                    .position(sydney));

            mapType = 0;
            toggleMapType();
        }




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

            Double lat = (double) 0;
            Double lon = (double) 0;
            JSONObject respJSON = null;
            try {
                respJSON = new JSONObject(result);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            try {
                lat = (Double) respJSON.getJSONArray("results")
                        .getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }
            try {
                lon = (Double) respJSON.getJSONArray("results")
                        .getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return;
            }

            LatLng destination = new LatLng(lat, lon);

            vMap.setMyLocationEnabled(true);
            vMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            vMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 13));

            String address = vAddress.getText().toString();

            vMap.addMarker(new MarkerOptions().title("Query:")
                    .snippet(address).position(destination));

            String postalCode = "Postal Code: ";

            try {
                postalCode +=
                        respJSON.getJSONArray("results").getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(7).getString("long_name");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                postalCode = "Postal Code: N/A";
                postalCodeString = postalCode;
            }

            vAddress.setText(postalCode);
        }
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