package cover;

import java.util.ArrayList;

import monsa.DataRow;
import monsa.Rule;

/*
 * This filters out and adds to cover set all rules that uniquely cover at least 1 object
 * i.e there is no other rule that covers given object
 * then passes reduced rule set to Greedy 
 * */
public class UniqueGreedyCoverage extends GreedyCoverage {

	public UniqueGreedyCoverage(int freeFactorLimitCover) {
		super(freeFactorLimitCover);
	}
	
	// coverage based on frequency tables
	public void getCoverage(ArrayList<Rule> rSet){
		
		// if empty rule set, do nothing (keeps usefulSet empty)
		if (!validateRuleSet(rSet)) {
			return;
		}

		@SuppressWarnings("unchecked")
		ArrayList<Rule> ruleSet = (ArrayList<Rule>) rSet.clone();
		
		// vector of size dataRowCount to mark coverage for objects
		// each rule that covers, adds 1
		coverageFreq = new int[dataRowCount];

		DataRow dr;
		
		// iterate once though all the rules to find frequencies over the dataset
		for(Rule r : ruleSet){
			// iterate though all the datarows that this rule covers
			while((dr = r.getData().nextLine())!= null){
				// increment coverage count for this object	
				coverageFreq[dr.getRowNumber()]++;
			}
		}
		
		// get all rules that do not overlap with anyone else and remove them from ruleSet
		int pointer = 0;
		while (ruleSet.size() > pointer) {	
			pointer = extractNonOverlappingRules(ruleSet, pointer);
		}
		
		// pass to greedy
		super.getCoverage(ruleSet);
	}

	// get rules that cover only unique objects
	private int extractNonOverlappingRules(ArrayList<Rule> ruleSet, int pointer) {
		
		DataRow dr;
		Rule r = ruleSet.get(pointer);

		int is_unique = 0;
		
		while((dr = r.getData().nextLine())!= null){
			// if given rules covers an object that is only covered by 1 rule, this must be covering it
			if (coverageFreq[dr.getRowNumber()] == 1) {
				// at least minCover unique objects are covered by the rule, so no point of checking other objects
				is_unique++;
				if (is_unique >= minCover) {
					break;
				}
			}
		}

		if (is_unique >= minCover) {
			addToSelection(r);
			ruleSet.remove(pointer);
		} else {
			pointer++;
		}
		
		return pointer;
	}

	@Override
	public String getName() {
		return "Unique first + Greedy";
	}
}
