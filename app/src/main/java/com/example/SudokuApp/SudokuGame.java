package com.example.SudokuApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class SudokuGame extends AppCompatActivity {
    private int[][] solve_board = new int[9][9];
    private int[][] solved_board = new int[9][9];
    private String[] possible_boards = new String[500001]; //1000001
    private SudokuBoardView sudokuBoardView;

    public SudokuGame(SudokuBoardView sudokuBoardView, BufferedReader reader) throws IOException {
        this.sudokuBoardView = sudokuBoardView;

        //Fill possible boards with all sudoku board combinations from csv file
        String line;
        int i = 0;
        while ((line = reader.readLine()) != null && i < 500001) {
            possible_boards[i] = line;
            i++;
        }

        create_solve_board();
        //testcreate_solve();
    }

    /*
    public void testcreate_solve() {
        /*
        int[][] solve_board =
                        {{5,3,0,0,7,0,0,0,0},
                        {6,0,0,1,9,5,0,0,0},
                        {0,9,8,0,0,0,0,6,0},
                        {8,0,0,0,6,0,0,0,3},
                        {4,0,0,8,0,3,0,0,1},
                        {7,0,0,0,2,0,0,0,6},
                        {0,6,0,0,0,0,2,8,0},
                        {0,0,0,4,1,9,0,0,5},
                        {0,0,0,0,8,0,0,7,9}};

         int[][] solve_board =
                        {{6,2,9,4,7,3,5,1,8},
                        {7,8,5,1,9,2,6,3,4},
                        {3,4,1,5,6,8,9,2,7},
                        {9,3,8,6,2,5,4,7,1},
                        {1,7,6,8,4,9,3,5,2},
                        {2,5,4,3,1,7,8,9,6},
                        {5,9,2,7,8,4,1,6,3},
                        {4,1,3,2,5,6,7,8,9},
                        {8,6,7,9,3,1,2,4,0}};


        solve_board = new int[][]
                        {{6,2,9,4,7,3,5,1,8},
                        {7,8,5,1,9,2,6,3,4},
                        {3,4,1,5,6,8,9,2,7},
                        {9,3,8,6,2,5,4,7,1},
                        {1,7,6,8,4,9,3,5,2},
                        {2,5,4,3,1,7,8,9,6},
                        {5,9,2,7,8,4,1,6,3},
                        {4,1,3,2,5,6,7,8,9},
                        {8,6,7,9,3,1,2,4,0}};
        solve();
    }
     */

    public int[][] getSolve_board() {
        return solve_board;
    }

    public void create_solve_board() {
        //Select a random sudoku board from csv file
        Random random = new Random();
        int random_number = random.nextInt(500001) + 1; //1000001
        System.out.println("Random Board Number "+random_number);
        String random_board = possible_boards[random_number];

        //Fill solve board with numbers
        for (int i = 0; i < 81; i++) {
            String cell = String.valueOf(random_board.charAt(i));
            solve_board[i/9][i%9] = Integer.parseInt(cell);
        }
        solve();
    }

    public boolean checkWin() {
        Cell [][] current_board = sudokuBoardView.getValueBoard();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (current_board[i][j] == null) {
                    return false;
                }
                else if (Integer.parseInt(current_board[i][j].getText()) != solved_board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkInput(int number) {
        if (sudokuBoardView.getSelectedRow() != -1 || sudokuBoardView.getSelectedColumn() != -1) {
            return !(solved_board[sudokuBoardView.getSelectedRow()][sudokuBoardView.getSelectedColumn()] == number);
        }
        return false;
    }


    private boolean possible(int x, int y, int number) {
        //Horizontal and vertical check
        for (int i = 0; i < 9; i++) {
            if (solve_board[i][x] == number || solve_board[y][i] == number) {
                return false;
            }
        }
        //Group check
        int xSquare = (x/3)*3;
        int ySquare = (y/3)*3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (solve_board[ySquare+i][xSquare+j] == number) {
                    return false;
                }
            }
        }
        return true;
    }

    private void solve() {
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (solve_board[y][x] == 0) {
                    for (int number = 1; number < 10; number++) {
                        if (possible(x,y,number)) {
                            solve_board[y][x] = number;
                            solve();
                            solve_board[y][x] = 0;
                        }
                    }
                    return;
                }
            }
        }
        for (int y = 0; y < 9; y++) {
            System.arraycopy(solve_board[y], 0, solved_board[y], 0, 9);
        }
        printBoard();
    }

    private void printBoard() {
        System.out.println("-----------------");
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (x == 8) {
                    System.out.print(solved_board[y][x]);
                }
                else {
                    System.out.print(solved_board[y][x] + "|");
                }
            }
            System.out.println(" ");
        }
        System.out.println("-----------------");
    }

}
