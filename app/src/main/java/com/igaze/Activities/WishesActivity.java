package com.igaze.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bluelinelabs.logansquare.LoganSquare;
import com.igaze.Adapter.WishAdapter;
import com.igaze.Models.Wish;
import com.igaze.R;
import com.igaze.Utilities.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method.GET;
import static com.igaze.Utilities.Constants.VERIFY_OTP_URL;
import static com.igaze.Utilities.Constants.showLog;

public class WishesActivity extends AppCompatActivity implements WishAdapter.ClickListener {

    Toolbar toolbar;
    RecyclerView recyclerView;
    WishAdapter cardAdapter;
    List<Wish> cardList;
    AlertDialog confirmDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishes);

        toolbar = findViewById(R.id.toolbarWishes);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.action_bar_layout, null);
        ImageView dateIcon = (ImageView)v.findViewById(R.id.dateIcon);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        actionBar.setCustomView(v);

        recyclerView = (RecyclerView)findViewById(R.id.wishesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        String url = String.format(Constants.GET_WISHES_URL,Constants.userDetailsId);
        StringRequest stringRequest = new StringRequest(GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showLog("get contacts "+response);
                try{
                    showLog("1");
                    cardList = LoganSquare.parseList(response, Wish.class);
                    showLog("2");
                    cardAdapter = new WishAdapter(getApplicationContext(), cardList);
                    showLog("3");
                    cardAdapter.setClickListener(WishesActivity.this);
                    showLog("4");
                    recyclerView.setAdapter(cardAdapter);
                    showLog("5");

                }catch (Exception e){
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

    @Override
    public void onClick(View view, final Wish productCard) {
        switch (view.getId()){
            case R.id.wishDeleteIcon:{
                final AlertDialog.Builder builder = new AlertDialog.Builder(WishesActivity.this);
                builder.setMessage("Do you want to remove the wish?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDialog.dismiss();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.DELETE_WISH_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showLog("delete wish: "+response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String flag = jsonObject.getString("flag");
                                    if(flag.equals("1")){
                                        startActivity(new Intent(WishesActivity.this,WishesActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        Snackbar.make(recyclerView,"Wish removed successfully", Snackbar.LENGTH_LONG).show();
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
                                map.put("wishId", productCard.getWishId());
                                return map;
                            }
                        };
                        Constants.addRequest(getApplicationContext(),stringRequest);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmDialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                confirmDialog = builder.create();
                confirmDialog.show();

            }break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_items, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(WishesActivity.this,ActivityWebView.class);
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
}
