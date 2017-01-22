package cover;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import monsa.Rule;
import monsa.WeigthComparator;
import monsa.inputdata.DataRow;

public class ApproxCoverage extends Coverage {

	public ApproxCoverage(int freeFactorLimitCover) {
		super(freeFactorLimitCover);
	}
	
	public void getCoverage(List<Rule> rSet) {
		List<Rule> ruleSet = new ArrayList<>(rSet);
		HashMap<Integer, List<Rule>> ruleMapping = new HashMap<>();
		
		// this is used to calculate useful frequencies and sort
		fc = new FrequencyComparator(usedRows);
		
		coverageFreq = new int[dataRowCount];

		DataRow dr;

		// iterate once though all the rules to find frequencies over the dataset
		for(Rule r : ruleSet){
			// skip rules below required frequency
			if (r.getFrequency() < minCover) {
				continue;
			}
			// iterate though all the datarows that this rule covers
			while((dr = r.getData().nextLine())!= null){
				// increment coverage count for this object	
				coverageFreq[dr.getRowNumber()]++;
				
				// save reference to the rules associated with the row number
				if(!ruleMapping.containsKey(dr.getRowNumber())) {
					ruleMapping.put(dr.getRowNumber(), new ArrayList<Rule>());
				}
				ruleMapping.get(dr.getRowNumber()).add(r);
			}
		}

		// add weights to rules
		for (Rule r : ruleSet) {
			// skip rules below required frequency
			if (r.getFrequency() < minCover) {
				continue;
			}

			while((dr = r.getData().nextLine())!= null){
				// this object is not covered by any other rule
				// it is unavoidable to add such rule to the cover
				if (coverageFreq[dr.getRowNumber()] == 1) {
					addToSelection(r);
					break;
				}
				r.incrementCoverageCoefBase(coverageFreq[dr.getRowNumber()]);
			}
		}

		WeigthComparator ws = new WeigthComparator();

		// select best rule that covers each object (with smallest coefficient)
		for (int i=0; i < dataRowCount; i++){
				
			// if data row is already covered then we skip this object
			if (usedRows[i] == 1) {
				continue;
			}
			
			List<Rule> rmap = ruleMapping.get(i);
			
			// add best rule to the selection if not already in selection
			if (rmap != null && rmap.size() > 0) {
				Collections.sort(rmap, ws);
				if (minCover == 1 || fc.computeFrequency(rmap.get(0)) >= minCover){
					addToSelection(rmap.get(0));	
				}
			}
		}
	}

	@Override
	public String getName() {
		return "Approximation";
	}
}
