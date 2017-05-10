package agents;

import misc.MP3Player;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import misc.Metrics;
import environment.Position;
import environment.TheGrid;
import environment.WorldEntity;
import environment.WorldEntity.WEType;
import graphics.EnvGraphics;

/**
 * An ambulance agent of the world as a thread
 * @author Jim
 */
public class Ambulance extends Agent{

	protected ArrayList<Position> victim_pos;
	protected Position hospital_pos;
	private MP3Player mp3;
	private boolean ascending_distance;

	/**
	 *Creates a new ambulance at a given position
	 * @param pos the position of the ambulance
	 * @param hospital_pos the position of the hospital
	 * @param env_g the graphics environment where the ambulance will be represented
	 * @param image the picture/sprite of the ambulance
	 * @param metr the score metric for the team of this ambulance
	 * @param team the name of the team of this ambulance
	 * @param ascending_distance whether the ambulance will travel to the victim closest to the hospital
	 */
	public Ambulance(Position pos, Position hospital_pos, EnvGraphics env_g, File image, Metrics metr, String team, boolean ascending_distance) {
		super(pos, env_g, image, metr, team);
		victim_pos = new ArrayList<Position>();
		this.hospital_pos = hospital_pos;
		mp3 = new MP3Player("."+File.separator+"sounds"+File.separator+"Ambulance.mp3");
		this.ascending_distance = ascending_distance;
	}
	
	public void run(){
		WorldEntity victim;
		int index_closest;
		while(!death_flag) {
			try{
			while(victim_pos.isEmpty()) {Thread.yield();}
			while(!victim_pos.isEmpty()) {
				mp3.play();
				
				//find the victim position nearest to the hospital
				if(ascending_distance) index_closest = indexOfClosestPosition(hospital_pos);
				else index_closest = 0;
				System.out.println(this.toString()+" going to "+victim_pos.get(index_closest).toString());
				
				super.travelTo2(victim_pos.get(index_closest),250);
				
				victim = searchForVictims(victim_pos.get(index_closest));
				
				if(victim==null) {//if victim has died, continue
					victim_pos.remove(index_closest);
					continue;
				}
				
				if(mp3.isComplete()){
					mp3.play();
				}
				
				secureVictim(victim_pos.get(index_closest), victim);
				
				System.out.println(this.toString()+" going back to hospital at "+hospital_pos.toString());
				super.travelTo2(hospital_pos, 250);
				
				if(!victim.is_dead) {//if victim still alive, it's rescued!
					metr.victimRescued();
					victim.setSafe();
					System.out.println(victim.toString()+" at "+victim_pos.get(index_closest).toString()+" rescued!");
				}
				else {System.out.println("Death during transfer!");}
				System.out.println("Current score for "+super.getTeamName()+": "+metr.getScore());
				victim_pos.remove(index_closest);
			}
			super.travelTo2(hospital_pos, 250);//back to hospital
			victim_pos.clear();
			}catch(NullPointerException e) {}
		}
	}
	
	/**
	 * Removes the victim from the square at the given position
	 * and also removes it from the graphical environment
	 * only if it is indeed a victim
	 * @param pos the given position
	 * @param we the given world entity 
	 */
	protected void secureVictim(Position pos, WorldEntity we){
		if(we.getType()==WEType.VICTIM) {
			//remove victim from square
			TheGrid.getSquareAt(pos).removeEntity(we);
			//remove it from graphics
			env_g.removeEntity(we.getPosition(), we.getSprite());
			System.out.println(we.toString()+" removed from "+ TheGrid.getSquareAt(pos).toString()+"!");
		}
	}
	
	/**
	 * Looks for a victim at the given position and if found, returns it
	 * @param pos the given position
	 * @return a victim entity if there is a victim, else null
	 */
	protected WorldEntity searchForVictims(Position pos){
		if (pos!=null) {
			ArrayList<Object> entities = new ArrayList<Object>(TheGrid.getSquareAt(pos).getEntities());
			//for all entities in that square
			for(Object obj : entities) {
				if(obj instanceof WorldEntity) {
					WorldEntity we = (WorldEntity)obj;
					//if the current entity is a victim, return it
					if(we.getType()==WEType.VICTIM){ 
						return we;
					}
				}
			}
		}
		// if there are no victims
		return null;
	}
	
	/**
	 * Finds the minimum distance of all victim positions
	 * and returns the index of the closest position 
	 * @param pos the position
	 * @return the index of the closest position
	 */
	protected int indexOfClosestPosition(Position pos){
		ArrayList<Integer> distances = new ArrayList<Integer>();
		int min_distance;
		for(int i=0; i<victim_pos.size(); i++) {
			distances.add(pos.getDistance(victim_pos.get(i)));
		}
		min_distance = Collections.min(distances);
		return distances.indexOf(min_distance);
	}
	
	/**
	 * @return the positions of the victims to retrieve
	 */
	public ArrayList<Position> getVictimPos(){return victim_pos;}
	
	/**
	 * Add a victim position to go to
	 * @param dest the victim's position
	 */
	public void addVictimPos(Position dest){
		synchronized(victim_pos){victim_pos.add(dest);}
	}
	
	/**
	 * Stops the siren
	 */
	public void stopSiren(){
		this.mp3.close();
	}
	
	/**
	 * String representation of the ambulance
	 */
	public String toString(){return "Ambulance "+super.toString();}	
}
