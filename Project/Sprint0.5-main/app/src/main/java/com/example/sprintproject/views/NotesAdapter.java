package com.example.sprintproject.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sprintproject.R;
import com.example.sprintproject.model.NotesModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NotesModel> notes;
    private SimpleDateFormat dateFormat;

    public NotesAdapter(List<NotesModel> notes) {
        this.notes = notes;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NotesModel note = notes.get(position);
        holder.noteText.setText(note.getNotes());
        holder.userEmail.setText(note.getUserEmail());
        String date = dateFormat.format(new Date(note.getTimestamp()));
        holder.timestamp.setText(date);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateNotes(List<NotesModel> newNotes) {
        this.notes = newNotes;
        notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteText;
        TextView userEmail;
        TextView timestamp;

        NoteViewHolder(View itemView) {
            super(itemView);
            noteText = itemView.findViewById(R.id.noteText);
            userEmail = itemView.findViewById(R.id.userEmail);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}