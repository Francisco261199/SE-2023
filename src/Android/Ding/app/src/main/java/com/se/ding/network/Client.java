package com.se.ding.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static String BASE_URL = "http://192.168.1.7:3000/";
    private static WebInterface service;

    public static WebInterface getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(WebInterface.class);
        }
        return service;
    }

    public static void setBaseURL(String url) {
        BASE_URL = url;
    }

    public static String getBaseURL() {
        return BASE_URL;
    }

}