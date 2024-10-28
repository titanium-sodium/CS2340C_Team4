package com.example.sprintproject.model;

public class NotesModel {

    private String notes;

    public NotesModel(String notes) throws IllegalArgumentException {
        if (notes.length() == 0) {
            throw new IllegalArgumentException("Note cannot be empty");
        }
        this.notes = notes;

    }

    public String getNotes() {

        return notes;

    }

    public void setNotes(String notes) {

        this.notes = notes;

    }

}
