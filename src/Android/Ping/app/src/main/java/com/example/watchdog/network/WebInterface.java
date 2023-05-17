package com.example.watchdog.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface WebInterface {
    @POST("login")
    Call<AccessToken> login(@Body LoginRequest loginRequest);

    @POST("users")
    Call<Void> createUser(@Body User user);

    @GET("videos")
    Call<List<Video>> getVideoCatalog(@Header("Authorization") String accessToken);

    @GET("live")
    Call<Video> getCameraFeed(@Header("Authorization") String accessToken);
}