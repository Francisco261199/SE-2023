package com.example.watchdog.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static final String BASE_URL = "http://<raspberry-pi-ip>:<port>/";
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

}