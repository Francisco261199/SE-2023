package com.se.ding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.se.ding.network.Client;
import com.se.ding.network.Notification;
import com.se.ding.network.Video;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private Button mLiveButton;
    private Button mLogoutButton;
    private Button mNotificationsButton;
    private RecyclerView videoCatalog;
    private VideoAdapter videoAdapter;

    private List<Video> videoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mNotificationsButton = findViewById(R.id.notifications_button);

        /*videoCatalog = findViewById(R.id.video_catalog);
        videoCatalog.setLayoutManager(new GridLayoutManager(this, 1));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoList.add(new Video("https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "2023-05-18"));
        videoAdapter = new VideoAdapter(videoList, this);
        videoCatalog.setAdapter(videoAdapter);*/

        // Notify the adapter that a new item has been inserted
        /*videoAdapter.notifyItemInserted(videoList.size() - 1);
        videoList.add(newVideo2);
        videoAdapter.notifyItemInserted(videoList.size() - 1);*/

        mNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropdownMenu(v);
            }
        });

        Call<List<Video>> call = Client.getService().getVideoCatalog(accessToken);
        call.enqueue(new Callback<List<Video>>() {
            @Override
            public void onResponse(Call<List<Video>> call, Response<List<Video>> response) {
                if (response.isSuccessful()) {
                    videoList = response.body();
                    videoAdapter.notifyDataSetChanged();
                    // Display the video catalog
                } else {
                    // Handle the error
                }
            }

            @Override
            public void onFailure(Call<List<Video>> call, Throwable t) {
                // Handle the error
                Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

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

        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
                String accessToken = sharedPref.getString("access_token", null);
                if (accessToken != null) {
                    Call<Video> call = Client.getService().getCameraFeed(accessToken);
                    call.enqueue(new Callback<Video>() {
                        @Override
                        public void onResponse(Call<Video> call, Response<Video> response) {
                            if (response.isSuccessful()) {
                                String url = response.body().getUrl();
                                // Start a new activity to play the video
                                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                                intent.putExtra("videoUrl", url);
                                startActivity(intent);
                            } else {
                                // Handle the error
                            }
                        }

                        @Override
                        public void onFailure(Call<Video> call, Throwable t) {
                            // Handle the error
                            Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // User needs to log in
                    Toast.makeText(MainActivity.this, "Login Required", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void showDropdownMenu(View anchorView) {
        // Inflate the dropdown_menu layout
        View contentView = getLayoutInflater().inflate(R.layout.notifications_layout, null);

        // Create a new PopupWindow with the inflated layout
        PopupWindow popupWindow = new PopupWindow(contentView,
                700,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        // Set the background drawable for the PopupWindow
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Set the elevation for the PopupWindow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(8f);
        }

        // Find the RecyclerView in the dropdown_menu layout
        RecyclerView recyclerView = contentView.findViewById(R.id.notifications_view);

        // Create and set the adapter for the RecyclerView
        List<Notification> notificationsList = new ArrayList<>();
        notificationsList.add(new Notification("0", "2023-05-18"));
        notificationsList.add(new Notification("1", "2023-05-20"));
        NotificationAdapter videoAdapter = new NotificationAdapter(notificationsList, videoCatalog); // Replace videoList with your actual list of videos
        recyclerView.setAdapter(videoAdapter);

        // Set the layout manager for the RecyclerView (e.g., LinearLayoutManager)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show the PopupWindow below the anchor view
        popupWindow.showAsDropDown(anchorView);
    }
}