package com.example.akash.m_ps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


public class Details extends Activity {
    String url;
    ImageView img11;
    ImageButton mycalldoc;
    TextView namedoc, adddoc, webdoc, calldoc;
    RatingBar ratingBar;
    private LatLng latLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        img11 = (ImageView) findViewById(R.id.img11);
        namedoc = (TextView) findViewById(R.id.namedoc);
        adddoc = (TextView) findViewById(R.id.adddoc);
        webdoc = (TextView) findViewById(R.id.webdoc);
        calldoc = (TextView) findViewById(R.id.calldoc);
        ratingBar = (RatingBar) findViewById(R.id.pop_ratingbar);
        mycalldoc = (ImageButton) findViewById(R.id.myvcalldoc);

        String reference = getIntent().getStringExtra("reference");  //we are getting the reference of place in this file from mMap actin

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        sb.append("reference=" + reference);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCfdXATlz7jtM6MEvy9Xh_3_g_Ivc5ysXE");
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(sb.toString());

    }

    private String downloadUrl(String strUrl) throws IOException {
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
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }


        return data;
    }

    /**
     * A class, to download Google Place Details
     */
    private class PlacesTask extends AsyncTask<String, Integer, String> {

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

            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask

            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Place Details in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String, String>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected HashMap<String, String> doInBackground(String... jsonData) {

            HashMap<String, String> hPlaceDetails = null;

            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return hPlaceDetails;


        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String, String> hPlaceDetails) {


            String name = hPlaceDetails.get("name");

            String icon = hPlaceDetails.get("icon");
            String vicinity = hPlaceDetails.get("vicinity");
            String lat = hPlaceDetails.get("lat");
            String lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            String website = hPlaceDetails.get("website");
            String rating = hPlaceDetails.get("rating");
            String international_phone_number = hPlaceDetails.get("international_phone_number");
            String url = hPlaceDetails.get("url");
            String photorefrence = hPlaceDetails.get("photo_reference");


            MarkerOptions markerOptions = new MarkerOptions();
            try {
                latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            } catch (RuntimeException ignored) {
                // NPE or parsing failed, double1 == -1.0
            }

            markerOptions.position(latLng);
            namedoc.setText(name);
            adddoc.setText(vicinity);
            webdoc.setText(website);
            try {
                ratingBar.setRating(Float.parseFloat(rating));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            Linkify.addLinks(webdoc, Linkify.WEB_URLS);
            calldoc.setText(formatted_phone);
            Linkify.addLinks(calldoc, Linkify.PHONE_NUMBERS);
            new photoo(photorefrence);
            mycalldoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + calldoc.getText()));
                    startActivity(intent);
                }
            });

        }
    }

    private class photoo {
        public photoo(String photorefrence) {
            int width = 400;
            int height = 120;
            String refrencee = photorefrence;

            url = "https://maps.googleapis.com/maps/api/place/photo?";
            String key = "key=AIzaSyCfdXATlz7jtM6MEvy9Xh_3_g_Ivc5ysXE";
            String refrence1 = "photoreference=" + refrencee;
            String sensor = "sensor=false";
            String maxWidth = "maxwidth=" + width;
            String maxHeight = "maxheight=" + height;

            url = url + "&" + key + "&" + refrence1 + "&" + sensor + "&" + maxWidth + "&" + maxHeight;
            // Traversing through all the photoreferences
            Log.e("prrr", "++++" + url);
            ImageDownloadTask jj = new ImageDownloadTask();
            jj.execute(url);


        }


    }

    private class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap> {
        Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                // Starting image download

                bitmap = downloadImage(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
           /* img11.setImageBitmap(result);*/
            Log.e("ref", "++++++++" + result);
            Picasso.with(Details.this).load(url).fit().placeholder(R.drawable.noim).into(img11);
        }
    }

    private Bitmap downloadImage(String s) throws IOException {
        Bitmap bitmap = null;
        InputStream iStream = null;
        try {
            URL url = new URL(s);

            /** Creating an http connection to communcate with url */
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            /** Connecting to url */
            urlConnection.connect();

            /** Reading data from url */
            iStream = urlConnection.getInputStream();


            /** Creating a bitmap from the stream returned from the url */
            bitmap = BitmapFactory.decodeStream(iStream);
            Log.d("Exception while downloading url", bitmap.toString());
        } catch (Exception e) {
            Log.d("Exception while downloading url", e.toString());
        } finally {
            iStream.close();
        }
        return bitmap;
    }


}

