package com.se.ding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.se.ding.network.Client;
import com.se.ding.network.Stream;
import com.se.ding.network.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button mLiveButton;
    private Button mLogoutButton;
    private RecyclerView videoCatalog;
    private VideoAdapter videoAdapter;

    private List<Video> videoList = new ArrayList<>(); // List containing all video data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Extract access token
        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String accessToken = sharedPref.getString("access_token", null);
        if (accessToken == null) {
            // User needs to log in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mLiveButton = findViewById(R.id.live_button);
        mLogoutButton = findViewById(R.id.logout_button);

        // Create video list view using recyclerview with custom adapter
        videoCatalog = findViewById(R.id.video_catalog);
        videoCatalog.setLayoutManager(new GridLayoutManager(this, 1));
        videoAdapter = new VideoAdapter(videoList, this);
        videoCatalog.setAdapter(videoAdapter);

        // Populate video list with data from server
        Call<List<Video>> call = Client.getService().getVideoCatalog("Bearer " + accessToken);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful()) {
                    Log.d("WEB", "Videos received: " + response.body().toString());
                    videoList.clear();
                    videoList.addAll(response.body());
                    videoAdapter.notifyDataSetChanged();
                    // Display the video catalog
                } else {
                    Log.d("WEB", "Video retrieval failed");
                    Toast.makeText(MainActivity.this, "Video retrieval failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("WEB", t.toString());
            }
        });

        // Logout and return to login menu
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("access_token", null);
                editor.apply();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Try to start camera stream viewing
        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for token
                SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                String accessToken = sharedPref.getString("access_token", null);
                if (accessToken != null) {
                    Call<Stream> call = Client.getService().startStream("Bearer " + accessToken);
                    call.enqueue(new Callback<Stream>() {
                        @Override
                        public void onResponse(Call<Stream> call, Response<Stream> response) {
                            if (response.isSuccessful()) {
                                // Start a new activity to play the stream
                                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                                String streamURL = "rtsp://" + Client.getHOST() + ":8554/stream";
                                intent.putExtra("videoUrl", streamURL);
                                intent.putExtra("isStreaming", true);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to start the stream", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Stream> call, Throwable t) {
                            // Handle the error
                            Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // User needs to log in
                    Toast.makeText(MainActivity.this, "Login Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}