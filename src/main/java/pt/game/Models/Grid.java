package pt.game.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Grid {
    private int width; //X, Columns
    private int height; //Y, Rows
    private Square[][] squares; //Valores estão em [Y][X]

    public Grid() {
        this.width = 100;
        this.height = 100;
        this.squares = new Square[this.height][this.width];
        populate();
    }

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.squares = new Square[this.height][this.width];
        populate();
    }

    public void populate(){
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                squares[y][x] = new Square(x,y);
            }
        }
    }

    public void randomPopulate(){
        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                Random random = new Random();
                boolean random20percentage = random.nextInt(5) == 0;
                if(random20percentage) this.squares[y][x].revive();
            }
        }
    }

    public Square getSquare(int x, int y){
        return this.squares[y][x];
    }

    /**
     * Checks if a given square should be revived or killed
     */
    public boolean canRevive(int x, int y, Square[][] squares) {
        int count = 0;
        Square actualPosition = squares[y][x];

        for (int iy = y - 1; iy <= y + 1; iy++) {
            for (int ix = x - 1; ix <= x + 1; ix++) {
                if(!(ix == x && iy == y)) {
                    if (isInsideGrid(ix, iy)) {
                        if(squares[iy][ix].isAlive()){
                            count++;
                        }
                    }
                }
            }
        }

        if (!actualPosition.isAlive()) { //if dead
            if (count == 3) {//revive
                return true;
            }
            return false;//kill
        } else { //if alive
            if (count < 2 || count > 3) {//kill
                return false;
            }
            return true;//revive
        }
    }

    public boolean isInsideGrid(int x, int y){
        return ( 0<= x && 0 <=y && x <= this.width -1 && y <= this.height - 1);
    }

    public Square[][] getGridSquares(){
        return this.squares;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void overrideGrid(Square[][] newGrid){
        this.squares = newGrid;
    }

    public Square[][] getGridCopy(){
        Square[][] copy = new Square[this.height][this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                copy[y][x] = this.squares[y][x];
            }
        }
        return copy;
    }

    public Square[][] getNewEmptyGrid(){
        Square[][] emptyGrid = new Square[this.height][this.width];
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                emptyGrid[y][x] = new Square(x,y);
            }
        }
        return emptyGrid;
    }

}
