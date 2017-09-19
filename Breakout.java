/*
 * File: Breakout.java
 * -------------------
 * Name: Derek Blankenship
 * 
 * This file implements a version of the game Breakout by Steve Wozniak.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

//import com.sun.prism.paint.Color;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 600;
	public static final int APPLICATION_HEIGHT = 800;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	//private static final int PADDLE_WIDTH = WIDTH; //for testing
	private static final int PADDLE_WIDTH = 70;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 12;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;
	
/** x position used for drawing a row such that the row will be 
 * centered in the window */
	private static final int CENTERED_ROW_X = 
			(WIDTH/2) - ((NBRICKS_PER_ROW * BRICK_WIDTH)/2) - ((BRICK_SEP * NBRICKS_PER_ROW)/2) + BRICK_SEP/2;
	
/** paddle speed when using directional arrows to move */
	private static final double PADDLE_SPEED = 5.0;

/** Number of turns */
	private static final int NTURNS = 3;

/** game speed determines difficulty */
	private static final double GAME_SPEED_4 = 3.7;
	private static final double GAME_SPEED_3= 5.7;
	private static final double GAME_SPEED_2 = 7.7;
	private static final double GAME_SPEED_1 = 9.7;
	private static final double GAME_SPEED_0 = 11.7;
	//private static final double GAME_SPEED = 0.5; // for testing
	
/** box width in pixels, for the win/lose message box */
	private static final int GAME_OVER_BOX_WIDTH = 200;
	
/** box height in pixels, for the win/lose message box */
	private static final int GAME_OVER_BOX_HEIGHT = 75;
	
/** offset of the score text from the top and left side*/
	private static final int SCORE_X_OFFSET = 10; 
	private static final int SCORE_Y_OFFSET = 50;

/** font for the score */
	private static final String SCORE_FONT = "SansSerif-18";
	
/** offset of the score text from the top and left side*/
	private static final int COMBO_X_OFFSET = WIDTH/2; 
	private static final int COMBO_Y_OFFSET = SCORE_Y_OFFSET;
	
/** font for the combo text*/
	private static final String COMBO_FONT = SCORE_FONT;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setupBreakout();
		playBreakout();
	}

/** initializes all elements of Breakout game */
	private void setupBreakout() {
		initRandom();
		drawBricks();
		initPaddle();
		drawHoldMessage();
		initBall();
		initLabels();
		addKeyListeners();
		addMouseListeners();
		this.bounceClip = MediaTools.loadAudioClip("bounce.au"); 
	}
	
/** runs game loop for breakout, ending when player either destroys all 
 * bricks or loses three turns (by allowing the ball to hit the bottom bound 
 * of the screen) */
	private void playBreakout() {
		while (!this.gameOver) {
			updatePaddle();
			checkForCollision();
			updateGameSpeed();
			updateLabels();
			updateBall();
			pause(this.gameSpeed);
		}
		gameOver();
	}

/** initializes a random number generator */
	private void initRandom() {
		this.rgen = RandomGenerator.getInstance();
	}
	
/** moves the paddle according to the mouse position when mouse is on screen */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() < WIDTH) {
			if (e.getX() > WIDTH - PADDLE_WIDTH) {
				this.paddle.setLocation(WIDTH - PADDLE_WIDTH, this.paddle.getY());
			}
			else if (e.getX() <= 10) {
				this.paddle.setLocation(0, this.paddle.getY());
			}
			else {
				this.paddle.setLocation(e.getX(), this.paddle.getY());
			}
			add(this.paddle);
		}
	}

/** allows the player to release the ball, starting the turn */
	public void mouseClicked(MouseEvent e) {
		this.ballHeld = false;
		remove(this.ballHeldMessage);
	}
	
/** alternative movement method, using the left and right directional arrows*/
	public void keyPressed(KeyEvent e) {
		if (this.paddle != null) {
			switch (e.getKeyCode()) {
		        case KeyEvent.VK_LEFT:  
		        	this.paddleLeft = true;
		        	break;
		        case KeyEvent.VK_RIGHT: 
		        	this.paddleRight = true;
		        	break;
			}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (this.paddle != null) {
			switch (e.getKeyCode()) {
	        case KeyEvent.VK_LEFT:  
	        	this.paddleLeft = false;
	        	break;
	        case KeyEvent.VK_RIGHT: 
	        	this.paddleRight = false;
	        	break;
			}
		}
	}
	
	
/** draws 10 rows of multicolored bricks */
	private void drawBricks() {
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET, Color.RED);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 2, Color.ORANGE);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 4, Color.YELLOW);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 6, Color.GREEN);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 8, Color.CYAN);
	}
	
/** draws a pair of brick rows of the same color, with 
 * the top row starting at (x,y) */
	private void drawPairBrickRows(int x, int y, Color color)
	{
		for (int i = 0; i < 2; i++)
		{
			drawBrickRow(x, y, color);
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
/** draws a single row of bricks of Color color, starting at (x,y) */
	private void drawBrickRow(int x, int y, Color color) {
		for (int i = NBRICKS_PER_ROW; i > 0; i--) {
			drawBrick(x, y, color);
			x += BRICK_WIDTH + BRICK_SEP;
		}
	}
	
/** draws a single brick or Color color at (x,y) */
	private void drawBrick(int x, int y, Color color) {
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(Color.BLACK);
		brick.setFillColor(color);
		add(brick);
	}

/** initalizes the paddle as a black, filled rectangle at the bottom
 * of the screen */
	private void initPaddle() {
		this.paddle = new GRect((WIDTH/2) - (PADDLE_WIDTH/2), 
				HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		this.paddle.setFilled(true);
		this.paddle.setColor(Color.BLACK);
		add(paddle);
	}

/** initializes the score and combo labels at the top of the screen */
	private void initLabels() {
		this.scoreLabel = new GLabel("Score: " + this.score, SCORE_X_OFFSET, SCORE_Y_OFFSET);
		this.scoreLabel.setFont(SCORE_FONT);
		this.comboLabel = new GLabel("Combo: " + this.paddleHits, COMBO_X_OFFSET, COMBO_Y_OFFSET);
		this.comboLabel.setFont(COMBO_FONT);
		add(this.scoreLabel);
		add(this.comboLabel);
	}
	
/** initializes the ball as a black circle, and sets its initial velocity;
 * the velocity in the x direction is randomized between 1.0 - 3.0 px/cycle;
 * ball remains stationary until player clicks to release it */
	private void initBall() {
		this.ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		this.ball.setFilled(true);
		add(this.ball, WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
		this.ballVX = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			this.ballVX *= -1;
		}	
		this.ballVY = 3.0;
		this.ballHeld = true;
		drawHoldMessage();
	}

/** resets the ball, sending it back to the middle of the screen;
 * ball will remain stationary until player clicks to release it */
	private void resetBall() {
		this.ball.setLocation(WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
		this.ballVX = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			this.ballVX *= -1;
		}	
		this.ballVY = 3.0;
		this.ballHeld = true;
		drawHoldMessage();
	}
	
/** updates the ball according to it's velocity, and checks
 * boundaries, reseting if it passes the bottom bound and 
 * bouncing if it passes the side or top bounds */	
	private void updateBall() {
		if (xBoundCrossed()) {
			this.bounceClip.play();
			ballVX *= -1;
		}
		if (topBoundCrossed()) {
			this.bounceClip.play();
			ballVY *= -1;
		}
		if (bottomBoundCrossed()) {
			this.turnsLost++;
			if (turnsLost >= NTURNS)
			{
				this.gameOver = true;
			}
			else {
				resetBall();
				this.paddleHits = 0;
			}
		}
		if (!ballHeld) {
			this.ball.move(ballVX, ballVY);
		}
	}

/** updates the paddle according to player input */	
	private void updatePaddle() {
		if (this.paddle.getLocation().getX() > (WIDTH - PADDLE_WIDTH)){
			this.paddle.setLocation(WIDTH - PADDLE_WIDTH, HEIGHT - PADDLE_Y_OFFSET);
		}
		else if (this.paddle.getLocation().getX() < 0) {
			this.paddle.setLocation(0, HEIGHT - PADDLE_Y_OFFSET);
		}
		if (this.paddleLeft)
		{
			this.paddle.move(-PADDLE_SPEED, 0);
		}
		if (this.paddleRight)
		{
			this.paddle.move(PADDLE_SPEED, 0);
		}
	}
	
/** checks if the ball has crossed the side boundaries of the window 
 * @return: true if the ball has passed the side boundaries, false if not */
	private boolean xBoundCrossed() {
		if (this.ball.getX() >= WIDTH - BALL_RADIUS*2) {
			return true;
		}
		if (this.ball.getX() <= 0) {
			return true;
		}
		return false;
	}
	
/** checks if the ball has passed the bottom boundary of the window 
 * @return: true if ball has passed the bottom boundary, false otherwise */
	private boolean bottomBoundCrossed() {
		if (this.ball.getY() >= HEIGHT - BALL_RADIUS*2) {
			return true;
		}
		return false;
	}
	
/** checks if the ball has passed the top boundary 
 * @return: true if the ball has passed the top boundary, false if not*/
	private boolean topBoundCrossed() {
		if (this.ball.getY() <= 0) {
			return true;
		}
		return false;
	}

/** checks ball for collision with other objects 
 * @return: GObject that the ball has collided with, or null if no collision */
	private GObject getBallCollision() {
		GObject temp;
		temp = getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
		if (temp != null) {
			return temp;
		}
		temp = getElementAt(ball.getX() + 2 + BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
		if (temp != null) {
			return temp;
		}
		temp = getElementAt(ball.getX(), ball.getY());
		if (temp != null) {
			return temp;
		}
		temp = getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
		if (temp != null) {
			return temp;
		}
		return null;
	}
	
/** checks for collision with ball; if ball collides with paddle, bounce occurs;
 * if ball collides with brick, brick is removed; if all bricks destroyed, player wins */
	private void checkForCollision() {
		GObject collider = getBallCollision();
		if (collider == null) {
			return;
		}
		else if (collider == paddle) {
			this.bounceClip.play();
			if (this.ball.getX() < this.paddle.getX() + PADDLE_WIDTH/12)
			{
				this.ballVX *= -1;
			}
			if (this.ball.getX() > this.paddle.getX() + PADDLE_WIDTH - PADDLE_WIDTH/12)
			{
				this.ballVX *= -1;
			}
			this.ballVY *= -1;
			this.paddleHits++;
		}
		else { // collider must be a brick
			this.bounceClip.play();
			remove(collider);
			this.ballVY *= -1;
			this.bricksRemaining--;
			this.score += 7 * this.paddleHits;
			if (bricksRemaining <= 0) {
				this.winner = true;
				this.gameOver = true;
			}
		}
	}
	
/** stops ball movement and displays a win/lose message on screen */
	private void gameOver() {
		this.ballVX = 0;
		this.ballVY = 0;
		this.paddleLeft = false;
		this.paddleRight = false;
		if (this.winner) {
			drawBoxWithLabel(WIDTH/2 - GAME_OVER_BOX_WIDTH/2, HEIGHT/2 - GAME_OVER_BOX_HEIGHT/2, "YOU WIN!");
		}
		else {
			drawBoxWithLabel(WIDTH/2 - GAME_OVER_BOX_WIDTH/2, HEIGHT/2 - GAME_OVER_BOX_HEIGHT/2, "YOU LOSE :(");
		}
	}
	
/** draws a box with a label in program window; automatically centers the box and message  */
	private void drawBoxWithLabel(int x, int y, String name) {
		GRect rect = new GRect(x, y, GAME_OVER_BOX_WIDTH, GAME_OVER_BOX_HEIGHT);
		rect.setFilled(false);
		GLabel label = new GLabel(name, x, y);
		label.setFont("SansSerif-18");
		label.move((GAME_OVER_BOX_WIDTH/2) - (label.getWidth()/2), 
				(GAME_OVER_BOX_HEIGHT/2) + (label.getAscent()/2));
		add(rect);
		add(label);
	}

/** periodically speeds up game as player increases combo (successive hits without failure);
 * resets speed if ball crosses bottom boundary (i.e. combo break) */
	private void updateGameSpeed() {
		switch (this.paddleHits) {
        case 0:  
        	this.gameSpeed = GAME_SPEED_0;
        	break;
        case 1: 
        	this.gameSpeed = GAME_SPEED_1;
        	break;
        case 3:
        	this.gameSpeed = GAME_SPEED_2;
        	break;
        case 12:
        	this.gameSpeed = GAME_SPEED_3;
        	break;
        case 25:
        	this.gameSpeed = GAME_SPEED_4;
        	break;
		}
	}

/** updates the labels, removing them from the canvas then redrawing */
	private void updateLabels() {
		remove(this.scoreLabel);
		remove(this.comboLabel);
		initLabels();
	}
	
/** initializes the message telling the player to click to release the ball;
 * this message is stored as an instance variable */
	private void drawHoldMessage() {
		this.ballHeldMessage = new GLabel("Click to Release Ball", WIDTH/2, HEIGHT/2 + BALL_RADIUS * 4);
		this.ballHeldMessage.setFont("SansSerif-18");
		this.ballHeldMessage.move(-(this.ballHeldMessage.getWidth()/2), (this.ballHeldMessage.getAscent()/2));
		add(this.ballHeldMessage);
	}
	
/** private instance variables */
	private RandomGenerator rgen;
	private GRect paddle;
	private GOval ball; 
	private double ballVX; // ball velocity x
	private double ballVY; // ball velocity y
	private boolean paddleLeft = false; // determines if paddle will move left in update loop
	private boolean paddleRight = false; // determines if paddle will move right in update loop
	private boolean gameOver = false;
	private boolean winner = false;
	private int turnsLost = 0;
	private int bricksRemaining = NBRICK_ROWS * NBRICKS_PER_ROW;
	private AudioClip bounceClip = null; 
	private int paddleHits = 0; // determines player's combo, game speed, and score value for breaking bricks
	private int score = 0; 
	private double gameSpeed = GAME_SPEED_0;
	private GLabel scoreLabel;
	private GLabel comboLabel;
	private boolean ballHeld = true; // keeps the ball from moving
	GLabel ballHeldMessage;

}
