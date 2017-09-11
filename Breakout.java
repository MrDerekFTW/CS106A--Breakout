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
	private static final int PADDLE_WIDTH = 60;
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
	private static final int PADDLE_SPEED = 10;

/** Number of turns */
	private static final int NTURNS = 3;

/** game speed determines difficulty */
	private static final double GAME_SPEED = 7.7;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		setupBreakout();
		playBreakout();
	}
	
	private void setupBreakout() {
		initRandom();
		drawBricks();
		initPaddle();
		initBall();
		addKeyListeners();
		addMouseListeners();
	}
	
	private void playBreakout() {
		boolean gameOver = false;
		while (!gameOver) {
			updateBall();
			pause(GAME_SPEED);
		}
	}
	
	private void initRandom() {
		this.rgen = RandomGenerator.getInstance();
	}
	
/* moves the paddle according to the mouse position when mouse is on screen */
	public void mouseMoved(MouseEvent e) {
		if (e.getX() < WIDTH) {
			if (e.getX() > WIDTH - PADDLE_WIDTH) {
				this.paddle.setLocation(WIDTH - PADDLE_WIDTH, this.paddle.getY());
			}
			else if (e.getX() < PADDLE_WIDTH) {
				this.paddle.setLocation(0, this.paddle.getY());
			}
			else {
				this.paddle.setLocation(e.getX(), this.paddle.getY());
			}
			add(this.paddle);
		}
	}
	
/* alternative movement method for when the mouse is not moving */ 
	public void keyPressed(KeyEvent e) {
		if (this.paddle != null) {
			switch (e.getKeyCode()) {
		        case KeyEvent.VK_LEFT:  
		        	if (this.paddle.getX() - PADDLE_SPEED > 0) {
		        		this.paddle.move(-PADDLE_SPEED, 0);
		        	}
		        	else {
		        		this.paddle.setLocation(0, this.paddle.getY());
		        	}
		        	break;
		        case KeyEvent.VK_RIGHT: 
		        	if (this.paddle.getX() + PADDLE_SPEED < WIDTH - PADDLE_WIDTH) {
		        		this.paddle.move(PADDLE_SPEED, 0);
		        	} 
		        	else {
		        		this.paddle.setLocation(WIDTH - PADDLE_WIDTH, this.paddle.getY());
		        	}
		        	break;
			}
		}
	}
	
	private void drawBricks() {
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET, Color.RED);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + BRICK_HEIGHT + BRICK_SEP, Color.ORANGE);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 2, Color.YELLOW);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 4, Color.GREEN);
		drawPairBrickRows(CENTERED_ROW_X, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * 6, Color.CYAN);
	}
	
	private void drawPairBrickRows(int x, int y, Color color)
	{
		for (int i = 0; i < 2; i++)
		{
			drawBrickRow(x, y, color);
			y += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	private void drawBrickRow(int x, int y, Color color) {
		for (int i = NBRICKS_PER_ROW; i > 0; i--) {
			drawBrick(x, y, color);
			x += BRICK_WIDTH + BRICK_SEP;
		}
	}
	
	private void drawBrick(int x, int y, Color color) {
		GRect brick = new GRect (x, y, BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(Color.BLACK);
		brick.setFillColor(color);
		add(brick);
	}
	
	private void initPaddle() {
		this.paddle = new GRect((WIDTH/2) - (PADDLE_WIDTH/2), 
				HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
		this.paddle.setFilled(true);
		this.paddle.setColor(Color.BLACK);
		add(paddle);
	}
	
	private void initBall() {
		this.ball = new GOval(BALL_RADIUS * 2, BALL_RADIUS * 2);
		this.ball.setFilled(true);
		add(this.ball, WIDTH/2 - BALL_RADIUS, HEIGHT/2 - BALL_RADIUS);
		this.ballVX = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			this.ballVX *= -1;
		}	
		this.ballVY = 3.0;
	}
	
	private void updateBall() {
		if (xBoundCrossed()) {
			ballVX *= -1;
		}
		if (yBoundCrossed()) {
			ballVY *= -1;
		}
		this.ball.move(ballVX, ballVY);
	}
	
	private boolean xBoundCrossed() {
		if (this.ball.getX() >= WIDTH - BALL_RADIUS*2) {
			return true;
		}
		if (this.ball.getX() <= 0) {
			return true;
		}
		return false;
	}
	
	private boolean yBoundCrossed() {
		if (this.ball.getY() >= HEIGHT - BALL_RADIUS*2) {
			return true;
		}
		if (this.ball.getY() <= 0) {
			return true;
		}
		return false;
	}
	
/** private instance variables */
	private RandomGenerator rgen;
	private GRect paddle;
	private GOval ball; 
	private double ballVX; // ball velocity x
	private double ballVY; // ball velocity y

}
