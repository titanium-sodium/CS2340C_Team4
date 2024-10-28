package com.example.sprintproject.viewmodels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprintproject.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<String> notes;
    private List<String> usernames;

    public NotesAdapter(List<String> notes, List<String> usernames) {

        this.notes = notes;
        this.usernames = usernames;

    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {

        holder.usernameText.setText(usernames.get(position));
        holder.noteText.setText(notes.get(position));

    }

    @Override
    public int getItemCount() {

        return notes.size();

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView noteText;
        private TextView usernameText;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);
            noteText = itemView.findViewById(R.id.noteText);
            usernameText = itemView.findViewById(R.id.usernameText);

        }

        public TextView getNoteText() {
            return noteText;
        }
        public TextView getUsernameText() {
            return usernameText;
        }
    }

}