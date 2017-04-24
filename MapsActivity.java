package com.example.akash.m_ps;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {



    private GoogleMap mMap;
    LocationManager lm;
    Location l;
    Button b1;
    HashMap<String, String> mMarkerPlaceLink = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         getSupportActionBar().show();
        getSupportActionBar().setTitle("Parking Finder");
        b1=(Button)findViewById(R.id.bb2);

        b1.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {   //to show exit button

        MenuInflater ob=new MenuInflater(MapsActivity.this);
        ob.inflate(R.menu.ext,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //working of exit button

        if (item.getItemId()==R.id.m1)
        {
            AlertDialog.Builder obj=new AlertDialog.Builder(MapsActivity.this);
            obj.setTitle("Exit");
            obj.setMessage("Do you want to exit the app ?");
            obj.setCancelable(false);
            obj.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            obj.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    onBackPressed();

                }
            });
            obj.create().show();

        }

        return super.onOptionsItemSelected(item);
    }

    private String downloadUrl(String strUrl) throws IOException {  //to get or download information from net
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    @Override
    public void onClick(View v) {  // places and Details are in build file.   //working of button click


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); //to set full screen on button click



        StringBuilder sb =new StringBuilder();
        sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"); //to search nearby places
        sb.append("location=" + l.getLatitude() + "," + l.getLongitude());  //getting lat and lang
        sb.append("&radius=5000");
        sb.append("&types=" + "parking");  // to search the particular places we replace the "parking" with that particular place
        sb.append("&sensor=true");        //
        sb.append("&key=AIzaSyCfdXATlz7jtM6MEvy9Xh_3_g_Ivc5ysXE");  //api key
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sb.toString());

    }

    /**
     * A class, to download Google Places
     */
    class PlacesTask extends AsyncTask<String, Integer, String> {  //google library function to download information from google server

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {


        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                /** Getting the parsed data as a List construct */
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            // Clears all the existing markers
            mMap.clear();


            try {
                setcurrentmarker();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);
                double lat = Double.parseDouble(hmPlace.get("lat"));
                double lng = Double.parseDouble(hmPlace.get("lng"));
                // Getting name
                String name = hmPlace.get("place_name");
                String vicinity = hmPlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);
                // Setting the title for the marker.
                //This will be displayed on taping the marker
                markerOptions.title(name);
                markerOptions.snippet(vicinity);

                // Placing a marker on the touched position
                Marker m = mMap.addMarker(markerOptions);
                mMarkerPlaceLink.put(m.getId(), hmPlace.get("reference"));
            }
        }
    }

    private void setcurrentmarker() throws IOException {  //user defined function to set the marker at current position




        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        l = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(l != null) {

            LatLng ltp=new LatLng(l.getLatitude(),l.getLongitude());
            Geocoder gt =new Geocoder(this);
            List<Address> ads =  gt.getFromLocation(l.getLatitude(),l.getLongitude(),1);

            Address ad =ads.get(0);                    //to get index zero element

            String a = ad.getAddressLine(0)+","+ad.getAddressLine(1);

            StringBuilder b = new StringBuilder();

            for(int i=2; i<=ad.getMaxAddressLineIndex();i++)
            {
                b.append(", "+ad.getAddressLine(i));
            }
            mMap.addMarker(new MarkerOptions().position(ltp).title(a).snippet(b.toString())).showInfoWindow();
            mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(ltp,15));
        }
        else {

            l = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (l != null) {

                LatLng ltp = new LatLng(l.getLatitude(), l.getLongitude());
                Geocoder gt =new Geocoder(this);
                List<Address> ads =  gt.getFromLocation(l.getLatitude(),l.getLongitude(),1);

                Address ad =ads.get(0);

                String a = ad.getAddressLine(0)+","+ad.getAddressLine(1);

                StringBuilder b = new StringBuilder();

                for(int i=2; i<=ad.getMaxAddressLineIndex();i++)
                {
                    b.append(", "+ad.getAddressLine(i));
                }
                mMap.addMarker(new MarkerOptions().position(ltp).title(a).snippet(b.toString())).showInfoWindow();
                mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(ltp, 15));

            }
            else
            {
                Toast.makeText(MapsActivity.this, "Not found", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {  //on long click the map will be clear and marker automatically set to current position
                mMap.clear();
                try {
                    setcurrentmarker();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {  //to display the information page on licking the map title
                String reference = mMarkerPlaceLink.get(marker.getId());
                if (reference != null) {
                    Intent intent = new Intent(getBaseContext(), Details.class);
                    intent.putExtra("reference", reference);
                    startActivity(intent);
                }
            }
        });

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);   //to show traffic lights
        mMap.getUiSettings().setZoomControlsEnabled(true);  //for zooming the camera


        try
        {
            setcurrentmarker();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}