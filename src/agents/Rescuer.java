package agents;

import misc.MP3Player;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import misc.Metrics;
import environment.Position;
import environment.TheGrid;
import environment.WorldEntity;
import environment.WorldEntity.WEType;
import graphics.EnvGraphics;

/**
 * A rescuer agent of the world as a thread
 * @author Jim
 */
public class Rescuer extends Agent {
	
	//the ambulance assigned to this rescuer's team
	protected Ambulance team_ambulance;
	private MP3Player mp3;

	/**
	 * Creates a new rescuer at a given position
	 * @param pos the position of the rescuer
	 * @param env_g the graphics environment where the rescuer will be represented
	 * @param image the picture/sprite of the rescuer
	 * @param team_ambulance the ambulance assigned for this rescuer's team
	 * @param metr the score metric for this rescuer's team
	 * @param team the name of this rescuer's team
	 */
	public Rescuer(Position pos, EnvGraphics env_g, File image, Ambulance team_ambulance, Metrics metr, String team){
		super(pos, env_g, image, metr, team);
		this.team_ambulance = team_ambulance;
		mp3 = new MP3Player("."+File.separator+"sounds"+File.separator+"GrenadeExplosion.mp3");
	}
	
	/**
	 * Selects a next position, goes there, searches for victims and if found, secures them.
	 * Updates graphical environment and sleeps for a bit so that the movements are not rapid
	 */
	public synchronized void run(){
		WorldEntity victim;
		while(!death_flag) {
			//select a position, go there, secure victim if found
			if((victim = searchForVictims(moveTo(getNextPosition())))!=null) {checkVictim(victim);}
			sleep(SLEEP_TIME);
		}
	}
	
	/**
	 * Retrieves all adjacent positions to the current position and returns a position
	 * that satisfies one of the below ordered by descending priority:
	 * 1. position contains a victim
	 * 2. position has rubble
	 * 3. position is unexplored
	 * 4. random position
	 * @return next position to go to
	 */
	protected Position getNextPosition(){
		ArrayList<Position> adjacent_positions = new ArrayList<Position>();
		//collect all possible adjacent positions
		adjacent_positions = TheGrid.getAllAdjacentPos(this.pos);
		//for all possible adjacent positions
		for (Position pos : adjacent_positions) {
			//if one of them has a victim without rubble, go there
			if (TheGrid.getSquareAt(pos).hasNewVictim()&&!TheGrid.getSquareAt(pos).hasRubble()) {
				TheGrid.getSquareAt(pos).setExplored();
				return pos;
			}
		}
		for (Position pos : adjacent_positions) {
			//if one of them has rubble, remove rubble and go there
			if(TheGrid.getSquareAt(pos).hasRubble()) {
				removeRubble(pos);
				TheGrid.getSquareAt(pos).setExplored();
				return pos;
			}
		}
		for (Position pos : adjacent_positions) {
			//else if one of them is unexplored, go there
			if(!TheGrid.getSquareAt(pos).isExplored()) {
				TheGrid.getSquareAt(pos).setExplored();
				return pos;
			}
		}
		//or go to a random adjacent position
		Random rand = new Random(System.currentTimeMillis());
		int i = rand.nextInt(adjacent_positions.size()-1);
		TheGrid.getSquareAt(adjacent_positions.get(i)).setExplored();
		return adjacent_positions.get(i);
	}
	
	/**
	 * Removes rubble from the specified position
	 * @param pos the specified position
	 */
	protected void removeRubble(Position pos) {
		ArrayList<Object> entities = TheGrid.getSquareAt(pos).getEntities();
		synchronized(entities) {
			for(Object entity : entities) {
				if(entity instanceof WorldEntity) {
					WorldEntity we = (WorldEntity)entity;
					//find the rubble and remove it
					if(we.getType()==WEType.RUBBLE) {
						//crash it
						we.crashRubble();
						if(we.getCrashes()<4) {
							mp3.play();
							we.setSprite(new File(super.images_path+"rubble"+we.getCrashes()+".png"));
							env_g.update(env_g.getGraphics());
						}
						sleep(SLEEP_TIME);
						System.err.println(this.toString()+" "+we.getCrashes());
						return;
						
					}
				}
			}
		}
	}
	
	/**
	 * Looks for a victim at the given position and if found, returns it
	 * @param pos the given position
	 * @return a victim entity if there is a victim, else null
	 */
	protected WorldEntity searchForVictims(Position pos){
		//if moveTo did not return null, then a movement has been made
		if (pos!=null) {
			ArrayList<Object> entities = new ArrayList<Object>(TheGrid.getSquareAt(pos).getEntities());
			//for all entities in that square
			for(Object obj : entities) {
				if(obj instanceof WorldEntity) {
					WorldEntity we = (WorldEntity)obj;
					//if the current entity is a new victim, return it
					if(we.getType()==WEType.VICTIM&&(we.isChecked()==false)){
						System.out.println(this.toString()+" found a new "+we.toString());
						callAmbulance(pos);
						sleep(500);
						return we;
					}
				}
			}
		}
		// if there are no victims or
		//if moveTo returned null, then a movement has not been made, return null victim
		return null;
	}
	
	/**
	 * Sets the found victim as checked
	 * @param we the found victim
	 */
	protected void checkVictim(WorldEntity we){
		if(we.getType()==WEType.VICTIM) {
			we.setChecked();
			we.setSprite(new File(super.images_path+"victim_checked.png"));
		}
	}
	
	/**
	 * Informs the ambulance agent of the newly found victim and its position
	 * @param dest the position of the newly found victim
	 * @param victim_to_get the newly found victim
	 */
	protected void callAmbulance(Position dest){
		team_ambulance.addVictimPos(new Position(dest.getX(), dest.getY()));
		System.out.println(this.toString()+" called "+team_ambulance.toString()+" for position "+dest.toString());
	}
	
	public String toString(){return "Rescuer "+super.toString();}
}
