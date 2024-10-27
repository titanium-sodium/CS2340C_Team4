package com.example.sprintproject.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sprintproject.model.NotesModel;
import com.example.sprintproject.views.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class NotesViewModel {

    private MutableLiveData<String> noteMessage;
    private NotesModel notesModel;

    String userId = MainActivity.getUserId();
    DatabaseReference DB = new DBViewModel().getDB();

    public NotesViewModel() {

        noteMessage = new MutableLiveData<>();

        notesModel = new NotesModel("");

        DB.child("users").child(userId).child("destinations").child("notes").child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            NotesModel note = snapshot.getValue(NotesModel.class);

                            if (note != null) {

                                noteMessage.setValue(note.getNotes());
                                notesModel.setNotes(note.getNotes());

                            } else {

                                notesModel.setNotes("");
                                noteMessage.setValue("");
                                DB.child("users").child(userId).child("destinations")
                                        .child("notes").child(userId).setValue("");

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //your words are as empty, empty
                    }
                });

    }

    public LiveData<String> getMessage() {

        return noteMessage;

    }

    public void updateMessage(String note) {

        notesModel.setNotes(note);
        noteMessage.setValue(notesModel.getNotes());

        DB.child("users").child(userId).child("destinations")
                .child("notes").child(userId).setValue(note);

    }

}
