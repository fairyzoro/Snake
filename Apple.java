import java.util.Random;

//Generic Class of an Apple
public class Apple {

	Random r; //Random used to set the first value of the 
	private int x; //x coordinate of the apple
	private int y; //y coordinate of the apple
	private static int width; //width of the apple (this will remain the same).
	private static int height; //height of the apple (this will also remain the same).
	
	public Apple(int width, int height){ //constructor for an Apple
		r = new Random(); //instantiate the random
		this.width = width; //set the width of the apple from provided data of the constructor
		this.height = height; //set the height of the apple from the provided data of the constructor
		this.x = r.nextInt(width); //set the first random x coordinate of the apple
		this.y = r.nextInt(height); //set the first random y coordinate of the apple
	}
	
	public int getX(){ //getter method for x
		return x;
	}
	
	public int getY(){ //getter method for y
		return y;
	}

	public void setX(int x){ //setter method for x
		this.x = x;
	}
	
	public void setY(int y){ //setter method for y
		this.y = y;
	}
	
	public boolean equalz(Apple p){ //returns true if this current class of apple is in the exact same location as Apple p
		return (this.getX() == p.getX() && this.getY() == p.getY());
	}
	
	public String toString() { //toString method, call this to "convert" the apple to a string, giving us a string with the coordinates of the apple
		return "(" + x + ", " + y + ")";
	}
}
