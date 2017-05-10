package environment;

import graphics.EnvGraphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import misc.Metrics;

/**
 * An entity of the world as a thread
 * @author Jim
 */
public class WorldEntity implements Runnable{
	
	//4 possible types, either an obstacle or removable rubble or a hospital or a victim
	public enum WEType{OBSTACLE, RUBBLE, HOSPITAL, VICTIM}
	
	private WEType type;
	private Position pos;
	private BufferedImage entity_sprite;
	private String images_path;
	private Thread thr;
	protected static final int SLEEP_TIME=500;
	private long life;
	private long birth_time;
	private boolean is_checked;
	private boolean is_safe;
	public boolean is_dead;
	private EnvGraphics env_g;
	private int crash_attempts=0;
	
	/**
	 * Creates an entity of some type
	 * @param type the type of the entity
	 * @param env_g the graphics environment where this entity will be represented
	 * @param image the picture/sprite of the entity
	 * @param life the remaining life this entity has (any value if this entity is inanimate)
	 * @param metr_a inform the score metric for team A that a new victim has been created (if this is a victim) 
	 * @param metr_b inform the score metric for team B that a new victim has been created (if this is a victim)
	 */
	public WorldEntity(WEType type, EnvGraphics env_g, File image, long life, Metrics metr_a, Metrics metr_b){
		this.type = type;
		this.env_g = env_g;
		try {
			 entity_sprite = ImageIO.read(image);
		}catch (IOException e) {System.err.println("IOException caught: failed to set sprite for WorldEntity "+this.toString());}
		images_path = image.getParent()+File.separator;
		this.life = life;
		thr = new Thread(this);
		is_checked=false;
		is_safe=false;
		is_dead=false;
		if(type==WEType.VICTIM){metr_a.victimAdded();metr_b.victimAdded();}
		birth_time = System.currentTimeMillis();
		thr.start();
	}
	
	/**
	 * If this entity is a victim, decrease its remaining life
	 * and kill it when there is no life remaining or when it's safe.
	 * If this entity is an obstacle or hospital, don't do anything.
	 * If this entity is rubble, remove it after 4 crash attempts.
	 */
	public synchronized void run(){
		env_g.addEntity(pos, entity_sprite);
		if(this.type==WEType.OBSTACLE||this.type==WEType.HOSPITAL) {return;}
		else if(this.type==WEType.RUBBLE) {
			while(crash_attempts<4) {sleep(SLEEP_TIME/2);continue;}
			TheGrid.getSquareAt(pos).removeEntity(this);
			env_g.removeEntity(getPosition(), getSprite());
			return;
		}
		else if(this.type==WEType.VICTIM) {
			long age=System.currentTimeMillis()-birth_time;;
			//while still alive
			while(life>age) {
				age = System.currentTimeMillis()-birth_time;
				sleep(2*SLEEP_TIME);
				if (is_safe) return;
			}
		}
		//die
		is_dead=true;
		TheGrid.getSquareAt(pos).removeEntity(this);
		if(!is_safe){setSprite(new File(images_path+"victim_dead.png"));}
	}
	
	/**
	 * Sleep for a given time
	 * @param millis the given time in milliseconds
	 */
	public void sleep(long millis){
		try {Thread.sleep(millis);}
		catch (InterruptedException e) {e.printStackTrace();}
	}
	
	/**
	 * @return the type of the entity
	 */
	public WEType getType(){return this.type;}
	
	/**
	 * @return the sprite of the entity
	 */
	public BufferedImage getSprite() {return this.entity_sprite;}
	
	/**
	 * assigns a new sprite to the entity
	 * @param image
	 */
	public void setSprite(File image) {
		//remove it from graphics
		env_g.removeEntity(pos, entity_sprite);
		try {
			 entity_sprite = ImageIO.read(image);
		}catch (IOException e) {
			System.err.println("IOException caught: failed to set sprite for WorldEntity "+this.toString());
		}
		//add it with its new sprite
		env_g.addEntity(pos, entity_sprite);
		//refresh
		env_g.update(env_g.getGraphics());
	}
	
	/**
	 * 
	 * @return the position of the entity
	 */
	public Position getPosition(){return this.pos;}
	
	/**
	 * Sets the entity at the given position
	 * @param pos the given position
	 */
	public void setPosition(Position pos){
		this.pos = pos;
	}
	
	/**
	 * @return true if entity is checked, else false
	 */
	public boolean isChecked(){return is_checked;}
	
	/**
	 *Flags the entity as checked
	 */
	public void setChecked(){is_checked=true;}
	
	/**
	 *Flags the entity as safe;
	 */
	public void setSafe(){is_safe=true;}
	
	/**
	 * Increases the crash attempts by 1
	 */
	public void crashRubble(){crash_attempts++;}
	
	/**
	 * @return the attempted crashes so far
	 */
	public int getCrashes(){return crash_attempts;}
	
	/**
	 * String representation of the entity
	 */
	public String toString(){return type.toString();}
}
