package com.igaze.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.igaze.R;
import com.igaze.Utilities.Constants;
import com.igaze.Utilities.CustomTextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.igaze.Utilities.Constants.VERIFY_OTP_URL;
import static com.igaze.Utilities.Constants.showLog;

public class ActivitySignIn extends AppCompatActivity implements View.OnClickListener {

    CustomTextView submit,resend,validate,cancel;
    RelativeLayout mobileNoLayout,otpLayout;

    final int REQUEST_READ_SMS =1;

    public static TextInputEditText contactNo,otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        checkReadSmsPermission();

        contactNo = (TextInputEditText)findViewById(R.id.contactNo);
        otp = (TextInputEditText)findViewById(R.id.otp);

        submit = (CustomTextView)findViewById(R.id.contactNoSubmitButton);
        resend = (CustomTextView)findViewById(R.id.resendOtpButton);
        validate = (CustomTextView)findViewById(R.id.validateOtpButton);
        cancel = (CustomTextView)findViewById(R.id.cancelButton);
        mobileNoLayout = (RelativeLayout)findViewById(R.id.mobileNoLayout);
        otpLayout = (RelativeLayout)findViewById(R.id.otpLayout);

        submit.setOnClickListener(this);
        resend.setOnClickListener(this);
        validate.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contactNoSubmitButton : {
                if(isNetworkAvailable()){
                        sendOtp();
                    ActivitySignIn.this.runOnUiThread(new Runnable(){
                        public void run() {
                            Animation in = AnimationUtils.loadAnimation(ActivitySignIn.this,R.anim.fast_alpha);
                            in.reset();
                            otpLayout.clearAnimation();
                            otpLayout.startAnimation(in);
                            otpLayout.setVisibility(View.VISIBLE);

                            Animation out = AnimationUtils.loadAnimation(ActivitySignIn.this,R.anim.reverse_fast_alpha);
                            out.reset();
                            mobileNoLayout.clearAnimation();
                            mobileNoLayout.startAnimation(out);
                            mobileNoLayout.setVisibility(View.GONE);
                        }

                    });

                }else {
                    Snackbar.make(mobileNoLayout, "Please check your Internet Connection and Retry", Snackbar.LENGTH_LONG).show();
                }
            }break;
            case R.id.cancelButton : {
                ActivitySignIn.this.runOnUiThread(new Runnable(){
                    public void run() {
                        Animation in = AnimationUtils.loadAnimation(ActivitySignIn.this,R.anim.fast_alpha);
                        in.reset();
                        mobileNoLayout.clearAnimation();
                        mobileNoLayout.startAnimation(in);
                        mobileNoLayout.setVisibility(View.VISIBLE);

                        Animation out = AnimationUtils.loadAnimation(ActivitySignIn.this,R.anim.reverse_fast_alpha);
                        out.reset();
                        otpLayout.clearAnimation();
                        otpLayout.startAnimation(out);
                        otpLayout.setVisibility(View.GONE);
                    }

                });
            }break;
            case R.id.resendOtpButton : {
                if(isNetworkAvailable()){
                    checkReadSmsPermission();
                    sendOtp();
                }else {
                    Snackbar.make(mobileNoLayout, "Please check your Internet Connection and Retry", Snackbar.LENGTH_LONG).show();
                }
            }break;
            case R.id.validateOtpButton : {
                if(isNetworkAvailable()){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, VERIFY_OTP_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            showLog("verify otp: "+response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String flag = jsonObject.getString("flag");
                                Constants.userDetailsId = jsonObject.getString("userDetailsId");
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("udi",Constants.userDetailsId).apply();

                                if(flag.equals("1")){
                                    Constants.userDetailsId = jsonObject.getString("userDetailsId");
                                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("loggedIn","1").apply();
                                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userDetailsId",Constants.userDetailsId).apply();
                                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("contactNo",Constants.contactNo).apply();

                                    Intent intent = new Intent(ActivitySignIn.this,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
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
                            map.put("contactNo", contactNo.getText().toString());
                            map.put("userId",Constants.userId);
                            map.put("otp",otp.getText().toString());
                            return map;
                        }
                    };
                    Constants.addRequest(getApplicationContext(),stringRequest);
                }else{
                    Snackbar.make(mobileNoLayout, "Please check your Internet Connection and Retry", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch(Exception e){
            Log.d("network error",e.getMessage());
            return false;
        }
    }
    public void checkReadSmsPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                    REQUEST_READ_SMS);
        }else{

        }
    }
    public void sendOtp(){
        Constants.contactNo = contactNo.getText().toString();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.SEND_OTP_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showLog("send otp: "+response);
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
                map.put("contactNo", contactNo.getText().toString());
                return map;
            }
        };
        Constants.addRequest(getApplicationContext(),stringRequest);
        checkReadSmsPermission();
    }
}