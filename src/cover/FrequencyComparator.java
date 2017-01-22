package cover;

import java.util.ArrayList;
import java.util.Comparator;

import monsa.DataCache;
import monsa.Rule;
import monsa.inputdata.DataRow;

/*
 * Compares rules by frequency, ignoring given set of objects
 * */
public class FrequencyComparator implements Comparator<Rule> {

	private int[] ignored;
	
	FrequencyComparator(int[] ignoredRows){
		ignored = ignoredRows;
	}
	
	@Override
	// larger frequency is preferred
	public int compare(Rule o1, Rule o2) {
		int f1 = computeFrequency(o1);
		int f2 = computeFrequency(o2);
		
		if(f1 > f2)
			return -1;
		if(f2 > f1)
			return 1;
		
		return 0;
	}
	
	// returns list of removable Rules (0 unique objects)
	public ArrayList<Rule> removeZeroCount(ArrayList<Rule> rs){
		
		ArrayList<Rule> removable = new ArrayList<>();
		
		for(Rule r : rs){
			// if no unique data
			if(computeFrequency(r) < 1){
				// add to the list of removables
				removable.add(r);
			}
		}
		
		return removable;
	}
	
	// computes the frequency of data rows that are not in ignore list
	public int computeFrequency(Rule r) {

		DataCache d = r.getData();
		// reset iterator
		d.setIterator();
		
		DataRow dr;
		int count = 0;

		// check for each row, if it should be ignored
		while((dr = d.nextLine()) != null){
			// if this row is not to be ignored, then count
			if(ignored[dr.getRowNumber()] != 1){
				count++;
			}
		}
		return count;
	}
}
