package misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import environment.Position;

/**
 * Useful utilities
 * @author Jim
 */
public class Utils {

	/**
	 * Sorts integer values of a HashMap in descending order
	 * @param map
	 * @return ArrayList of the values
	 */
	public static ArrayList<Integer> sortValsDescending(HashMap<Position, Integer> map) {
		ArrayList<Integer> vals = new ArrayList<Integer>(map.values());
		Comparator<Integer> comparator = Collections.reverseOrder();
		Collections.sort(vals, comparator);
		return vals;
	}
	
	/**
	 * Sorts integer values of a HashMap in ascending order
	 * @param map
	 * @return ArrayList of the values
	 */
	public static ArrayList<Integer> sortValsAscending(HashMap<Position, Integer> map) {
		ArrayList<Integer> vals = new ArrayList<Integer>(map.values());
		Collections.sort(vals);
		return vals;
	}
	
	/**
	 * Arranges the keys of a HashMap according to the descending order of its values
	 * @param map
	 * @param mode ascending or descending
	 * @return ArrayList of the keys
	 */
	public static ArrayList<Position> arrangeKeys(HashMap<Position, Integer> map, String order) {
		ArrayList<Integer> sorted_vals;
		if(order.equals("descending")) {sorted_vals = sortValsDescending(map);}
		else if(order.equals("ascending")) {sorted_vals = sortValsAscending(map);}
		else {sorted_vals = sortValsAscending(map);}
		
		ArrayList<Position> keys = new ArrayList<Position>(map.keySet());
		Collections.sort(keys);
		ArrayList<Position> keys_new = new ArrayList<Position>();
		
		Iterator<Integer> vals_i = sorted_vals.iterator();
		Iterator<Position> keys_i;
		double curr_val = 0;
		double curr_val2 = 0;
		Position curr_key;
		
		while(vals_i.hasNext()){
			curr_val = vals_i.next();
			keys_i = keys.iterator();
			while (keys_i.hasNext()) {
				curr_key = keys_i.next();
				curr_val2 = map.get(curr_key);
				if(curr_val == curr_val2) {
					keys.remove(curr_key);
					keys_new.add(curr_key);
					break;
				}
			}
		}
	return keys_new;
	}
}
