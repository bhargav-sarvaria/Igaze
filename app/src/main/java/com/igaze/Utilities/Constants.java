package com.igaze.Utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by ascent-3 on 17/5/18.
 */

public class Constants {

    public static String userId = "1";
    public static String contactNo = "";
    public static String userDetailsId = "";

    public static String baseUrl = "https://igaze.com";
    public static String REGISTER_DEVICE_URL = "https://igaze.app/apis/registerDevice.php";
    public static String SEND_OTP_URL = "https://igaze.app/apis/sendOtp.php";
    public static String VERIFY_OTP_URL = "https://igaze.app/apis/verifyOtp.php";
    public static String ADD_WISH_URL = "https://igaze.app/apis/addWish.php";
    public static String GET_WISHES_URL = "https://igaze.app/apis/getWishes.php?userDetailsId=%s";
    public static String DELETE_WISH_URL = "https://igaze.app/apis/deleteWish.php";
    public static String EDIT_WISH_URL = "https://igaze.app/apis/updateWish.php";
    public static String WISH_HISTORY_URL = "https://igaze.app/apis/getWishHistory.php";
    public static String GET_WISH_TYPES_URL = "https://igaze.app/apis/getWishTypes.php";

    private static RequestQueue requestQueue;
    public static void addRequest(Context context, StringRequest stringRequest) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }
    public static void showLog(String s) {
        Log.d("Dev110", s);
    }
}
