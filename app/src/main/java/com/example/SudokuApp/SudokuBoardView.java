package com.example.SudokuApp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SudokuBoardView extends View {
    private int sqrSize = 3;
    private int size = 9;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    private float cellSizePixels = 0F;
    private float noteSizePixels = 0F;
    private boolean isFalse = false;
    private Cell[][] valueBoard = new Cell[9][9];
    private List<Pair<Integer,Integer>> startingCells = new ArrayList<>();
    private Paint thickline = new Paint();
    private Paint thinline = new Paint();
    private Paint selectedCellPaint = new Paint();
    private Paint relatedCellPaint = new Paint();
    private Paint startingCellPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint falseTextPaint = new Paint();
    private Paint noteTextPaint = new Paint();

    public SudokuBoardView(Context context, AttributeSet attributeSet) {
        super(context,attributeSet);

        this.thickline.setStyle(Paint.Style.STROKE);
        this.thickline.setColor(Color.BLACK);
        this.thickline.setStrokeWidth(7F);

        this.thinline.setStyle(Paint.Style.STROKE);
        this.thinline.setColor(Color.BLACK);
        this.thinline.setStrokeWidth(2F);

        this.selectedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.selectedCellPaint.setColor(Color.parseColor("#AB169304"));

        this.relatedCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.relatedCellPaint.setColor(Color.parseColor("#1D26EC09")); //#2A26EC09

        this.startingCellPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.startingCellPaint.setColor(Color.parseColor("#B2888888"));

        this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.textPaint.setColor(Color.BLACK);
        this.textPaint.setTextSize(70F);

        this.falseTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.falseTextPaint.setColor(Color.RED);
        this.falseTextPaint.setTextSize(70F);

        this.noteTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.noteTextPaint.setColor(Color.DKGRAY);
    }

    //Grid will be adjusted to a square
    @Override
    public void onMeasure(int width, int height) {
        super.onMeasure(width,height);
        int sizePixels = Math.min(width,height);
        setMeasuredDimension(sizePixels,sizePixels);
    }

    @Override
    public void onDraw(Canvas canvas) {
        cellSizePixels = (float) (getWidth() / size);
        noteSizePixels = cellSizePixels / sqrSize;
        noteTextPaint.setTextSize(noteSizePixels);
        fillCells(canvas);
        drawText(canvas);
        drawLines(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handleTouchEvent(event.getX(),event.getY());
            return true;
        }
        else {
            return false;
        }
    }

    public void setIsFalse(boolean b) {
        isFalse = b;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public int getSelectedColumn() {
        return selectedColumn;
    }

    public Cell[][] getValueBoard() {
        return valueBoard;
    }

    public void resetSelRowCol() {
        selectedRow = -1;
        selectedColumn = -1;
    }

    //Sets the text of current cell position or deletes it
    public void setCell(String text) {
        if (selectedRow != -1 || selectedColumn != -1) {
            if (text.equals("del")) {
                System.out.println("Delete at " + selectedRow + " " + selectedColumn);
                valueBoard[selectedRow][selectedColumn] = null;
            }
            else {
                Rect textBounds = new Rect();
                textPaint.getTextBounds(text, 0, text.length(), textBounds);
                float textWidth = textPaint.measureText(text);
                float textHeight = textBounds.height();
                valueBoard[selectedRow][selectedColumn] = new Cell(text,textWidth,textHeight,isFalse);
                isFalse = false;

                //Check if related cell notes contain notes equal to given text
                //Not done

                System.out.println("SelectedRow " + selectedRow);
                System.out.println("SelectedColumn " + selectedColumn);
                System.out.println("------------------");
                System.out.println("X " + ((selectedColumn * cellSizePixels) + cellSizePixels / 2 - textWidth / 2));
                System.out.println("Y " + ((selectedRow * cellSizePixels) + cellSizePixels / 2 - textHeight / 2) + 35);

            }
            invalidate();
        }
    }

    public void setCellNotes(int note) {
        if (selectedRow != -1 || selectedColumn != -1) {
            //If cell is empty create new cell with only notes
            if (valueBoard[selectedRow][selectedColumn] == null) {
                valueBoard[selectedRow][selectedColumn] = new Cell(note);
            }
            //If cell already has notes add the new note to the set or remove if set already contains given note
            else if (!(valueBoard[selectedRow][selectedColumn].getNotes().isEmpty())) {
                if (valueBoard[selectedRow][selectedColumn].getNotes().contains(note)) {
                    valueBoard[selectedRow][selectedColumn].removeNote(note);
                    //If Set empty then make cell equal null
                    if (valueBoard[selectedRow][selectedColumn].getNotes().isEmpty()) {
                        valueBoard[selectedRow][selectedColumn] = null;
                    }
                }
                else {
                    valueBoard[selectedRow][selectedColumn].addNote(note);
                }
            }
        }
        invalidate();
    }

    //Sets the starting sudoku board up and marks all starting cells
    public void setValueBoard(int[][] board) {
        valueBoard = new Cell[9][9];
        startingCells = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (board[y][x] != 0) {
                    String text = Integer.toString(board[y][x]);
                    Rect textBounds = new Rect();
                    textPaint.getTextBounds(text, 0, text.length(), textBounds);
                    float textWidth = textPaint.measureText(text);
                    float textHeight = textBounds.height();
                    valueBoard[y][x] = new Cell(text,textWidth,textHeight,false);
                    startingCells.add(new Pair<>(y, x));
                }
            }
        }
        invalidate();
    }

    private void fillCells(Canvas canvas) {
        //Rest of the cells
        if (selectedRow != -1 && selectedColumn != -1) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    //Cell is the selected Cell
                    if (i == selectedRow && j == selectedColumn) {
                        //System.out.println("SelectedCell");
                        fillCell(canvas, i, j, selectedCellPaint);
                    }
                    //Cell is a related Cell horizontal or vertical
                    else if (i == selectedRow || j == selectedColumn) {
                        //System.out.println("RelatedCell");
                        fillCell(canvas, i, j, relatedCellPaint);
                    }
                    //Cell is a related Cell in the related group
                    else if (i / sqrSize == selectedRow / sqrSize && j / sqrSize == selectedColumn / sqrSize) {
                        //System.out.println("Related Group Cell");
                        fillCell(canvas, i, j, relatedCellPaint);
                    }
                }
            }
        }
        //Starting cells
        for (int i = 0; i < startingCells.size(); i++) {
            fillCell(canvas,startingCells.get(i).first,startingCells.get(i).second,startingCellPaint);
        }
    }

    private void fillCell(Canvas canvas, int i, int j, Paint paint) {
        canvas.drawRect(j*cellSizePixels, i*cellSizePixels, (j+1)*cellSizePixels, (i+1)*cellSizePixels,paint);
    }

    private void drawLines(Canvas canvas) {
        //Border of Sudoku Board
        canvas.drawRect(0F,0F,(float) getWidth(), (float) getHeight(), thickline);

        //Grid of Sudoku Board
        for (int i = 0; i < size; i++) {
            Paint paintToUse;
            if (i % sqrSize == 0) {
                paintToUse = thickline;
            }
            else {
                paintToUse = thinline;
            }
            //HorizontalLines
            canvas.drawLine(i*cellSizePixels,0F,i*cellSizePixels,(float) getHeight(), paintToUse);
            //VerticalLines
            canvas.drawLine(0F, i*cellSizePixels, (float) getWidth(), i*cellSizePixels,paintToUse);
        }
    }

    private void handleTouchEvent(float x, float y) {
        //Check if cords are a starting cell
        int checkRow = (int) (y / cellSizePixels);
        int checkColumn = (int) (x / cellSizePixels);
        for (int i = 0; i < startingCells.size(); i++) {
            if (checkRow == startingCells.get(i).first && checkColumn == startingCells.get(i).second) {
                return;
            }
        }
        selectedRow = checkRow;
        selectedColumn = checkColumn;
        invalidate();
    }

    //Draw text in every cell
    private void drawText(Canvas canvas) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (valueBoard[row][col] != null) {
                    //If Cell does not contain any notes
                    if (valueBoard[row][col].getNotes().isEmpty()) {
                        float x = (((col * cellSizePixels) + cellSizePixels / 2 - valueBoard[row][col].getTextWidth() / 2));
                        float y = (((row * cellSizePixels) + cellSizePixels / 2 - valueBoard[row][col].getHeight() / 2)) + 50;

                        //If text is wrong mark it red otherwise black
                        if (valueBoard[row][col].getIsFalse()) {
                            System.out.println(valueBoard[row][col].getText() + " IS FALSE AT " + row + " " + col);
                            canvas.drawText(valueBoard[row][col].getText(), x, y, falseTextPaint);
                        }
                        else {
                            canvas.drawText(valueBoard[row][col].getText(), x, y, textPaint);
                        }
                    }
                    //If Cell contains notes display all notes
                    else {
                        Iterator<Integer> notesIter = valueBoard[row][col].getNotes().iterator();
                        while (notesIter.hasNext()) {
                            int note = notesIter.next();
                            Rect textBounds = new Rect();
                            String valueString = Integer.toString(note);
                            noteTextPaint.getTextBounds(valueString,0,valueString.length(),textBounds);
                            float textWidth = noteTextPaint.measureText(valueString);
                            float textHeight = textBounds.height();
                            int rowInCell = (note - 1) / sqrSize;
                            int colInCell = (note - 1) % sqrSize;
                            float x = ((col * cellSizePixels) + (colInCell * noteSizePixels) + (noteSizePixels / 2) - (textWidth / 2F));
                            float y = ((row * cellSizePixels) + (rowInCell * noteSizePixels) + (noteSizePixels / 2) - (textHeight / 2F)) + 34F;
                            /*
                            System.out.println("Note "+note);
                            System.out.println("Row in Cell "+rowInCell);
                            System.out.println("Col in Cell "+colInCell);
                            System.out.println("X "+x);
                            System.out.println("Y "+y);
                             */
                            canvas.drawText(Integer.toString(note), x, y, noteTextPaint);
                        }
                    }
                }
            }
        }
    }

}
