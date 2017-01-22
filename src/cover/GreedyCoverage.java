package cover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import monsa.Rule;

public class GreedyCoverage extends Coverage {

	public GreedyCoverage(int freeFactorLimitCover) {
		super(freeFactorLimitCover);
	}
	
	public void getCoverage(List<Rule> ruleSet){
		
		// if empty rule set, do nothing (keeps usefulSet as it is)
		if (!validateRuleSet(ruleSet)) {
			return;
		}
		
		// make a copy to manipulate with
		// not required by the algorithm, but this preserves the original
		List<Rule> rSet = new ArrayList<>(ruleSet);
		
		// this is used to calculate useful frequencies and sort
		fc = new FrequencyComparator(usedRows);

		// sort Rules by coverage, largest coverage first
		Collections.sort(rSet, fc);
		
		optiTakeBest(rSet);
	}

	// rSet - sorted rule set
	// adds "best" rule to the selection of final rules
	private void optiTakeBest(List<Rule> rSet) {
		
		// add first rule in sorted rule set to set of useful rules
		// and do statistics
		addToSelection(rSet.get(0));

		// remove this top rule
		rSet.remove(0);
		
		//this code removes redundant rules from the dataset
		//removeRedunantRules(rSet);
		
		// sort Rules by coverage, largest coverage first
		// required to push a rule with largest unique coverage onto the top of the list
		Collections.sort(rSet, fc);
		
		// if largest remaining rule has unique objects then continue
		if(rSet.size() > 0 && fc.computeFrequency(rSet.get(0)) >= minCover){
			optiTakeBest(rSet);
		}
	}

	@Override
	public String getName() {
		return "Greedy";
	}

}
