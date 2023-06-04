package com.se.ding;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.se.ding.network.Client;
import com.se.ding.network.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Adapter for video recyclerview
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private List<Video> videoItems;
    private Context context;

    public VideoAdapter(List<Video> videoItems, Context context) {
        this.videoItems = videoItems;
        this.context = context;
    }

    public void setHighlightedPosition(int position) {
        notifyDataSetChanged();
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
        holder.videoId = videoItem.getId();

        // set the video content as the datetime
        holder.buttonVideo.setText(videoItem.getDatetime());

        // Set the video URL as the button's tag
        holder.buttonVideo.setTag(videoItem.getPath());
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
        Button buttonDelete;
        String videoId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonVideo = itemView.findViewById(R.id.button_video);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonVideo.setOnClickListener(this);
            // Delete video button
            buttonDelete.setOnClickListener(view -> {
                // Get access token
                SharedPreferences sharedPref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
                String accessToken = sharedPref.getString("access_token", null);
                if (accessToken == null) {
                    // User needs to log in
                    Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                } else {
                    // Delete request
                    Call<Void> call = Client.getService().deleteVideo("Bearer " + accessToken, videoId);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("WEB", "Video deleted");
                                Toast.makeText(context, "Video deleted", Toast.LENGTH_SHORT).show();
                                int adapterPosition = getAbsoluteAdapterPosition();
                                videoItems.remove(adapterPosition);
                                notifyItemRemoved(adapterPosition);
                            } else {
                                Log.d("WEB", "Video deletion failed");
                                Toast.makeText(context, "Video deletion failed", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("WEB", t.toString());
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
            // Retrieve the video URL from the button's tag
            String videoUrl = Client.getBaseURL() + "/videos/" + (String) buttonVideo.getTag();

            // Start a new activity to play the video
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("videoUrl", videoUrl);
            intent.putExtra("isStreaming", false);
            context.startActivity(intent);
        }
    }
}
