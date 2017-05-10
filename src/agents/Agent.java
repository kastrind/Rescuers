package agents;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import javax.imageio.ImageIO;
import misc.Metrics;
import misc.Utils;
import environment.Position;
import environment.TheGrid;
import graphics.EnvGraphics;

/**
 * An agent acting in the world
 * @author Jim
 */
public class Agent implements Runnable{
	
	protected int id;
	private static int counter=0;
	protected Position pos;
	protected EnvGraphics env_g;
	protected Metrics metr;
	protected BufferedImage agent_sprite;
	protected static final int SLEEP_TIME=500;
	protected Thread thr;
	protected String team;
	protected String images_path;
	public boolean death_flag=false;
	
	/**
	 * Creates an agent at a given position
	 * @param pos the position of the agent
	 * @param env_g the graphics environment where the agent will be represented
	 * @param image the picture/sprite of the agent
	 * @param metr the score metric for this agent's team
	 * @param team the name of this agent's team
	 */
	public Agent(Position pos, EnvGraphics env_g, File image, Metrics metr, String team){
		id=counter++;
		this.pos = pos;
		TheGrid.getSquareAt(pos).addEntity(this);
		this.env_g = env_g;
		try {
			 agent_sprite = ImageIO.read(image);
		}catch (IOException e) {System.err.println("IOException caught: failed to set sprite for "+this.toString());}
		env_g.addEntity(pos, agent_sprite);
		this.images_path = image.getParent()+File.separator;
		this.metr = metr;
		this.team = team;
		thr = new Thread(this);
		thr.start();
	}
	
	public void run(){}
	
	/**
	 * Retrieves all adjacent positions to the current position
	 * and selects a random next position from the adjacent positions
	 * @return next position to go to
	 */
	protected Position getNextPosition(){
		ArrayList<Position> adjacent_positions = new ArrayList<Position>();
		//collect all possible adjacent positions
		adjacent_positions = TheGrid.getAllAdjacentPos(this.pos);
		//and go to a random adjacent position
		Random rand = new Random(System.currentTimeMillis());
		int i = rand.nextInt(adjacent_positions.size()-1);
		return adjacent_positions.get(i);
	}
	
	/**
	 * Move to the given adjacent destination, only if
	 * destination is adjacent and free of obstacles
	 * @param dest the destination
	 * @return new position, or null if movement could not be made
	 */
	protected Position moveTo(Position dest) {
		//if next position is adjacent and free of obstacles and rubble
		if(pos.isAdjacent(dest)&&(!TheGrid.getSquareAt(dest).hasObstacle())
				&&(!TheGrid.getSquareAt(dest).hasRubble())) {
			//move to that position
			TheGrid.getSquareAt(pos).removeEntity(this);
			TheGrid.getSquareAt(dest).addEntity(this);
			pos.setX(dest.getX());
			pos.setY(dest.getY());
			env_g.update(env_g.getGraphics());
			//and return it
			return pos;
		}
		//else return null
		return null;
	}
	
	/**
	 * Travels to a non adjacent destination.
	 * @param dest the destination
	 * @param sleep_per_move sleep for how long after each movement
	 * @return the current position, equal to the destination
	 */
	protected Position travelTo(Position dest, long sleep_per_move) {
		ArrayList<Position> adjpos;
		int new_distance, old_distance;
		int loop_cnt=0;
		//while not in our destination
		while(!pos.equals(dest)) {
			old_distance = TheGrid.getSquareAt(pos).getDistance(dest);
			adjpos = TheGrid.getAllAdjacentPos(pos);
			//by shuffling adjacent positions predictable movement decisions are prevented
			//and indirectly, sticking to a square when an obstacle is found is prevented
			//with the help of the loop_cnt mechanism
			Collections.shuffle(adjpos);
			//for every adjacent position
			for(Position curr_adj_pos : adjpos) {
				new_distance = TheGrid.getSquareAt(curr_adj_pos).getDistance(dest);
				//if distance from adjacent <= distance from current position
				if(new_distance<=old_distance) {
					//go to adjacent position
					moveTo(curr_adj_pos);
					sleep(sleep_per_move);
					break;
				}
				//else if all adjacent positions are checked then
				//all have greater distance from destination than
				//the distance between current position and destination.
				//The last resort is to go to that adjacent, and keep walking.
				//Shuffling the movement decisions will ultimately provide and alternative path
				else if(loop_cnt++==adjpos.size()){loop_cnt=0;moveTo(curr_adj_pos);break;}
			}
		}
		return pos;
	}
	
	/**
	 * Travels to a non adjacent destination, shortest path.
	 * @param dest the destination
	 * @param sleep_per_move sleep for how long after each movement
	 * @return the current position, equal to the destination
	 */
	protected Position travelTo2(Position dest, long sleep_per_move) {
		ArrayList<Position> adjpos, pos_ascend;
		HashMap<Position, Integer> pos2dist = new HashMap<Position, Integer>();;
		int curr_distance;
		//while not in our destination
		while(!pos.equals(dest)) {
			//get all adjacent positions to current position
			adjpos = TheGrid.getAllAdjacentPos(pos);
			pos2dist.clear();
			//for every adjacent position
			for(Position curr_adj_pos : adjpos) {
				//compute its distance from destination
				curr_distance = TheGrid.getSquareAt(curr_adj_pos).getDistance(dest);
				pos2dist.put(curr_adj_pos, curr_distance);
			}
			//sort adjacent positions by ascending distance from destination
			pos_ascend = Utils.arrangeKeys(pos2dist, "ascending");
			//move to the free position that decreases distance the most
			for(Position curr_adj_pos : pos_ascend) {
				if(moveTo(curr_adj_pos)!=null){sleep(250); break;}
				else continue;
			}
		}
		return pos;
	}
	
	/**
	 * Sleep for a given time
	 * @param millis the given time in milliseconds
	 */
	protected void sleep(long millis){
		try {Thread.sleep(millis);}
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	/**
	 * @return the id of the agent
	 */
	public int getId(){return this.id;}
	
	/**
	 * @return the current position of the agent
	 */
	public Position getPosition() {return this.pos;}
	
	/**
	 * @return the agent's sprite
	 */
	public BufferedImage getSprite() {return this.agent_sprite;}
	
	/**
	 * @return the agent's team name
	 */
	public String getTeamName() {return this.team;}
	
	/**
	 * String representation of the agent
	 */
	public String toString(){return "Agent_"+id+" from "+team;}
	
}
