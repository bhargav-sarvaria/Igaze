package com.igaze.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.igaze.R;
import com.igaze.Utilities.Constants;
import com.igaze.Utilities.CustomBoldTextView;
import com.igaze.Utilities.CustomTextView;
import com.igaze.Utilities.GPSTracker;
import com.igaze.Utilities.MyDatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.igaze.Utilities.Constants.GET_WISH_TYPES_URL;
import static com.igaze.Utilities.Constants.showLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar toolbar;
    android.support.v7.app.AlertDialog titleDialog,wishTypeDialog;

    String wishTitleValue = "I would like to..";
    ImageView titleEditIcon;
    CustomBoldTextView wishTitle;
    String d,m,y;
    Button saveWishButton;
    EditText wishEditText;
    Calendar myCalendar = Calendar.getInstance();
    private static  final int REQUEST_FINE_LOCATION=2;
    private static final int LOCATION_SERVICE_REQUEST =3;
    HashMap<String,String> wishTypeHashMap = new HashMap<>();
    String[] wishTypes;
    JSONArray chosenWishTypes ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        showLog("user details id"+Constants.userDetailsId);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_layout, null);
        ImageView dateIcon = (ImageView)v.findViewById(R.id.dateIcon);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,WishesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        actionBar.setCustomView(v);

        titleEditIcon = (ImageView)findViewById(R.id.titleEditIcon);
        titleEditIcon.setOnClickListener(this);

        saveWishButton = (Button)findViewById(R.id.saveWishButton);
        saveWishButton.setOnClickListener(this);

        wishEditText = (EditText)findViewById(R.id.wishText);
        wishTitle = (CustomBoldTextView)findViewById(R.id.wishTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_items, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,ActivityWebView.class);
        switch (item.getItemId()){
            case R.id.action_about:{
                intent.putExtra("url","/about-us");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }break;
            case R.id.action_terms:{
                intent.putExtra("url","/terms-and-conditions");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }break;
            case R.id.action_privacy:{
                intent.putExtra("url","/privacy-policy");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }break;
            case R.id.action_rate:{
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName()));
                startActivity(intent);
            }break;
            case R.id.action_share:{
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Download the Igaze app from " + "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName() );
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                startActivity(Intent.createChooser(intent, "Share via..."));
            }break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.titleEditIcon :{
                View v = getLayoutInflater().inflate(R.layout.dialog_wish_title,null);
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                final EditText wishTitleEditText = (EditText)v.findViewById(R.id.dialogWishTitle);
                final CustomTextView submitTitle = (CustomTextView)v.findViewById(R.id.updateTitleButton);
                if(!wishTitleValue.equals("I would like to..")){
                    wishTitleEditText.setText(wishTitleValue);
                }

                submitTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(wishTitleEditText.getText().toString().length()>0){
                            wishTitleValue = wishTitleEditText.getText().toString();
                            wishTitle.setText(wishTitleValue);
                            titleDialog.dismiss();
                        }
                    }
                });

                builder.setView(v);
                titleDialog = builder.create();
                titleDialog.show();

            }break;

            case R.id.saveWishButton : {
                if(wishEditText.getText().toString().length()>0){
                    MyDatePickerDialog dialog = new MyDatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                    dialog.setPermanentTitle("Choose a target date");
                    dialog.show();
                }
            }break;
        }
    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            d = String.valueOf(dayOfMonth);
            m=String.valueOf(monthOfYear+1);
            y = String.valueOf(year);

            if(m.length()==1){
                m="0"+m;
            }
            if(d.length()==1){
                d="0"+d;
            }
            showLog(""+d+""+m+""+y);
            StringRequest stringRequest = new StringRequest(GET, GET_WISH_TYPES_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showLog("get wish type " + response);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        wishTypes = new String[jsonArray.length()];
                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            wishTypes[i] = jsonObject.getString("type");
                            wishTypeHashMap.put(jsonObject.getString("type"),jsonObject.getString("wishTypesId"));
                        }
                        chosenWishTypes = new JSONArray();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Choose wish type");
                        builder.setMultiChoiceItems(wishTypes, null, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("type", wishTypes[i]);
                                    jsonObject.put("wishTypesId",wishTypeHashMap.get(wishTypes[i]));
                                    showLog(i + " " + b);
                                    if (b) {
                                        chosenWishTypes.put(jsonObject);
                                    } else {
                                        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
                                        int len = chosenWishTypes.length();
                                        if (chosenWishTypes != null) {
                                            for (int j = 0; j < len; j++) {
                                                showLog(chosenWishTypes.getJSONObject(j).getString("wishTypesId")+" "+ wishTypes[i]);
                                                if(!chosenWishTypes.getJSONObject(j).getString("wishTypesId").equals(wishTypeHashMap.get(wishTypes[i]))) {
                                                    list.add(chosenWishTypes.getJSONObject(j));
                                                }
                                            }
                                            chosenWishTypes = new JSONArray(list);
                                        }

                                    }
                                    showLog(chosenWishTypes.toString());
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wishTypeDialog.dismiss();
                                addWish();
                            }
                        });
                        builder.setCancelable(false);
                        wishTypeDialog = builder.create();
                        wishTypeDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.fillInStackTrace();
                }
            });
            Constants.addRequest(getApplicationContext(), stringRequest);
        }

    };
    public void addWish(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    3);
        }else {
            uploadWish();
        }
    }
    public void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    uploadWish();
                }else {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_WISH_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showLog("add wish: " + response);
                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getString("flag").equals("1")){
                                    Snackbar.make(saveWishButton,"Wish saved successfully", Snackbar.LENGTH_LONG).show();
                                }else{
                                    Snackbar.make(saveWishButton,"Could not save your wish", Snackbar.LENGTH_LONG).show();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.fillInStackTrace();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            map.put("wishPrefix", wishTitleValue);
                            map.put("wish", wishEditText.getText().toString());
                            map.put("targetDate", y + "-" + m + "-" +d);
                            map.put("sharingType", "1");
                            map.put("latitude", "");
                            map.put("longitude", "");
                            map.put("userDetailsId", Constants.userDetailsId);
                            map.put("types", chosenWishTypes.toString());
                            showLog(map.toString());
                            return map;
                        }
                    };
                    Constants.addRequest(getApplicationContext(), stringRequest);
                }
            }
        }
    }
    public void uploadWish(){
        if(isLocationEnabled(getApplicationContext())){
            final GPSTracker tracker = new GPSTracker(getApplicationContext());
            if(tracker.canGetLocation()) {
                showLog("here");
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_WISH_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showLog("add wish: " + response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("flag").equals("1")){
                                Snackbar.make(saveWishButton,"Wish saved successfully", Snackbar.LENGTH_LONG).show();
                            }else{
                                Snackbar.make(saveWishButton,"Could not save your wish", Snackbar.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.fillInStackTrace();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("wishPrefix", wishTitleValue);
                        map.put("wish", wishEditText.getText().toString());
                        map.put("targetDate", y + "-" + m + "-" + d);
                        map.put("sharingType", "1");
                        map.put("latitude", String.valueOf(tracker.getLatitude()));
                        map.put("longitude", String.valueOf(tracker.getLongitude()));
                        map.put("userDetailsId", Constants.userDetailsId);
                        map.put("types", chosenWishTypes.toString());
                        showLog(map.toString());
                        return map;
                    }
                };
                Constants.addRequest(getApplicationContext(), stringRequest);
            }
        }else{
            displayLocationSettingsRequest(getApplicationContext(),LOCATION_SERVICE_REQUEST);
        }

    }
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    private void displayLocationSettingsRequest(Context context, final int requestCode) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        showLog("Location setting satisfied");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        showLog("not satisfied");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, requestCode);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOCATION_SERVICE_REQUEST : {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showLog("now called alert");
                                final GPSTracker tracker = new GPSTracker(getApplicationContext());
                                if (tracker.canGetLocation()) {
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_WISH_URL, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            showLog("add wish: " + response);
                                            try{
                                                JSONObject jsonObject = new JSONObject(response);
                                                if(jsonObject.getString("flag").equals("1")){
                                                    Snackbar.make(saveWishButton,"Wish saved successfully", Snackbar.LENGTH_LONG).show();
                                                }else{
                                                    Snackbar.make(saveWishButton,"Could not save your wish", Snackbar.LENGTH_LONG).show();
                                                }
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            error.fillInStackTrace();
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            map.put("wishPrefix", wishTitleValue);
                                            map.put("wish", wishEditText.getText().toString());
                                            map.put("targetDate", y + "-" + m + "-" + d);
                                            map.put("sharingType", "1");
                                            map.put("latitude", String.valueOf(tracker.getLatitude()));
                                            map.put("longitude", String.valueOf(tracker.getLongitude()));
                                            map.put("userDetailsId", Constants.userDetailsId);
                                            map.put("types", chosenWishTypes.toString());
                                            showLog(map.toString());
                                            return map;
                                        }
                                    };
                                    Constants.addRequest(getApplicationContext(), stringRequest);
                                }
                            }
                        }, 2000);
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ADD_WISH_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showLog("add wish: " + response);
                                try{
                                    JSONObject jsonObject = new JSONObject(response);
                                    if(jsonObject.getString("flag").equals("1")){
                                        Snackbar.make(saveWishButton,"Wish saved successfully", Snackbar.LENGTH_LONG).show();
                                    }else{
                                        Snackbar.make(saveWishButton,"Could not save your wish", Snackbar.LENGTH_LONG).show();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.fillInStackTrace();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("wishPrefix", wishTitleValue);
                                map.put("wish", wishEditText.getText().toString());
                                map.put("targetDate", y + "-" + m + "-" + d);
                                map.put("sharingType", "1");
                                map.put("latitude", "");
                                map.put("longitude", "");
                                map.put("userDetailsId", Constants.userDetailsId);
                                map.put("types", chosenWishTypes.toString());
                                showLog(map.toString());
                                return map;
                            }
                        };
                        Constants.addRequest(getApplicationContext(), stringRequest);
                    }
                }
            }break;
        }
    }
}
