package com.se.ding.network;

import android.util.Log;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit client used to retrieve retrofit service, as well as control server address
public class Client {
    private static String HOST = "192.168.1.3";
    private static String PORT = "3000";

    public static WebInterface getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WebInterface.class);
    }

    public static void setHost(String host) {
        HOST = host;
    }

    public static String getHOST() {
        return HOST;
    }

    public static String getPORT() {
        return PORT;
    }

    public static void setPort(String port) {
        PORT = port;
    }

    public static String getBaseURL() {
        return "http://" + HOST + ":" + PORT;
    }

}