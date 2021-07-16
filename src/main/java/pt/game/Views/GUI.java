package pt.game.Views;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pt.game.Controllers.Conway;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GUI extends Application {

    private ExecutorService threadPool;
    private int x;
    private int y;
    private Rectangle[][] rectanglesArray;
    VBox vBox = new VBox();
    private boolean runningSimulation;
    private Conway game;
    private Button pauseButton = null;
    private Button startButton = null;
    private Button randomButton = null;
    private Button stepButton = null;
    private Button resetButton = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Game Of Life by github.com/jpfn2407");
        primaryStage.setResizable(false);
        Image image = new Image("/images/icon.png");
        primaryStage.getIcons().add(image);

        GridPane grid = new GridPane();

        final Label runningLabel = new Label("Simulation is running");
        runningLabel.setVisible(false);
        runningLabel.setId("runningLabel");
        runningLabel.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC, 20));
        grid.add(runningLabel, 5, 0);

        this.startButton = new Button("Start Simulation");
        startButton.setId("StartButton");
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(!runningSimulation) {
                    runningLabel.setVisible(true);
                    pauseButton.setText("Pause Simulation");
                    startSimulation();
                    startButton.setVisible(false);
                    stepButton.setVisible(true);
                    pauseButton.setVisible(true);
                    randomButton.setVisible(false);
                }
            }
        });
        grid.add(startButton, 0,0);
        startButton.setVisible(true);

        this.pauseButton = new Button("Pause Simulation");
        pauseButton.setId("PauseButton");
        pauseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(runningSimulation){
                    pauseSimulation();
                    if(pauseButton.getText().equals("Pause Simulation")) pauseButton.setText("Resume Simulation");
                } else {
                    pauseSimulation();
                    pauseButton.setText("Pause Simulation");
                }

            }
        });
        grid.add(pauseButton, 2,0);
        pauseButton.setVisible(false);

        this.randomButton = new Button("Randomly Populate");
        randomButton.setId("randomButton");
        randomButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if(!runningSimulation) {
                    randomPopulate();
                }
            }
        });
        grid.add(randomButton, 1,0);
        randomButton.setVisible(true);

        this.stepButton = new Button("Step Frame");
        stepButton.setId("stepFrame");
        stepButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                stepIteration();
            }
        });
        grid.add(stepButton, 3,0);
        stepButton.setVisible(false);

        this.resetButton = new Button("Clear & Reset");
        resetButton.setId("resetButton");
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                runningLabel.setVisible(false);
                pauseButton.setText("Pause Simulation");
                resetSimulation();
                startButton.setVisible(true);
                stepButton.setVisible(false);
                pauseButton.setVisible(false);
                randomButton.setVisible(true);
            }
        });
        grid.add(resetButton, 4,0);
        resetButton.setVisible(true);

        GridPane squaresGrid = new GridPane();
        if(!runningSimulation) {
            squaresGrid.setOnDragDetected(
                    event -> {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            event.consume();
                            squaresGrid.startFullDrag();
                        }
                    });
        }
        this.x = 100;
        this.y = 100;
        this.rectanglesArray = new Rectangle[this.y][this.x];
        for (int row = 0; row < y; row++) {
            for (int col = 0; col < x; col++) {
                Rectangle rec = new Rectangle();
                rectanglesArray[row][col] = rec;
                rec.setWidth(8);
                rec.setHeight(8);
                rec.setStyle("-fx-fill: white; -fx-stroke: grey; -fx-stroke-width: 0.2;");

                if(!runningSimulation) {
                    rec.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            if (rec.getFill() == Color.WHITE) {
                                rec.setFill(Color.BLACK);
                            } else {
                                rec.setFill(Color.WHITE);
                            }
                        }
                    });
                    rec.setOnMouseDragEntered(
                            event -> {
                                event.consume();
                                if (rec.getFill() == Color.WHITE) {
                                    rec.setFill(Color.BLACK);
                                } else {
                                    rec.setFill(Color.WHITE);
                                }
                            });
                }

                GridPane.setRowIndex(rec, row);
                GridPane.setColumnIndex(rec, col+1);
                squaresGrid.getChildren().addAll(rec);
            }
        }
        vBox.getChildren().addAll(grid, squaresGrid);
        Scene scene = new Scene(vBox, 900, 930);
        primaryStage.setScene(scene);
        primaryStage.show();

        this.threadPool = Executors.newCachedThreadPool();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                runningSimulation = false;
                try {
                    if (threadPool != null){
                        threadPool.awaitTermination(1,TimeUnit.SECONDS);
                    }
                } catch (InterruptedException interruptedException) {
                    //interruptedException.printStackTrace();
                }
                if (threadPool != null){
                    threadPool.shutdownNow();
                }
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void randomPopulate(){
        for (int ix = 0; ix < this.x; ix++) {
            for (int iy = 0; iy < this.y; iy++) {
                Random random = new Random();
                boolean random20percentage = random.nextInt(5) == 0;
                if(random20percentage) this.rectanglesArray[iy][ix].setFill(Color.BLACK);
            }
        }
    }

    public void startSimulation(){
        this.runningSimulation = true;
        threadPool.submit(()->{
            this.game = new Conway(this.x,this.y);
            populateGridWithSelectedSquares();
            while (true){
                renderIterationFrame();
                try {
                    //60 FPS
                    TimeUnit.MILLISECONDS.sleep(166);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(!this.runningSimulation) TimeUnit.MILLISECONDS.sleep(1);
            }
        });
    }

    public void stepIteration(){
        renderIterationFrame();
    }

    public void renderIterationFrame(){
        //Limpar tudo (deixar a branco)
        for(int iy = 0; iy < y; iy++) {
            for (int ix = 0; ix < x; ix++) {
                rectanglesArray[iy][ix].setFill(Color.WHITE);
            }
        }
        //Dar render
        for (int ix = 0; ix < game.getGrid().getWidth(); ix++) {
            for (int iy = 0; iy < game.getGrid().getHeight(); iy++) {
                if(game.getGrid().getSquare(ix, iy).isAlive()){
                    rectanglesArray[iy][ix].setFill(Color.BLACK);
                }
            }
        }
        //Pegar a proxima frame
        game.iterate();
    }

    public void pauseSimulation(){
        this.runningSimulation = !this.runningSimulation;
    }

    public void clearGrid(){
        for(int iy = 0; iy < y; iy++) {
            for (int ix = 0; ix < x; ix++) {
                rectanglesArray[iy][ix].setFill(Color.WHITE);
            }
        }
    }

    public void resetSimulation(){
        this.runningSimulation = false;
        try {
            if (threadPool != null){
                threadPool.awaitTermination(500,TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException interruptedException) {
            //interruptedException.printStackTrace();
        }
        if (threadPool != null){
            threadPool.shutdownNow();
        }
        clearGrid();
        threadPool = Executors.newCachedThreadPool();
    }

    public void populateGridWithSelectedSquares(){
        for (int iy = 0; iy < this.y; iy++) {
            for (int ix = 0; ix < this.x; ix++) {
                if(this.rectanglesArray[iy][ix].getFill() == Color.BLACK){
                    this.game.getGrid().getSquare(ix,iy).revive();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
