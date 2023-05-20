package com.se.ding;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.se.ding.network.Notification;
import com.se.ding.network.Video;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notificationItems;
    private RecyclerView videosRecyclerView;

    public NotificationAdapter(List<Notification> notificationItems, RecyclerView videosRecyclerView) {
        this.notificationItems = notificationItems;
        this.videosRecyclerView = videosRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notificationItem = notificationItems.get(position);
        holder.showButton.setText(notificationItem.getDateTime());

        holder.showButton.setOnClickListener(v -> {
            // Scroll to the video indicated by the item's ID
            int targetPosition = findVideoPositionById(notificationItem.getVideoID());
            VideoAdapter videoAdapter = (VideoAdapter) videosRecyclerView.getAdapter();
            videoAdapter.setHighlightedPosition(targetPosition);
            scrollToVideoPosition(targetPosition);
        });
    }

    private void scrollToVideoPosition(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) videosRecyclerView.getLayoutManager();

        // Calculate the offset to ensure the desired video appears at the top
        int offset = 0;
        if (layoutManager != null && layoutManager.findViewByPosition(position) != null) {
            View targetView = layoutManager.findViewByPosition(position);
            offset = videosRecyclerView.getPaddingTop() - targetView.getTop();
        }

        // Scroll to the desired position with the calculated offset
        layoutManager.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    private int findVideoPositionById(String videoId) {
        /*for (int i = 0; i < videosRecyclerView.getAdapter().getItemCount(); i++) {
            Video video = (Video) videosRecyclerView.getAdapter().getItem(i);
            if (video.getId() == videoId) {
                return i;
            }
        }*/
        return 16;
        //return RecyclerView.NO_POSITION; // Return NO_POSITION if video ID is not found
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button showButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showButton = itemView.findViewById(R.id.button_show_notification);
        }
    }
}
