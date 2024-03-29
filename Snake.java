//import various classes we need.

import java.applet.AudioClip;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

public class Snake extends Canvas implements Runnable {//Class of the game
    
	
	public static void main(String[] args) {//main method
		
		System.out.println("main");
		Snake game = new Snake();//create a new instance of the class
		game.frame.setResizable(false);
		game.frame.setTitle(title);
		game.frame.add(game);//add the class to the frame object (the class is a canvas object)
		game.frame.pack();//pack the frame
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//set window to close when X button is pressed
		game.frame.setLocationRelativeTo(null);//set location to middle of screen
		game.frame.setVisible(true);
		r = new Random();//instantiate random object
		parts = new ArrayList<Snakepart>();//initialize the array of snake parts
		parts.add(new Snakepart(width/2, height/2));//create the first snake part in the middle of the screen
		apple = new Apple(width, height);//create the first apple, in a random location
		
		//set the possible colors of the snake to all the colors of the rainbow!
		//this works but I don't like how it looks...
		rainbow = new int[7];
		rainbow[0] = 0xff0000;
		rainbow[1] = 0xffa500;
		rainbow[2] = 0xffff00;
		rainbow[3] = 0x00ff00;
	    rainbow[4] = 0x0000ff;
		rainbow[5] = 0x4b0082;
		rainbow[6] = 0xee82ee;

		//color = rainbow[0];
		//if(new Scanner(System.in).nextLine().equals("dw"))

		game.start(); //run the start method
	}

	private static int[] rainbow; //declare rainbow array
	private static int oldDirection = 0; //the last direction the snake was going in before the current one
	private static Random r; //declare random object
	public static int width = 32; //declare and initialize width
	public static int height = width / 16 * 9; //set height, in a 16:9 ratio
	public static int scale = 27; //set the pixels to scale by 27
	private static double updatesPerSecond = 10.0; //update entire game every 10 seconds (the mysterious UPS)
	private static ArrayList<Snakepart> parts;//declare arraylist of snakeparts
	private static Apple apple;//declare the apple
	private static String score = "0"; //set score to 0
	private static final int movement = 1; //direction the snake is going in
	private static int direction = 0; //0 = not moving, 1 = up, 2 = right, 3 = left, 4, = down
	public static String title = "Snake";
	private static Thread thread; //thread the game will run in
	JFrame frame; //frame object
	private boolean running = false; //whether or not the game is running
    private static final int color = 0xdead & 0xbeef; //original color of snakepart
	private static boolean reset = false; //should the game reset
	private static boolean lose = false; //has the player lost (has the snake touches itself, or the wall)
	private static boolean started = false; //has the game started
	private static boolean playClipLose = false; //should the lose clip be played
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //image displayed on screen
	private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData(); //pixels of image displayed on screen

	private static Keyboard key; //declare keyboard to get keyboard input

	int x = 0, y = 0; //x and y coordinates of something

	public Snake(){
		System.out.println("new game");
		Dimension size = new Dimension(width*scale, height*scale); //find the size of the screen
		setPreferredSize(size); //set the size

		frame = new JFrame(); //initialize the frame
		key = new Keyboard(); //initialize the 
		addKeyListener(key); //say we want keyboard to be able to get key input
	}

	public synchronized void start(){
		System.out.println("start");
		running = true;//set running to true, because the game has now started
		thread = new Thread(this); //initialize the new thread.
		thread.start(); //runs the "run" method in snake
	}

	public synchronized void stop(){
		running = false; //set running to false
		try{
			thread.join(); //kill the thread
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	public void run(){
		//xSystem.out.println("run");
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0/updatesPerSecond;
		double delta = 0;
		int frames = 0;
		int updates = 0;
		requestFocus();

		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				update();//restrict to running 10 ups
				updates++;
				delta--;
			}
			
			render(lose, started);//unrestricted  FPS
			frames++;

			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				//System.out.println(updates + " ups, " + frames + " fps");
				frame.setTitle(title + " | " + updates + " ups, " + frames + "fps"); //prints FPS and UPS
				updates = 0;
				frames = 0;
			}
		}
		stop(); //if while loop has finished, stop the program.
	}

	public void update(){
		
		key.update(); //find out what keys are pressed
		
		//find out what direction the snake should be going in
		//make sure it does not double back on itself
		
		keyLogic:{
			if(key.up&&direction!=4&&!lose&&oldDirection!=4){
				direction = 1;
				break keyLogic;
			}
			if(key.down&&direction!=1&&!lose&&oldDirection!=1){
				direction = 4;
				break keyLogic;
			}
			if(key.left&&direction!=2&&!lose&&oldDirection!=2){
				direction = 3;
				break keyLogic;
			}
			if(key.right&&direction!=3&&!lose&&oldDirection!=3){
				direction = 2;
				break keyLogic;
			}

			if(key.space&&!started) // start the game when space is pressed
				started = true;
		}
		
		if(key.pause){
			//pause the game, by stopping the snake
			oldDirection = direction;
			direction = 0;
		}
		
		if(key.reset) reset(); //if r is pressed, reset the game
		
		//duplicate the snakeparts array
		ArrayList<Snakepart> oldParts = new ArrayList<Snakepart>();
		for(int i = 0; i < parts.size(); i++) oldParts.add(parts.get(i));


		
		//move the first snakepart in the direction it is going
		if(direction == 1) parts.set(0, new Snakepart(parts.get(0).getX(), parts.get(0).getY()-movement));
		if(direction == 2) parts.set(0, new Snakepart(parts.get(0).getX()+movement, parts.get(0).getY()));
		if(direction == 3) parts.set(0, new Snakepart(parts.get(0).getX()-movement, parts.get(0).getY()));
		if(direction == 4) parts.set(0, new Snakepart(parts.get(0).getX(), parts.get(0).getY()+movement));

		//set each part above the one at index 0 to where the part in front of it was during the last update
		for(int i = 1; (i < parts.size() && direction!=0); i++){
			parts.set(i, oldParts.get(i-1));
		}

		//check to see if the snake is touching the wall, if so, run associated "losing" code
		if(parts.get(0).getX()<0||parts.get(0).getX()>(width-1)||parts.get(0).getY()<0||parts.get(0).getY()>(height-1)){
			lose = true;
			direction = 0;
			playSound("lose.wav");
			playClipLose = true;
		}

		//check to see if any part of the snake is touching itself, ifso, running losing code
		for(int a = 0; a < parts.size(); a++){
			for(int b = 0; b < parts.size(); b++){
				if(a!=b){
					if(parts.get(a).equalz(parts.get(b))){
						playSound("lose.wav");
						playClipLose = true;
						lose = true;
						direction = 0;
						return;
					}

				}
			}
		}

		//run code to see if snake is touching apple, if so, the snake "eats the apple"
		if(collides(apple, parts.get(0))){ //check if snake got apple
			playSound("munch.wav");
			changeAppleLocation(); //set apple to new location
			//length parts

			//add a new snakepart on to the end of snakeparts
			if(direction == 1){
				parts.add(new Snakepart(parts.get(0).getX(), parts.get(0).getY()+1));
			}
			if(direction == 2){
				parts.add(new Snakepart(parts.get(0).getX()-1, parts.get(0).getY()));
			}
			if(direction == 3){
				parts.add(new Snakepart(parts.get(0).getX()+1, parts.get(0).getY()));
			}
			if(direction == 4){
				parts.add(new Snakepart(parts.get(0).getX(), parts.get(0).getY()-1));
			}
			
			//increase the score by 10
			int scoreInt = Integer.parseInt(score);
			scoreInt += 10;
			score = "" + scoreInt; //score could be scored in int, but it doesn't make a difference, because when it is displayed, it will be concatanated onto a string anyway
		}

	}

	private static void reset(){
		//reset snakeparts
		parts = new ArrayList<Snakepart>();
		System.out.println("reset");
		reset = true;
		parts.add(new Snakepart(width/2, height/2));//put original snakepart in middle of screen
		//create new apple
		apple = null;
		apple = new Apple(width, height);
		score = "0"; //reset score
		direction = 0; //set snake to not moving
		lose = false;
		playClipLose = false;
	}

	private static void changeAppleLocation(){
		//set x and y to random locations
		int newX = r.nextInt(width);
		int newY = r.nextInt(height);
		//make sure apple is not in same location (too boring).
		if(newX==apple.getX()&&newY==apple.getY()) changeAppleLocation();
		//change the apple's location
		apple.setX(r.nextInt(width));
		apple.setY(r.nextInt(height));
	}
	private boolean collides(Apple a, Snakepart p){
		//see if the apple and snakepart collide
		return(a.getX() == p.getX() && a.getY() == p.getY());
	}

	public void render(boolean death, boolean start){
		//initalize bufferstrategy which holds rendered images in RAM
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			System.out.println("genesis");
			createBufferStrategy(3);
			return;
		}


		Graphics g = bs.getDrawGraphics(); //create new Graphics instance

		//rendering code if game HAS STARTED
		if(started){
			if(!(parts.get(0).getX()<0||parts.get(0).getX()>width-1||parts.get(0).getY()<0||parts.get(0).getY()>height-1)&&parts.size()>0){
				if(!death){
					for(int i = 0; i < pixels.length; i++) pixels[i] = 0x0; //set all pixels to black, originally

					loop:{
						for(int i = 0; i < parts.size(); i++){
							if(parts.get(i).getX()<0||parts.get(i).getX()>width-1||parts.get(i).getY()<0||parts.get(i).getY()>height-1) break loop; //don't render off screen
							pixels[parts.get(i).getX() + parts.get(i).getY() * width] = (color << i) | 0x0000ff; //set snakepart's color
						}
					}
					pixels[apple.getX() + apple.getY() * width] = 0xff0000; //set apple to red

					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
					g.setFont(new Font("American Typewriter", Font.BOLD, 50));
					g.drawString(score, width-20, 50);
				}
			}
			//rendering code if GAME HAS STARTED, and player has lost
			if(death){
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				for(int i = 0; i < pixels.length; i++) pixels[i] = 0x0;
				g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
				g.setFont(new Font("American Typewriter", Font.BOLD, 50));
				g.drawString("YOU LOSE", 200, 200);
				g.drawString("THANKS FOR PLAYING!", 200, 300);
				g.drawString("PRESS R TO RESET", 200, 400);
			}
			g.dispose(); //get rid of current graphics instance
			bs.show(); //show the image
		} else {
			//rendering code if game HAS NOT STARTED
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			for(int i = 0; i < pixels.length; i++) pixels[i] = 0x0;
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
			g.setFont(new Font("American Typewriter", Font.BOLD, 50));
			g.drawString("Welcome to Snake", 40, 100);
			g.drawString("Use the arrow", 40, 200);
			g.drawString("Keys to move", 40, 300);
			g.drawString("Press space to start", 40, 400);
			

			g.dispose(); //get rid of current graphics instance
			bs.show(); //show the image
		}



	} 

	public void playSound(String name){
		//play sound, loading into memory, and then playing
		if(!playClipLose){
			if(lose) playClipLose = true;
			try {
				//could load sound into RAM immediately, but playing around with how fast harddrive can spin up
				File soundFile = new File("" + name);
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
				Clip clip;

				clip = AudioSystem.getClip();

				clip.open(audioInputStream);
				clip.start();//This plays the audio

			} catch (LineUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}