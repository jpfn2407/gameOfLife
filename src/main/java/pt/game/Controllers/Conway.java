package pt.game.Controllers;
import pt.game.Models.Grid;
import pt.game.Models.Square;

public class Conway {
    private final Grid grid;

    public Conway(int width, int height) {
        this.grid = new Grid(width, height);
    }

    public void iterate() {
        Square[][] prev = this.grid.getGridCopy();
        Square[][] next = this.grid.getNewEmptyGrid();
        for (int y = 0; y < grid.getHeight(); y++) {
            for (int x = 0; x < grid.getWidth(); x++) {
                boolean canRevive = grid.canRevive(x, y, prev);
                if (canRevive) {
                    next[y][x].revive();
                } else {
                    next[y][x].kill();
                }
            }
        }
        grid.overrideGrid(next);
    }

    public Square[][] getGridSquares() {
        return this.grid.getGridSquares();
    }

    public Grid getGrid(){
        return this.grid;
    }

}