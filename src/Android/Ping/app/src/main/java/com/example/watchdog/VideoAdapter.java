package com.example.watchdog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.watchdog.network.Video;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private List<Video> videoItems;
    private Context context;

    public VideoAdapter(List<Video> videoItems, Context context) {
        this.videoItems = videoItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video videoItem = videoItems.get(position);
        holder.buttonVideo.setText(videoItem.getDateTime());

        // Set the video URL as the button's tag
        holder.buttonVideo.setTag(videoItem.getUrl());
    }

    @Override
    public int getItemCount() {
        return videoItems.size();
    }

    public List<Video> getVideoList() {
        return videoItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button buttonVideo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonVideo = itemView.findViewById(R.id.button_video);
            buttonVideo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Retrieve the video URL from the button's tag
            String videoUrl = (String) buttonVideo.getTag();

            // Start a new activity to play the video
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", videoUrl);
            context.startActivity(intent);
        }
    }
}
