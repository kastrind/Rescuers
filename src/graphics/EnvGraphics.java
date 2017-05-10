package graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;
import environment.Position;

/**
 * The graphical representation of the world
 * @author Jim
 */
public class EnvGraphics extends JPanel {
	
	private static final long serialVersionUID = 1L;
	public int square_width, square_height;
	private static boolean on_going=false;
	private ArrayList<GraphicEntity> graphic_entities;
	
	/**
	 * Creates a window of given width and height
	 * @param width in pixels
	 * @param height in pixels
	 * @param num_of_squares squares per grid side
	 */
	public EnvGraphics(int width, int height, int num_of_squares) {
		this.square_width = width/num_of_squares;
		this.square_height = height/num_of_squares;
		graphic_entities = new ArrayList<GraphicEntity>();
	}
	
	/**
	 * Adds a new entity to be drawn in graphics
	 * @param pos the position of the entity
	 * @param image the image of the entity
	 */
	public void addEntity(Position pos, BufferedImage image){
		synchronized(graphic_entities) {graphic_entities.add(new GraphicEntity(pos, image));}
	}
	
	/**
	 * Finds an entity with the given position and image and removes
	 * it so that it will not be drawn in graphics anymore
	 * @param pos the position of the entity
	 * @param image the image of the entity
	 */
	public void removeEntity(Position pos, BufferedImage image) {
		synchronized(graphic_entities) {
			GraphicEntity ge_tmp = new GraphicEntity(pos, image);
			for(GraphicEntity ge: graphic_entities) {
				if(ge.equals(ge_tmp)) {
					graphic_entities.remove(ge);
					break;
				}
			}
		}
	}
	
	public void update(Graphics g) {
		if(!on_going) {
			on_going=true;
			paint(g);
			on_going=false;
		}
	}
	
	public void paint(Graphics g) {
		clear(g);
		synchronized(graphic_entities) {
			try {
				for(GraphicEntity ge: graphic_entities) {g.drawImage(ge.image, ge.pos.getX()*square_width, ge.pos.getY()*square_height, null);}
				}catch (NullPointerException e) {}
		}
	}
	
	protected void clear(Graphics g) {super.paintComponent(g);}
	
	/**
	 * Auxiliary object for drawing each entity in graphics
	 * @author Jim
	 */
	public class GraphicEntity {
		private Position pos;
		private BufferedImage image;
		
		public GraphicEntity(Position pos, BufferedImage image){
			this.pos=pos;
			this.image=image;
		}
		
		public boolean equals(GraphicEntity ge) {
			return (this.pos==ge.pos&&this.image==ge.image);
		}
	}
}
