package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRL;
    private ProgressBar loadPB;
    private TextView cityTV, tempTV, conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private RecyclerView weatherRV;

    private LocationManager locationManager;
    private int PERMISSON_CODE = 1;
    private String cityName;

    private ArrayList<ModalWindow> modalWindowArrayList;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        homeRL = findViewById(R.id.idRLHome);
        loadPB = findViewById(R.id.idPBLoad);
        cityTV = findViewById(R.id.idTVCityName);
        tempTV = findViewById(R.id.idTVTemp);
        conditionTV = findViewById(R.id.idTVCondition);
        cityEdt = findViewById(R.id.idEdtCity);
        backIV = findViewById(R.id.idIVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        weatherRV = findViewById(R.id.idRVWeather);

        modalWindowArrayList = new ArrayList<>();
        adapter = new Adapter(this, modalWindowArrayList);
        weatherRV.setAdapter(adapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                    }, PERMISSON_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityName = getCity(location.getLongitude(), location.getLatitude());
        getWeatherInfo(cityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter a City Name", Toast.LENGTH_SHORT).show();
                } else {
                    cityTV.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSON_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please give me permissions :(", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCity(double longitude, double latitude) {
        String city = "Not Found";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(longitude, latitude, 10);
            for (Address a : addressList) {
                if (a != null) {
                    String cityTest = a.getLocality();
                    if (cityTest != null && !cityTest.equals("")) {
                        city = cityTest;
                    } else {
                        Log.d("TAG", "CITY NOT FOUND");
                        Toast.makeText(this, "City not found...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }

    private void getWeatherInfo(String city) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=e2331dbcd81944b39fa110536221704&q=" + city + "&days=1&aqi=yes&alerts=yes";

        cityTV.setText(city);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                modalWindowArrayList.clear();
                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    tempTV.setText(temperature + "°C");

                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    if (isDay == 1) {
                        Picasso.get().load("https://i.imgur.com/sKdisgO.png").into(backIV);
                    } else {
                        Picasso.get().load("https://i.imgur.com/KllB4aL.png").into(backIV);
                    }

                    JSONObject forecast = response.getJSONObject("forecast");
                    JSONObject forecastday = forecast.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastday.getJSONArray("hour");

                    for(int i = 0; i < hourArray.length(); i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String icon = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        modalWindowArrayList.add(new ModalWindow(time, temper, icon, wind));
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter a Valid City Name.", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonObjectRequest);

    }
}