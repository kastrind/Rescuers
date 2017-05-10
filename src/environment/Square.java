package environment;

import java.util.ArrayList;
import agents.Agent;
import environment.WorldEntity.WEType;

/**
 * A square of the grid
 * @author Jim
 */
public class Square {

	private Position pos;
	private ArrayList<Object> entities_in;
	private boolean is_explored;
	
	/**
	 * Create a new square
	 * @param pos at this position
	 */
	public Square(Position pos){
		this.pos = pos;
		entities_in = new ArrayList<Object>();
		is_explored=false;
	}
	
	/**
	 * @return the position of this square
	 */
	public Position getPosition(){return pos;}
	
	/**
	 * Put an entity at this square
	 * @param ent the entity 
	 */
	public void addEntity(Object ent){
		synchronized(entities_in) {
			if(ent instanceof WorldEntity) {
				WorldEntity we = (WorldEntity)ent;
				we.setPosition(this.pos);
			}
			entities_in.add(ent);
		}
	}
	
	/**
	 * Remove an entity from this square
	 * @param ent the entity
	 * @return true if entity was contained in the square, else false
	 */
	public boolean removeEntity(Object obj) {
		synchronized(entities_in) {
			return entities_in.remove(obj);
		}
	}
	
	/**
	 * @return all entities at this square
	 */
	public ArrayList<Object> getEntities() {return entities_in;}
	
	/**
	 * @return true if there are no entities at this square, else false
	 */
	public boolean isFree(){return entities_in.isEmpty();}
	
	/**
	 * @param pos the position
	 * @return the distance of this square from the given position
	 */
	public int getDistance(Position pos) {
		return Math.abs(this.getPosition().getX()-pos.getX())+
							Math.abs(this.getPosition().getY()-pos.getY());
	}
	
	/**
	 * @return true if this square has an entity of type obstacle or an entity of type agent,
	 * else false
	 */
	public boolean hasObstacle(){
		ArrayList<Object> entities = new ArrayList<Object>(this.getEntities());
		for(Object obj : entities) {
			if(obj instanceof WorldEntity) {
				WorldEntity we = (WorldEntity)obj;
				if(we.getType()==WEType.OBSTACLE) return true;
			}
			else if(obj instanceof Agent) return true;
		}
		return false;
	}
	
	/**
	 * @return true if this square has an entity of type rubble, else false
	 */
	public boolean hasRubble(){
		ArrayList<Object> entities = new ArrayList<Object>(this.getEntities());
		for(Object obj : entities) {
			if(obj instanceof WorldEntity) {
				WorldEntity we = (WorldEntity)obj;
				if(we.getType()==WEType.RUBBLE) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if this square contains a victim, else false
	 */
	public boolean hasVictim(){
		ArrayList<Object> entities = new ArrayList<Object>(this.getEntities());
		for(Object obj : entities) {
			if(obj instanceof WorldEntity) {
				WorldEntity we = (WorldEntity)obj;
				if(we.getType()==WEType.VICTIM) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if this square contains an unchecked victim, else false
	 */
	public boolean hasNewVictim(){
		ArrayList<Object> entities = new ArrayList<Object>(this.getEntities());
		for(Object obj : entities) {
			if(obj instanceof WorldEntity) {
				WorldEntity we = (WorldEntity)obj;
				if(we.getType()==WEType.VICTIM&&we.isChecked()==false) return true;
			}
		}
		return false;
	}
	
	/**
	 * @return true if square explored, else false
	 */
	public boolean isExplored(){return is_explored;}
	
	/**
	 * Flags the square as explored
	 */
	public void setExplored(){is_explored=true;}
	
	/**
	 * String representation of the square
	 */
	public String toString(){
		String str = "Square"+this.pos.toString()+" containing: ";
		for(Object ent: this.entities_in) str=str.concat(ent.toString()+", ");
		return str;
	}
	
	/**
	 * Clears the square, restoring it to its initial state.
	 */
	public void reset(){
		this.is_explored=false;
		this.entities_in.clear();
		this.pos=null;
	}
}
