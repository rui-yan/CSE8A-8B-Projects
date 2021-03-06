/* File header comment
 * Name: Rui Yan
 * Login: cs8bwaka 
 * Date: March 2, 2016
 * file: Gui2048.java 
 * Sources of Help: None  
 * 
 * This program will implement the GUI and the event handling code. Gui will 
 * show the score, the name of the game (2048), the tiles with values (and 
 * colors which change with values). Also, it will display "Game Over" when 
 * the game ends; Eventhandler will either move (if one of the arrow keys is 
 * pressed) or save the board if the "s" key is pressed based on the key 
 * pressed. 
 *
 */

import javafx.application.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;
import java.util.*;
import java.io.*;

/* Class header comment
 * Name: Gui2048
 * Purpose: This class will implement the GUI and the event handling code.
 */
public class Gui2048 extends Application {

    private String outputBoard; 
    // The filename for where to save the Board
    private Board board; 
    // The 2048 Game Board

    private static final int TILE_WIDTH = 106;

    private static final int TEXT_SIZE_LOW = 55; 
    // Low value tiles (2,4,8,etc)
    private static final int TEXT_SIZE_MID = 45;
    // Mid value tiles (128, 256, 512)
    private static final int TEXT_SIZE_HIGH = 35; 
    // High value tiles (1024, 2048, Higher)

    // Fill colors for each of the Tile values
    private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
    private static final Color COLOR_2 = Color.rgb(238, 228, 218);
    private static final Color COLOR_4 = Color.rgb(237, 224, 200);
    private static final Color COLOR_8 = Color.rgb(242, 177, 121);
    private static final Color COLOR_16 = Color.rgb(245, 149, 99);
    private static final Color COLOR_32 = Color.rgb(246, 124, 95);
    private static final Color COLOR_64 = Color.rgb(246, 94, 59);
    private static final Color COLOR_128 = Color.rgb(237, 207, 114);
    private static final Color COLOR_256 = Color.rgb(237, 204, 97);
    private static final Color COLOR_512 = Color.rgb(237, 200, 80);
    private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
    private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
    private static final Color COLOR_OTHER = Color.BLACK;
    private static final Color COLOR_GAME_OVER=Color.rgb(238, 228, 218, 0.73);

    private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242); 
    // For tiles >= 8

    private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101); 
    // For tiles < 8

    private GridPane pane;

    /** Add your own Instance Variables here */
    // Create a StackPane to add the gameover layer, 
    // Text gameover and GridPane on it
    private StackPane stack;
    private int gridSize;
    // Create three Text object
    private Text gameName;
    private Text score;
    private Text gameOver;
    // Create a 2D array of Rectangle objects
    private Rectangle[][] tiles;
    // Create a 2D arrray of Text objects
    private Text[][] num;
    // Create a 2D array of int to get the grid in Board.java 
    private int[][] grid;
    // Create a boolean to determine whether I should print out "Game Over!"
    // If I have printed out once, it should be false 
    boolean printOver = true;

    /* Method header comment
     * Name: start
     * Purpose: This method will show the score, the name of the game 
     *          (2048), the tiles with values (and colors which change 
     *          with values). Also, it will display "Game Over" when the 
     *          game ends.
     * Parameter: Stage primaryStage
     * Return: void
     */
    @Override
    public void start(Stage primaryStage) {

	// Process Arguments and Initialize the Game Board
	processArgs(getParameters().getRaw().toArray(new String[0]));

	// Create the pane that will hold all of the visual objects
	stack = new StackPane();
	pane = new GridPane();
	pane.setAlignment(Pos.CENTER);
	pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
	pane.setStyle("-fx-background-color: MEDIUMPURPLE");
	// Set the spacing between the Tiles
	pane.setHgap(10); 
	pane.setVgap(10);
	stack.getChildren().add(pane);

	/** Add your Code for the GUI Here */
	// Initialize the gridSize according to GRID_SIZE in grid
	gridSize = this.board.getGrid()[0].length;

	// Initialize the instance variable board, the passed in size = 4
	Random rm = new Random();
	this.board = new Board(gridSize, rm);

	// Initialize the grid and get the grid in Board
	grid = this.board.getGrid(); 

	//Add constructed pane to a scene and scene to the stage
	//so that the pane can be displayed
	Scene scene = new Scene(stack, 400, 400);
	primaryStage.setTitle("Gui2048");

	// Initialize the instance variables
	gameName = new Text("2048");
	score = new Text("Score: 0");
	gameOver = new Text("Game Over!");

	// Set the font and color of text gameName and score
	gameName.setFont(Font.font("Times New Roman", FontWeight.BOLD, 50));
	gameName.setFill(Color.BLACK);
	score.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
	score.setFill(Color.BLACK);

	// Add these two texts at the center of (0,0) and (2,0) 
	pane.add(gameName, 0, 0, 2, 1);
	GridPane.setHalignment(gameName, HPos.CENTER);
	pane.add(score, 2, 0, 2, 1);

	// Initialize the 2D Rectangle array tiles as required
	// Rectangle is to represent the tile
	tiles = new Rectangle[gridSize][];
	for(int i = 0; i < gridSize; i++) {
	    tiles[i] = new Rectangle[gridSize];
	}
	for(int i = 0; i < gridSize; i++) {
	    for(int j = 0; j < gridSize; j++) {
		tiles[i][j] = new Rectangle();
		//Set the width and height of tiles
		tiles[i][j].heightProperty().bind
		    (stack.heightProperty().divide(gridSize+2));
		tiles[i][j].widthProperty().bind
		    (stack.widthProperty().divide(gridSize+2));
		//Set the color of tiles
		tiles[i][j].setFill(COLOR_EMPTY);
		//Add rectangle tiles to pane
		pane.add(tiles[i][j], j, i+1, 1, 1);
	    }
	}

	// Initialize the 2D Text array num and add it to pane
	// Text is to show the value
	num = new Text[gridSize][];
	for(int i = 0; i < gridSize; i++) {
	    num[i] = new Text[gridSize];
	}

	for(int i = 0; i < gridSize; i++) {
	    for(int j = 0; j < gridSize; j++) {
		//Determine the first two random tiles
		//Change the color of the tiles if they are 2 or 4
		if(grid[i][j] == 2 || grid[i][j]==4) {
		    if(grid[i][j] == 2)
			tiles[i][j].setFill(COLOR_2);
		    else if(grid[i][j] == 4)
			tiles[i][j].setFill(COLOR_4);

		    num[i][j] = new Text(String.valueOf(grid[i][j]));
		    //Set the size of the font of the Text
		    num[i][j].setFont(new Font(TEXT_SIZE_LOW));
		    //Set the color of the Text
		    num[i][j].setFill(COLOR_VALUE_DARK);
		    //Add text to the pane
		    pane.add(num[i][j], j, i+1);
		    //Set the text to be centered in the middle of 
		    //the rectangle
		    GridPane.setHalignment(num[i][j], HPos.CENTER);
		}

		else if(grid[i][j] == 0) {
		    num[i][j] = new Text("");
		    //Add text to the pane
		    pane.add(num[i][j], j, i+1);
		    //Set the text to be centered in the middle of
		    //the rectangle
		    GridPane.setHalignment(num[i][j], HPos.CENTER);
		}
	    }
	}

	//Add an instantiation of key handler to the scene 
	//in order to handle keypresses
	scene.setOnKeyPressed(new myKeyHandler());
	primaryStage.setScene(scene);
	primaryStage.show();
    }


    /** Add your own Instance Methods Here*/
    /* innerclass header comment
     * Name: myKeyHandler
     * Purpose: This class will implement the interface EventHandler
     *          to handle the key event. Eventhandler will either move 
     *          (if one of the arrow keys is pressed) or save the board 
     *          if the "s" key is pressed, or rotate the board if "r" key 
     *          is pressed. 
     */
    private class myKeyHandler implements EventHandler<KeyEvent> 
    {

	/* Method header comment
	 * Name: updateBoard
	 * Purpose: This method will update the rectangle tiles and Text num 
	 *          according to the updated grid in the Board.java  
	 * Parameter: None
	 * Return: void
	 */
	private void updateBoard() 
	{
	    //    if(board.isGameOver() == false)
	    //{
	    // Remove all old Rectangle tiles and Text num
	    // in the old grid
	    for(int i = 0; i < gridSize; i++) {
		for(int j = 0; j < gridSize; j++) {  
		    pane.getChildren().remove(tiles[i][j]);
		    pane.getChildren().remove(num[i][j]);
		}
	    }

	    // Get the updated grid
	    grid = board.getGrid();

	    //Add Rectangle tiles and Text num based on the updated grid
	    for(int i = 0; i < gridSize; i++) {
		for(int j = 0; j < gridSize; j++) {  
		    // If the value of tiles is 0
		    // Add rectangle with width TILE_WIDTH and
		    // color COLOR_EMPTY
		    // Add text with nothing in it
		    if(grid[i][j]==0) {
			tiles[i][j] = new Rectangle();
			tiles[i][j].heightProperty().bind
			    (stack.heightProperty().divide(gridSize + 2));
			tiles[i][j].widthProperty().bind
			    (stack.widthProperty().divide(gridSize + 2));
			tiles[i][j].setFill(COLOR_EMPTY);
			num[i][j] = new Text("");
			pane.add(tiles[i][j], j, i+1, 1, 1);
			pane.add(num[i][j], j, i+1, 1, 1);
		    }

		    // If the value of tiles do not equal to 0
		    // Add tiles with a certain color and text with 
		    // a certain string based on the updated grid
		    else if(grid[i][j]!=0) {
			tiles[i][j] = new Rectangle();
			tiles[i][j].heightProperty().bind
			    (stack.heightProperty().divide(gridSize+2));
			tiles[i][j].widthProperty().bind
			    (stack.widthProperty().divide(gridSize+2));
			num[i][j] = new Text(String.valueOf(grid[i][j]));

			// Set the color of Rectangle tiles based on the value
			// of grid[i][j]
			if(grid[i][j] == 2)
			    tiles[i][j].setFill(COLOR_2);
			else if(grid[i][j] == 4)
			    tiles[i][j].setFill(COLOR_4);
			else if(grid[i][j] == 8)
			    tiles[i][j].setFill(COLOR_8);
			else if(grid[i][j] == 16)
			    tiles[i][j].setFill(COLOR_16);
			else if(grid[i][j] == 32)
			    tiles[i][j].setFill(COLOR_32);
			else if(grid[i][j] == 64)
			    tiles[i][j].setFill(COLOR_64);
			else if(grid[i][j] == 128)
			    tiles[i][j].setFill(COLOR_128);
			else if(grid[i][j] == 256)
			    tiles[i][j].setFill(COLOR_256);
			else if(grid[i][j] == 512)
			    tiles[i][j].setFill(COLOR_512);
			else if(grid[i][j] == 1024)
			    tiles[i][j].setFill(COLOR_1024);
			else if(grid[i][j] == 2048)
			    tiles[i][j].setFill(COLOR_2048);
			else
			    tiles[i][j].setFill(COLOR_OTHER);

			// Add the Rectangle tiles to the pane
			pane.add(tiles[i][j], j, i+1, 1, 1);

			//Set the font of the Text based on the value of tiles
			if(grid[i][j] < 100){
			    num[i][j].setFont(new Font(TEXT_SIZE_LOW));
			}
			else if(grid[i][j] < 1000){
			    num[i][j].setFont(new Font(TEXT_SIZE_MID));
			}
			else{
			    num[i][j].setFont(new Font(TEXT_SIZE_HIGH));
			}

			//Set the color of the Text based on 
			//the value of tiles
			if(grid[i][j] < 8)
			    num[i][j].setFill(COLOR_VALUE_DARK);
			else 
			    num[i][j].setFill(COLOR_VALUE_LIGHT);

			// Add the Text num to the plane
			pane.add(num[i][j], j, i+1, 1, 1);
			GridPane.setHalignment(num[i][j], HPos.CENTER);
		    }
		}
	    }
	    }

	/* Method header comment
	 * Name: printOutOver
	 * Purpose: This method will create a new rectangle 
	 *          and add it to StackPane when the game
	 *          is over.
	 * Parameter: None
	 * Return: void
	 */
	private void printOutOver() {
	    Rectangle cover = new Rectangle();
	    // Set the font and color of the Text gameOver
	    gameOver.setFont(Font.font
		    ("Times New Roman", FontWeight.BOLD, 50));
	    gameOver.setFill(Color.BLACK);
	    // Set the color, width and size of the rectangle cover
	    cover.setFill(COLOR_GAME_OVER);
	    cover.heightProperty().bind(stack.heightProperty());
	    cover.widthProperty().bind(stack.widthProperty());
	    StackPane.setAlignment(cover, Pos.CENTER);
	    stack.getChildren().add(cover);
	    stack.getChildren().add(gameOver);
	}

	/* Method header comment
	 * Name: handle
	 * Purpose: This method will handle keyboard events. Based on the key 
	 *          pressed it will either move (if one of the arrow keys is 
	 *          pressed) or save the board if the "s" key is pressed. 
	 * Parameter: KeyEvent e
	 * Return: void
	 */
	@Override
	public void handle(KeyEvent e)
	{
	    switch(e.getCode()) 
	    {
		// click up arrow
		case UP: 
		    if(board.move(Direction.UP) == true) {
			board.move(Direction.UP);
			board.addRandomTile(); 
			// Print out updated score on pane
			score.setText("Score:" + board.getScore());
			// Print out required string 
			System.out.println("Moving Up");
			// Call the updateBoard method to print out 
			// the updated board on pane 
			this.updateBoard();
		    }
		    else if(board.isGameOver()==true && printOver == true) {
			this.updateBoard();
			this.printOutOver();
			printOver = false;
		    }
		    break;

		    // click down arrow
		case DOWN: 
		    if(board.move(Direction.DOWN) == true) {
			board.move(Direction.DOWN);	
			board.addRandomTile();
			score.setText("Score:" + board.getScore());
			System.out.println("Moving Down");
			this.updateBoard();
		    }
		    else if(board.isGameOver()==true && printOver == true) {
			this.updateBoard();
			this.printOutOver();
			printOver = false;
		    }
		    break;

		    // click left arrow
		case LEFT: 
		    if(board.move(Direction.LEFT) == true) {
			board.move(Direction.LEFT);
			board.addRandomTile(); 
			score.setText("Score:" + board.getScore());
			System.out.println("Moving Left");
			this.updateBoard();
		    }
		    else if(board.isGameOver() == true  && printOver == true){
			this.updateBoard();
			this.printOutOver();
			printOver = false;
		    }
		    break;

		    // click right arrow
		case RIGHT: 
		    if(board.move(Direction.RIGHT) == true) {
			board.addRandomTile(); 
			score.setText("Score:" + board.getScore());
			System.out.println("Moving Right");
			this.updateBoard();
		    }
		    else if(board.isGameOver() == true  && printOver == true){
			this.updateBoard();
			this.printOutOver();
			printOver = false;
		    }
		    break;

		    // click s
		case S: 
		    try {
			// save Board to a file called outputBoard
			board.saveBoard(outputBoard);
		    } catch (IOException a) { 
			System.out.println("saveBoard threw an Exception");
		    }
		    System.out.println("Saving Board to " + outputBoard);
		    break;

		    // click r
		case R: 
		    // rotate the board clockwise
		    if(board.isGameOver() == false) {
			board.rotate(true);
			this.updateBoard();
		    }
		    else if(board.isGameOver() == true  && printOver == true) {
			this.updateBoard();
			this.printOutOver();
			printOver = false;
		    }
		    break;

		default: break;
	    }
	}
	}

	/** DO NOT EDIT BELOW */
	// The method used to process the command line arguments
	private void processArgs(String[] args) {

	    String inputBoard = null;   // The filename for where to load the Board
	    int boardSize = 0;          // The Size of the Board

	    // Arguments must come in pairs
	    if((args.length % 2) != 0) {
		printUsage();
		System.exit(-1);
	    }

	    // Process all the arguments 
	    for(int i = 0; i < args.length; i += 2) {
		if(args[i].equals("-i")){   
		    // We are processing the argument that specifies
		    // the input file to be used to set the board
		    inputBoard = args[i + 1];
		}
		else if(args[i].equals("-o"))
		{   // We are processing the argument that specifies
		    // the output file to be used to save the board
		    outputBoard = args[i + 1];
		}
		else if(args[i].equals("-s"))
		{   // We are processing the argument that specifies
		    // the size of the Board
		    boardSize = Integer.parseInt(args[i + 1]);
		}
		else
		{   // Incorrect Argument 
		    printUsage();
		    System.exit(-1);
		}
	    }

	    // Set the default output file if none specified
	    if(outputBoard == null)
		outputBoard = "2048.board";
	    // Set the default Board size if none specified or less than 2
	    if(boardSize < 2)
		boardSize = 4;

	    // Initialize the Game Board
	    try{
		if(inputBoard != null)
		    board = new Board(inputBoard, new Random());
		else
		    board = new Board(boardSize, new Random());
	    }
	    catch (Exception e)
	    {
		System.out.println(e.getClass().getName() + 
			" was thrown while creating a " +
			"Board from file " + inputBoard);
		System.out.println("Either your Board(String, Random) " +
			"Constructor is broken or the file isn't " +
			"formated correctly");
		System.exit(-1);
	    }
	}

	// Print the Usage Message 
	private static void printUsage()
	{
	    System.out.println("Gui2048");
	    System.out.println("Usage:  Gui2048 [-i|o file ...]");
	    System.out.println();
	    System.out.println("  Command line arguments come in pairs of the "+ 
		    "form: <command> <argument>");
	    System.out.println();
	    System.out.println("  -i [file]  -> Specifies a 2048 board that " + 
		    "should be loaded");
	    System.out.println();
	    System.out.println("  -o [file]  -> Specifies a file that should be " + 
		    "used to save the 2048 board");
	    System.out.println("                If none specified then the " + 
		    "default \"2048.board\" file will be used");  
	    System.out.println("  -s [size]  -> Specifies the size of the 2048" + 
		    "board if an input file hasn't been"); 
	    System.out.println("                specified.  If both -s and -i" + 
		    "are used, then the size of the board"); 
	    System.out.println("                will be determined by the input" +
		    " file. The default size is 4.");
	}
    }
