package com.example.sprintproject.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;
import com.example.sprintproject.model.CommunityPost;

import java.util.List;

public class CommunityPostAdapter
        extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {
    private List<CommunityPost> posts;

    public CommunityPostAdapter(List<CommunityPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<CommunityPost> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView durationText;
        private TextView destinationsText;
        private TextView accommodationsText;
        private TextView diningText;
        private TextView notesText;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            durationText = itemView.findViewById(R.id.durationText);
            destinationsText = itemView.findViewById(R.id.destinationsText);
            accommodationsText = itemView.findViewById(R.id.accommodationsText);
            diningText = itemView.findViewById(R.id.diningText);
            notesText = itemView.findViewById(R.id.notesText);
        }

        public void bind(CommunityPost post) {
            durationText.setText(post.getDuration());
            destinationsText.setText(String.join(", ", post.getDestinations()));

            // Format accommodations
            StringBuilder accBuilder = new StringBuilder();
            post.getAccommodations().forEach((dest, acc) ->
                    accBuilder.append(dest).append(": ").append(acc).append("\n"));
            accommodationsText.setText(accBuilder.toString().trim());

            // Format dining
            StringBuilder diningBuilder = new StringBuilder();
            post.getDiningReservations().forEach((dest, dining) ->
                    diningBuilder.append(dest).append(": ").append(dining).append("\n"));
            diningText.setText(diningBuilder.toString().trim());

            notesText.setText(post.getNotes());
        }
    }
}