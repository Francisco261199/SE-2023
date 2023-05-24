package com.se.ding.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface WebInterface {
    @POST("/user/login")
    Call<AccessToken> login(@Body LoginRequest loginRequest);

    @POST("/user/new")
    Call<Void> createUser(@Body User user);

    @GET("/videos")
    Call<List<Video>> getVideoCatalog(@Header("Authorization") String accessToken);

    @GET("/live")
    Call<Video> getCameraFeed(@Header("Authorization") String accessToken);
}