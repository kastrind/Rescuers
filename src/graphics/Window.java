package graphics;

import misc.MP3Player;
import environment.Position;
import environment.TheGrid;
import environment.WorldEntity;
import environment.WorldEntity.WEType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import misc.Metrics;
import agents.Ambulance;
import agents.Rescuer;

/**
 * Opens a window to contain the graphical representation of the world
 * @author Jim
 */
public class Window implements ActionListener{
	
	static int victim_life=0;
	static int width, height, size;
	private JFrame frame;
	private EnvGraphics env_g;
	private Color background_c;
	private JTextField height_field, width_field, size_field;
	private JSlider life_slider;
	JTextArea scores_area;
	private String images_path = "."+File.separator+"images"+File.separator;
	
	/**
	 * Initializes the window
	 */
	public void init() {
		frame = new JFrame("Rescuers v0.1");
	    frame.setLayout(new BorderLayout());
	    setupControls();
	    frame.setSize(width+100, height+150);
	    frame.setVisible(true);
	    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(images_path+"rescuer_a.png"));
	    frame.pack();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    try {
			background_c = new Color(ImageIO.read(new File(images_path+"rescuer_a.png")).getRGB(0, 0));
		} catch (IOException e) {
			System.err.println("IOException caught: failed to set background color.");
		}
	}
	
	/**
	 * Draws the configuration panel of the frame
	 */
	private void setupControls(){
		JPanel panel_ctrls = new JPanel();
		panel_ctrls.setLayout(new FlowLayout());
		//create the slider label
		JLabel life_label = new JLabel("Victims' life in minutes:");
		panel_ctrls.add(life_label);
		
		//create the slider
		life_slider = new JSlider();
		life_slider.setMinimum(0);
		life_slider.setMaximum(300);
		life_slider.setMajorTickSpacing(30);
		Hashtable<Integer, JLabel> slider_labels = new Hashtable<Integer, JLabel>();
		slider_labels.put(new Integer(0), new JLabel("0"));
		slider_labels.put(new Integer(30), new JLabel("0.5"));
		slider_labels.put(new Integer(60), new JLabel("1"));
		slider_labels.put(new Integer(90), new JLabel("1.5"));
		slider_labels.put(new Integer(120), new JLabel("2"));
		slider_labels.put(new Integer(150), new JLabel("2.5"));
		slider_labels.put(new Integer(180), new JLabel("3"));
		slider_labels.put(new Integer(210), new JLabel("3.5"));
	    slider_labels.put(new Integer(240), new JLabel("4"));
		slider_labels.put(new Integer(270), new JLabel("4.5"));
		slider_labels.put(new Integer(300), new JLabel("5"));
		life_slider.setLabelTable(slider_labels);
		life_slider.setPaintLabels(true);
		life_slider.setPaintTicks(true);
	    life_slider.addChangeListener(new LifeListener());
	    life_slider.setValue(30);
		panel_ctrls.add(life_slider);
		
		//create the text fields and their labels
		JLabel height_label = new JLabel("Height:");
		height_field = new JTextField("480",4);
		height=Integer.parseInt(height_field.getText());
		JLabel width_label = new JLabel("Width:");
		width_field = new JTextField("640",4);
		width=Integer.parseInt(width_field.getText());
		JLabel size_label = new JLabel("Grid size (>15):");
		size_field = new JTextField("16",3);
		size=Integer.parseInt(size_field.getText());
		panel_ctrls.add(height_label);
		panel_ctrls.add(height_field);
		panel_ctrls.add(width_label);
		panel_ctrls.add(width_field);
		panel_ctrls.add(size_label);
		panel_ctrls.add(size_field);
		
		//create the go button
		JButton go_button = new JButton("Go!");
		go_button.addActionListener(this);
		panel_ctrls.add(go_button);
		
		//create the "score board" area
		scores_area = new JTextArea();
		scores_area.setEditable(false);
		scores_area.setLineWrap(false);
		scores_area.setBackground(Color.LIGHT_GRAY);
		
		frame.add("South", scores_area);
		frame.add("North", panel_ctrls);
	  }
	
	  /** Tell system to use native look and feel, as in previous
	   *  releases. Metal (Java) LAF is the default otherwise.
	   */
	  public static void setNativeLookAndFeel() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch(Exception e) {
	      System.out.println("Error setting native LAF: " + e);
	    }
	  }

	public void actionPerformed(ActionEvent e) {
		//play music
        MP3Player mp3 = new MP3Player("."+File.separator+"sounds"+File.separator+"bensound-epic.mp3");
        mp3.play();
        
        victim_life*=1000;
		
		try {
			height = Integer.parseInt(height_field.getText());
			width = Integer.parseInt(width_field.getText());
			size = Integer.parseInt(size_field.getText());
			if(size<16) size=16;
		}
		catch(NumberFormatException nfe) {
			height=480;width=640;size=16;
		}
		
		//recreate the grid and the graphics
		TheGrid.createGrid(size);
		env_g = new EnvGraphics(width, height, size);
		env_g.setBackground(background_c);
		frame.add("Center", env_g);
		frame.setSize(width+100, height+150);
		frame.paintAll(frame.getGraphics());
		
		//a score metric for each team
		Metrics metr_a = new Metrics();
		Metrics metr_b = new Metrics();
		
		//initial time
		long t_start = System.currentTimeMillis();
		
		//populate the grid
		File victim_image = new File(images_path+"victim.png");
		File rubble_image = new File(images_path+"rubble0.png");
		File boulder_image = new File(images_path+"boulder.png");
		File fire_image = new File(images_path+"fire.png");
		TheGrid.getSquareAt(new Position(12,10)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(3,10)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(12,13)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(3,13)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(7,10)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,10)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,10)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(4,4)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(2,4)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(4,11)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(4,15)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(11,4)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(15,4)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(7,6)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,6)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(6,7)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(6,8)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,7)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,7)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		
		TheGrid.getSquareAt(new Position(15,3)).addEntity(new WorldEntity(WEType.VICTIM, env_g, victim_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(15,3)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		
		TheGrid.getSquareAt(new Position(7,7)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(6,6)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(5,5)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,8)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,9)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,10)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,5)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,6)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,7)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(7,8)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(6,9)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, boulder_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(5,10)).addEntity(new WorldEntity(WEType.OBSTACLE, env_g, fire_image, victim_life, metr_a, metr_b));
		
		TheGrid.getSquareAt(new Position(9,12)).addEntity(new WorldEntity(WEType.VICTIM, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,12)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,11)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,12)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,11)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(8,13)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(10,13)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,13)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		TheGrid.getSquareAt(new Position(9,11)).addEntity(new WorldEntity(WEType.RUBBLE, env_g, rubble_image, victim_life, metr_a, metr_b));
		
		File hospital_image = new File(images_path+"hospital.png");
		WorldEntity hospitalA = new WorldEntity(WEType.HOSPITAL, env_g, hospital_image, victim_life, metr_a, metr_b);
		TheGrid.getSquareAt(new Position(0,15)).addEntity(hospitalA);
		WorldEntity hospitalB = new WorldEntity(WEType.HOSPITAL, env_g, hospital_image, victim_life, metr_a, metr_b);
		TheGrid.getSquareAt(new Position(15,15)).addEntity(hospitalB);
		
		//set team A up
		String teamA = "teamRED";
		File ambulance_imageA = new File(images_path+"ambulance_a.png");
		File rescuer_imageA = new File(images_path+"rescuer_a.png");
		Ambulance a0 = new Ambulance(new Position(0,7), hospitalA.getPosition(), env_g, ambulance_imageA, metr_a, teamA, true);
		Rescuer r0 = new Rescuer(new Position(0,0), env_g, rescuer_imageA, a0, metr_a, teamA);
		Rescuer r1 = new Rescuer(new Position(0,1), env_g, rescuer_imageA, a0, metr_a, teamA);
		Rescuer r2 = new Rescuer(new Position(0,2), env_g, rescuer_imageA, a0, metr_a, teamA);
		Rescuer r3 = new Rescuer(new Position(0,4), env_g, rescuer_imageA, a0, metr_a, teamA);
		Rescuer r4 = new Rescuer(new Position(0,5), env_g, rescuer_imageA, a0, metr_a, teamA);
		Rescuer r5 = new Rescuer(new Position(0,6), env_g, rescuer_imageA, a0, metr_a, teamA);
		
		//set team B up
		String teamB = "teamBLUE";
		File ambulance_imageB = new File(images_path+"ambulance_b.png");
		File rescuer_imageB = new File(images_path+"rescuer_b.png");
		Ambulance a0b = new Ambulance(new Position(9,0), hospitalB.getPosition(), env_g, ambulance_imageB, metr_b, teamB, false);
		Rescuer r0b = new Rescuer(new Position(15,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		Rescuer r1b = new Rescuer(new Position(14,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		Rescuer r2b = new Rescuer(new Position(13,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		Rescuer r3b = new Rescuer(new Position(12,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		Rescuer r4b = new Rescuer(new Position(11,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		Rescuer r5b = new Rescuer(new Position(10,0), env_g, rescuer_imageB, a0b, metr_b, teamB);
		
		//start measuring time, disable the go button
		long t_end = System.currentTimeMillis();
		long duration = t_end-t_start;
		JButton go_button = (JButton)e.getSource();
		go_button.setEnabled(false);
		float teamAscore=0, teamBscore=0, sum=0;
		
		//wait until the life time of victims expires, +3 seconds
		while(duration<victim_life+3000) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {e1.printStackTrace();}
			//refresh duration
			t_end = System.currentTimeMillis();
			duration = t_end-t_start;
			//show scores
			teamAscore = metr_a.getScore();
			teamBscore = metr_b.getScore();
			sum=teamAscore+teamBscore;
			scores_area.setText("Team "+teamA+" score: "+teamAscore+", team "+teamB+" score: "+teamBscore+", sum: "+sum);
			scores_area.update(scores_area.getGraphics());
			//repeat song if end of play-back
			if(mp3.isComplete()){
				mp3.play();
			}
		}
		
		//kill the rest of the threads, reset the grid, enable button
		r0.death_flag=true;
		r1.death_flag=true;
		r2.death_flag=true;
		r3.death_flag=true;
		r4.death_flag=true;
		r5.death_flag=true;
		a0.death_flag=true;
		r0b.death_flag=true;
		r1b.death_flag=true;
		r2b.death_flag=true;
		r3b.death_flag=true;
		r4b.death_flag=true;
		r5b.death_flag=true;
		a0b.death_flag=true;
		TheGrid.reset();
		go_button.setEnabled(true);
		//stop sounds
		mp3.close();
		a0.stopSiren();
		a0b.stopSiren();
		Toolkit.getDefaultToolkit().beep();
		//assign default value to the slider
		life_slider.setValue(30);
	}
}
