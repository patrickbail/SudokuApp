package com.example.SudokuApp;

import java.util.HashSet;
import java.util.Set;

public class Cell {
    private String text;
    private Set<Integer> notes = new HashSet<>();
    private float textWidth;
    private float textHeight;
    private boolean isFalse;

    public Cell(String text, float textWidth, float textHeight, boolean isFalse) {
        this.text = text;
        this.textWidth = textWidth;
        this.textHeight = textHeight;
        this.isFalse = isFalse;
    }

    public Cell(int note) {
        this.notes.add(note);
    }

    public void addNote(int note) {
        notes.add(note);
    }

    public void removeNote(int note) {
        notes.remove(note);
    }

    public Set<Integer> getNotes() {
        return notes;
    }

    public String getText() {
        return text;
    }

    public float getTextWidth() {
        return textWidth;
    }

    public float getHeight() {
        return textHeight;
    }

    public boolean getIsFalse() {
        return isFalse;
    }

}
