package pt.game.Models;

public class Square {
    private final int posX;
    private final int posY;
    private boolean isAlive;

    public Square(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.isAlive = false;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void kill(){
        this.isAlive = false;
    }

    public void revive(){
        this.isAlive = true;
    }
}
