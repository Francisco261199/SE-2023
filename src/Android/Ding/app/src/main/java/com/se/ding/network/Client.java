package com.se.ding.network;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static String BASE_URL = "http://192.168.1.7:3000/";

    public static WebInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebInterface.class);
    }

    public static void setBaseURL(String url) {
        BASE_URL = url;
    }

    public static String getBaseURL() {
        return BASE_URL;
    }

}