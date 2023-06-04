package com.se.ding.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

// Web interface used to communicate with server
public interface WebInterface {
    @POST("/user/login")
    Call<Token> login(@Body LoginRequest loginRequest);

    @POST("/user/new")
    Call<Void> createUser(@Body User user);

    @GET("/videos")
    Call<List<Video>> getVideoCatalog(@Header("Authorization") String accessToken);

    @GET("/videos/delete")
    Call<Void> deleteVideo(@Header("Authorization") String accessToken, @Query("videoId") String videoId);

    @POST("/notifications/registerDevice")
    Call<Void> registerDevice(@Body Token registrationToken);

    @GET("/stream/start")
    Call<Stream> startStream(@Header("Authorization") String accessToken);

    @GET("/stream/stop")
    Call<Void> stopStream(@Header("Authorization") String accessToken);
}