package www.sk.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;
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
import android.provider.MediaStore;
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

    private Context context;
    private RelativeLayout RLhome;
    private ProgressBar PBloading;
    private TextView TVcityname , TVtemperatture , TVcondition;
    private RecyclerView RVweather;
    private TextInputEditText TIETcityedit;
    private ImageView IVback , IVicon , IVsearch;
    private ArrayList<RVWeatherModal> rvWeatherModalArrayList;
    private RVWeatherAdaptor rvWeatherAdaptor;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        RLhome = findViewById(R.id.rlhome);
        PBloading = findViewById(R.id.PBLoading);
        TVcityname = findViewById(R.id.tvCityName);
        TVtemperatture = findViewById(R.id.tvTemp);
        TVcondition = findViewById(R.id.tvCondition);
        RVweather = findViewById(R.id.rvWeather);
        TIETcityedit = findViewById(R.id.tietEditCity);
        IVback = findViewById(R.id.ivBack);
        IVicon = findViewById(R.id.ivIcon);
        IVsearch = findViewById(R.id.ivSearch);
        rvWeatherModalArrayList = new ArrayList<>();
        rvWeatherAdaptor = new RVWeatherAdaptor(this,rvWeatherModalArrayList);
        RVweather.setAdapter(rvWeatherAdaptor);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        cityName = getCityName(location.getLongitude(),location.getLatitude());
//        getWeatherInfo(cityName);
        if (location != null){cityName = getCityName(location.getLongitude(),location.getLatitude());
            getWeatherInfo(cityName);
        } else {
            cityName = "Delhi";
            getWeatherInfo(cityName);
        }

        IVsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = TIETcityedit.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter City Name",Toast.LENGTH_SHORT).show();
                }else{
                    TVcityname.setText(cityName);
                    getWeatherInfo(city);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission granted..",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude , double latitude){
        String cityName = "Not found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);

            for(Address adr : addresses){
                if(adr!= null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName = city;
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"User City Not Found..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=a1c494369af14927b89120320221903&q="+cityName+"&days=30&aqi=yes&alerts=yes";
        TVcityname.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                PBloading.setVisibility(View.GONE);
                RLhome.setVisibility(View.VISIBLE);
                rvWeatherModalArrayList.clear();

                try{
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    TVtemperatture.setText(temperature+"°c");
                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(IVicon);
                    TVcondition.setText(condition);
                    if(isDay == 1){
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1504628958125-edac62db68fe?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2070&q=80").into(IVback);
                    }else{
                        //night
                        Picasso.get().load("https://images.unsplash.com/photo-1445233566136-a2a4e2c38bc2?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80").into(IVback);
                    }
                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");
                    for(int i=0 ; i<hourArray.length() ; i++) {
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        rvWeatherModalArrayList.add(new RVWeatherModal(time,temper,img,wind));
                    }
                    rvWeatherAdaptor.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}