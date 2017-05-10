package environment;

import java.util.ArrayList;

/**
 * The World as a grid of squares
 * @author Jim
 */
public class TheGrid {

	private static int count=0;
	private static Square[][] squares;
	private static int size;

	/**
	 * Builds a rectangular grid of a given size
	 * @param size the number of squares each rectangle side has
	 */
	private TheGrid(int size){
		 TheGrid.size = size;
		 squares = new Square[size][size];
		 Position pos;
		 for(int x=0; x<size; x++) {
			 for(int y=0; y<size; y++) {
				 pos = new Position(x,y);
				 squares[x][y] = new Square(pos);
			}
		}
		 count++;
	}
	
	/**
	 * Builds a rectangular grid of a given size
	 * @param size the number of squares each rectangle side has
	 * @return
	 */
	public static TheGrid createGrid(int size) {
		if(count==0)return new TheGrid(size);
		else return null;
	}
	
	/**
	 * Retrieves a square at the given position
	 * @param pos the position of the square
	 * @return the square
	 */
	public static Square getSquareAt(Position pos) {
		return squares[pos.getX()][pos.getY()];
	}
	
	/**
	 * @return the squares of the grid
	 */
	public static Square[][] getGrid(){return squares;}
	
	/**
	 * @return the size of the grid
	 */
	public static int getSize(){return size;}

	/**
	 * Returns all adjacent positions to the given position
	 * @param pos the given position
	 * @return all adjacent positions
	 */
	public static ArrayList<Position> getAllAdjacentPos(Position pos) {
		ArrayList<Position> adjpos = new ArrayList<Position>();
		//collect all possible adjacent positions
		if (pos.getX()+1<getSize())		{adjpos.add(new Position(pos.getX()+1,pos.getY()));}
		if (pos.getY()+1<getSize())		{adjpos.add(new Position(pos.getX(), pos.getY()+1));}
		if (pos.getX()-1>=0) 			{adjpos.add(new Position(pos.getX()-1, pos.getY()));}
		if (pos.getY()-1>=0) 			{adjpos.add(new Position(pos.getX(), pos.getY()-1));}
		if ((pos.getX()+1<getSize())&&(pos.getY()+1<getSize())) {adjpos.add(new Position(pos.getX()+1, pos.getY()+1));}
		if ((pos.getX()-1>=0)&&(pos.getY()-1>=0))   			{adjpos.add(new Position(pos.getX()-1, pos.getY()-1));}
		if ((pos.getX()+1<getSize())&&(pos.getY()-1>=0))		{adjpos.add(new Position(pos.getX()+1, pos.getY()-1));}
		if ((pos.getX()-1>=0)&&(pos.getY()+1<getSize()))		{adjpos.add(new Position(pos.getX()-1, pos.getY()+1));}
		return adjpos;
	}
	
	/**
	 * String representation of the grid
	 */
	public String toString() {
		String str="Grid of squares: \n";
		 for(int x=0; x<size; x++) {
			 for(int y=0; y<size; y++) {str=str.concat(squares[x][y].toString()+"\n");}
		}
		return str;
	}
	
	/**
	 * Clears all the squares of the grid,
	 * allowing to recreate the grid
	 */
	public static void reset() {
		count=0;
		for(int x=0; x<size; x++) {
			 for(int y=0; y<size; y++) {
				 squares[x][y].reset();
			 }	
		}
	}
}
