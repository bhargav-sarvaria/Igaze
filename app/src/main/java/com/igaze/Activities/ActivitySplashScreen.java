package com.igaze.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.igaze.R;
import com.igaze.Utilities.Constants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.igaze.Utilities.Constants.showLog;

public class ActivitySplashScreen extends AppCompatActivity {

    Thread splashTread;
    ImageView iv;
    private static final int RC_SIGN_IN=1;
    private GoogleApiClient googleApiClient;
    private final String TAG="Google sign in:";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        try {

            mAuth = FirebaseAuth.getInstance();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            if (isNetworkAvailable()) {
                final String token = FirebaseInstanceId.getInstance().getToken();
                showLog("FCM Token " + token);
                if (token != null) {
                    if (token.length() > 0) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.REGISTER_DEVICE_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                showLog("register device: " + response);
                                try{
                                    JSONObject jsonObject = new JSONObject(response);
                                    Constants.userId = jsonObject.getString("userId");
                                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("userId",Constants.userId).apply();
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
                                map.put("deviceId", Settings.Secure.getString(getApplicationContext().getContentResolver(),
                                        Settings.Secure.ANDROID_ID));
                                map.put("notificationId", token);
                                map.put("osType", "1");
                                return map;
                            }
                        };
                        Constants.addRequest(getApplicationContext(), stringRequest);
                    }
                }
            }
        }catch (Exception e ){
            showLog(e.getMessage());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        RelativeLayout l = (RelativeLayout)findViewById(R.id.splash_linear);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        iv = findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }

                } catch (InterruptedException ignored) {
                } finally {

                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("loggedIn", "0").equals("1")) {
                        Constants.userDetailsId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("udi", "0");
                        showLog("userdetails id in splash"+Constants.userDetailsId);
                        Constants.contactNo = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("userDetailsId", "0");


                        Intent intent = new Intent(ActivitySplashScreen.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        ActivitySplashScreen.this.finish();
                    }else {

                        Intent intent = new Intent(ActivitySplashScreen.this,ActivitySignIn.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        ActivitySplashScreen.this.finish();
                    }
                }
            }
        };
        splashTread.start();

    }private boolean isNetworkAvailable() {
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
}
