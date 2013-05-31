
public class Snakepart { //Generic Class of each segment of the snake

	public int x; //x coordinate of the snakepart
	public int y; //y coordinate of the snakepart
	
	public Snakepart(int x, int y){ //constructor for the snakepart
		this.x = x; //set the x coordinate of the snakepart
		this.y = y; //set the y coordinate of the snakepart
	}
	
	public int getX(){ //getter method of the x coordinate
		return x;
	}
	
	public int getY(){ //getter method of the y coordinate
		return y;
	}

	public void setX(int x){ //setter method of the x coordinate
		this.x = x;
	}
	
	public void setY(int y){ //setter method of the y coordinate
		this.y = y;
	}
	
	public boolean equalz(Snakepart p){ //checks to see if the current class snakepart is in the exact same location as the Snakepart p
		return (this.getX() == p.getX() && this.getY() == p.getY());
	}

	public String toString(){ //Convert the location of the snakepart to a string
		return "(" + x + ", " + y + ")";
	}
}
