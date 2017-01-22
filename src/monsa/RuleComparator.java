package monsa;

import java.util.ArrayList;
import java.util.Comparator;

public class RuleComparator implements Comparator<Rule>{

public static void markDuplicates(ArrayList<Rule> ruleSet){

	for (Rule r: ruleSet) {
		if(r.isDuplicate()) {
			continue;
		}
		for (Rule e : ruleSet) {
			if (!e.isDuplicate()) {
				r.compareTo(e);
			}
		}
	}
}


/* compares two rules by frequencies 
 * should you need natural ordering
 * use compareTo() method of Rule class
 */
public int compare(Rule first, Rule other){
	//compares two rules by coverage
	return first.compareCoverage(other);
}


}
