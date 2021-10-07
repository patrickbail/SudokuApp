package com.example.SudokuApp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class SudokuActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[] numberButtons = new Button[9];
    private boolean noteClicked = false;
    private SudokuBoardView sudokuBoardView;
    private SudokuGame sudokuGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sudokuBoardView = findViewById(R.id.sudokuBoardView);
        InputStream is = getResources().openRawResource(R.raw.sudoku_boards);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        try {
            sudokuGame = new SudokuGame(sudokuBoardView,reader);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        sudokuBoardView.setValueBoard(sudokuGame.getSolve_board());

        //Numpad
        for (int i = 0; i < 9; i++) {
            String buttonId = "button" + (i + 1);
            int resID = getResources().getIdentifier(buttonId, "id", getPackageName());
            numberButtons[i] = findViewById(resID);
            numberButtons[i].setOnClickListener(this);
        }

        //Menu Button
        ImageButton menu_button = findViewById(R.id.menu_button);
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnMenu();
            }
        });

        //Delete Button
        ImageButton del_button = findViewById(R.id.del_button);
        del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sudokuBoardView.setCell("del");
            }
        });

        //Next Button
        ImageButton next_button = findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sudokuGame.create_solve_board();
                sudokuBoardView.resetSelRowCol();
                sudokuBoardView.setValueBoard(sudokuGame.getSolve_board());
            }
        });

        //Note Button
        final Drawable buttonNotesDrawable = ContextCompat.getDrawable(this,R.drawable.rounded_corner_numbutton_notes);
        final Drawable buttonDrawable = ContextCompat.getDrawable(this,R.drawable.rounded_corner_numbutton);
        ImageButton note_button = findViewById(R.id.note_button);
        note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteClicked = !noteClicked;
                if (noteClicked) {
                    for (Button button: numberButtons) {
                        button.setBackground(buttonNotesDrawable);
                    }
                }
                else {
                    for (Button button: numberButtons) {
                        button.setBackground(buttonDrawable);
                    }
                }
            }
        });

    }

    private void returnMenu() {
        Intent intent = new Intent(this, HomeScreenActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof Button) {
            String textChange = ((Button) v).getText().toString();
            //Fill Cells normal with digits
            if (!noteClicked) {
                if (sudokuGame.checkInput(Integer.parseInt(textChange))) {
                    sudokuBoardView.setIsFalse(true);
                    sudokuBoardView.setCell(textChange);
                } else {
                    sudokuBoardView.setIsFalse(false);
                    sudokuBoardView.setCell(textChange);
                    if (sudokuGame.checkWin()) {
                        Toast.makeText(this, "YOU WON", Toast.LENGTH_SHORT).show();
                        sudokuGame.create_solve_board();
                        sudokuBoardView.resetSelRowCol();
                        sudokuBoardView.setValueBoard(sudokuGame.getSolve_board());
                    }
                }
            }
            //Fill Cells with notes
            else {
                sudokuBoardView.setCellNotes(Integer.parseInt(textChange));
            }
        }
    }
}