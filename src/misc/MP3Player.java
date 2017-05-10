package misc;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javazoom.jl.player.Player;

/**
 * A simple MP3Player
 * @author Nick
 *
 */
public class MP3Player {

	private String filename;
    private Player player; 

    /**
     * Takes a given file
     * @param filename the given file
     */
    public MP3Player(String filename) {
        this.filename = filename;
    }

    /**
     * Closes the player
     */
    public void close() { if (player != null) player.close(); }

    /**
     * Plays the MP3 file
     */
    public void play() {
        try {
            FileInputStream fis     = new FileInputStream(filename);
            BufferedInputStream bis = new BufferedInputStream(fis);
            player = new Player(bis);
        }
        catch (Exception e) {
            System.err.println("Problem playing file " + filename);
            System.err.println(e.getMessage());
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { player.play(); }
                catch (Exception e) { System.err.println(e.getMessage()); }
            }
        }.start();

    }
    
    /**
     * Checks whether play back has finished
     * @return true if play back has finished, else false
     */
    public boolean isComplete(){return this.player.isComplete();}
    
}
