package monsa;

import java.util.Comparator;

public class WeigthComparator implements Comparator<Rule>{

	@Override
	public int compare(Rule o1, Rule o2) {
		
		float c1 = o1.getCoverageCoef();
		float c2 = o2.getCoverageCoef();
		
		// smaller coefficent implies a better rules
		if(c1 < c2) {
			return 1;
		} else if(c2 > c1) {
			return -1;
		}
		return 0;
	}

}
