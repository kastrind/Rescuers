package environment;

/**
 * A position in cartesian representation of the 2D space
 * @author Jim
 *
 */
public class Position implements Comparable<Position>{

	private int x, y;
	
	/**
	 * Creates a new position
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @return the x coordinate
	 */
	public int getX(){return this.x;}
	
	/**
	 * @param x the x coordinate
	 */
	public void setX(int x) {this.x = x;}
	
	/**
	 * @return the y coordinate
	 */
	public int getY(){return this.y;}
	
	/**
	 * @param y the y coordinate
	 */
	public void setY(int y) {this.y = y;}
	
	/**
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void setPosition(int x, int y){this.setX(x);this.setY(y);}
	
	/**
	 * @param pos the given position
	 * @return true if given position equal to this position, else false
	 */
	public boolean equals(Position pos) {
		if(pos.getX()==this.getX()&&pos.getY()==this.getY()) return true;
		return false;
	}
	
	/**
	 * @param pos the given position
	 * @return true if position is adjacent with given position, else false
	 */
	public boolean isAdjacent(Position pos){
		if((Math.abs(this.getX()-pos.getX())<=1)&&(Math.abs(this.getY()-pos.getY())<=1)) return true;
		return false;
	}
	
	/**
	 * @param pos the position
	 * @return the distance of this position from the given position
	 */
	public int getDistance(Position pos) {
		return Math.abs(getX()-pos.getX())+
							Math.abs(getY()-pos.getY());
	}
	
	/**
	 * String representation of the position
	 */
	public String toString(){
		return "("+this.getX()+" , "+this.getY()+")";
	}

	/**
	 * Compares two positions
	 * @return positive integer if this position is greater than specified position,
	 * nevative integer if this position is less than specified position,
	 * zero if this position is equal to specified position
	 */
	public int compareTo(Position pos) {
		int x_diff, y_diff;
		x_diff = this.getX()-pos.getX();
		y_diff = this.getY()-pos.getY();
		return x_diff+y_diff;
	}
}
