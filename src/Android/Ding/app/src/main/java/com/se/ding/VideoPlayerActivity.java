package com.se.ding;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.se.ding.network.Client;
import com.se.ding.network.Stream;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class VideoPlayerActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        libVLC = new LibVLC(this);
        mediaPlayer = new MediaPlayer(libVLC);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.getVLCVout().setVideoSurface(holder.getSurface(), null);
        mediaPlayer.getVLCVout().attachViews();

        Uri uri = Uri.parse(getIntent().getStringExtra("videoUrl"));
        Media media = new Media(libVLC, uri);
        mediaPlayer.setMedia(media);
        media.release();

        mediaPlayer.play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaPlayer.stop();
        mediaPlayer.release();
        libVLC.release();
    }
}