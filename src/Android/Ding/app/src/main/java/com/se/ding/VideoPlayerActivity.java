package com.se.ding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.se.ding.network.Client;
import com.se.ding.network.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoPlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Boolean isStreaming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        isStreaming = getIntent().getExtras().getBoolean("isStreaming");

        playerView = findViewById(R.id.player_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        if(isStreaming) {
            SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
            String accessToken = sharedPref.getString("access_token", null);
            if (accessToken != null) {
                Call<Void> call = Client.getService().stopStream("Bearer " + accessToken);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                    }
                });
            }
        }
    }

    private void initializePlayer() {
        // Create a new ExoPlayer instance
        player = new SimpleExoPlayer.Builder(this).build();
        Toast.makeText(this, "Player Initialized", Toast.LENGTH_SHORT).show();

        // Attach the player to the player view
        playerView.setPlayer(player);

        // Create a media source pointing to the video URL
        Uri videoUri = Uri.parse(getIntent().getStringExtra("videoUrl"));
        Toast.makeText(this, "Video URL: " + videoUri, Toast.LENGTH_SHORT).show();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(videoUri);

        // Prepare the player with the media source
        player.setMediaSource(mediaSource);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    private void releasePlayer() {
        Toast.makeText(this, "Releasing player...", Toast.LENGTH_SHORT).show();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}