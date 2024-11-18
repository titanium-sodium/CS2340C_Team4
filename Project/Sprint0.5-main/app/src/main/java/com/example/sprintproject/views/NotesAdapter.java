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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<NotesModel> notes;
    private final SimpleDateFormat dateFormat;
    private SortOrder currentSortOrder = SortOrder.NEWEST_FIRST;

    public enum SortOrder {
        NEWEST_FIRST,
        OLDEST_FIRST
    }

    public NotesAdapter(List<NotesModel> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        sortNotes(currentSortOrder);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NotesModel note = notes.get(position);

        // Set the note text
        if (holder.noteText != null) {
            holder.noteText.setText(note.getNotes());
        }

        // Set username (previously was userEmail)
        if (holder.usernameText != null) {
            holder.usernameText.setText(note.getUserEmail());
        }

        // Set location if available
        if (holder.location != null) {
            if (note.getLocation() != null && !note.getLocation().isEmpty()) {
                holder.location.setVisibility(View.VISIBLE);
                holder.location.setText("Location: " + note.getLocation());
            } else {
                holder.location.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateNotes(List<NotesModel> newNotes) {
        this.notes = newNotes != null ? newNotes : new ArrayList<>();
        sortNotes(currentSortOrder);
        notifyDataSetChanged();
    }

    public void setSortOrder(SortOrder sortOrder) {
        if (currentSortOrder != sortOrder) {
            currentSortOrder = sortOrder;
            sortNotes(sortOrder);
            notifyDataSetChanged();
        }
    }

    private void sortNotes(SortOrder sortOrder) {
        Collections.sort(notes, (note1, note2) -> {
            switch (sortOrder) {
                case NEWEST_FIRST:
                    return Long.compare(note2.getTimestamp(), note1.getTimestamp());
                case OLDEST_FIRST:
                    return Long.compare(note1.getTimestamp(), note2.getTimestamp());
                default:
                    return 0;
            }
        });
    }

    public void filterByTrip(String tripId) {
        if (tripId == null || tripId.isEmpty()) {
            return;
        }
        List<NotesModel> filteredNotes = new ArrayList<>();
        for (NotesModel note : notes) {
            if (tripId.equals(note.getTripId())) {
                filteredNotes.add(note);
            }
        }
        updateNotes(filteredNotes);
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView noteText;
        private final TextView usernameText;
        private final TextView location;

        NoteViewHolder(View itemView) {
            super(itemView);
            // Updated to match layout IDs
            noteText = itemView.findViewById(R.id.noteText);
            usernameText = itemView.findViewById(R.id.usernameText);
            location = itemView.findViewById(R.id.location);
        }
    }
}