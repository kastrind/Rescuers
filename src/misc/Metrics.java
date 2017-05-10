package misc;

/**
 * For measuring the current score
 * @author Jim
 *
 */
public class Metrics {
	
	private int victims_num;
	private int victims_rescued;
	
	public Metrics(){
		victims_num=0;
		victims_rescued=0;
	}
	
	/**
	 * Increment the number of victims by 1
	 */
	public void victimAdded() {victims_num++;}
	
	/**
	 * Increment the number of rescued victims by 1
	 */
	public void victimRescued(){victims_rescued++;}
	
	/**
	 * @return the number of rescued victims divided by total victims
	 */
	public float getScore(){return (float)victims_rescued/victims_num;}

}
