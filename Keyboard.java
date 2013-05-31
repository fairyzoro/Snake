import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener { //Keyboard class for getting user input.

	
	private boolean[] keys = new boolean[2560]; //Array of keys, to see what keys are pressed down. Large to allow for pretty much number of keyboard keys. any key that is pressed will be placed in this array at the index of its key ID and it will have the value of either true (pressed) or false (not pressed). 
	public boolean up = false; //returns true if up arrow key or w key is pressed
	public boolean down = false; //returns true if down arrow key or s key is pressed
	public boolean left = false; //returns true if left arrow key or a key is pressed
	public boolean right = false; //returns true if right arrow key or d key is pressed
	public boolean space = false; //returns true if space bar is pressed 
	public boolean reset = false; //returns true if reset key (r key) is pressed.
	public boolean pause = false; //returns true if pause key (p key) is pressed.
	
	
	public void update(){
		up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];//set the value of up
		down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];//set the value of down
		left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];//set the value of left
		right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];//set the value of right
		space = keys[KeyEvent.VK_SPACE];//set the value of space
		reset = keys[KeyEvent.VK_R];//set the value of reset
		pause = keys[KeyEvent.VK_P];//set the value of pause
	}

	public void keyPressed(KeyEvent e) {
		//when any key is pressed, set that key to true in the keys array
		
		keys[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		//when any key is released, set that key to false in the keys array
		keys[e.getKeyCode()] = false;
	}
	
	public void keyTyped(KeyEvent e) {
		//unused method
	}

}
