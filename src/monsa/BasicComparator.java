package monsa;

import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicComparator implements Comparator<Integer>{

	HashMap<Integer,AtomicInteger> map;
	
	public BasicComparator(HashMap<Integer,AtomicInteger> coverageFreqMap){
		this.map = coverageFreqMap;
	}
	
	@Override
	public int compare(Integer o1, Integer o2) {
		AtomicInteger val1 = map.get(o1);
		AtomicInteger val2 = map.get(o2);
		
		if (val1.get() < val2.get()) {
			return -1;
		}
		else if (val2.get() < val1.get()){
			return 1;
		}
		// order by keys for equal values
		else if (o1 < o2) {
			return -1;
		}
		return 1;	
	}	
}
