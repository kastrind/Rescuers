package graphics;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LifeListener implements ChangeListener{

	public void stateChanged(ChangeEvent e) {
	    JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        Window.victim_life = (int)source.getValue();
	        System.out.println("Victim life set up for: "+Window.victim_life+" seconds.");
	    }
	}

}
