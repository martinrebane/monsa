package cover;

import java.util.ArrayList;
import java.util.List;

import monsa.DataCache;
import monsa.DataRow;
import monsa.Rule;

public abstract class Coverage {
	
// set of rules that cover some unique objects
protected List<Rule> usefulSet = new ArrayList<>();
protected int[] usedRows;
protected FrequencyComparator fc;

protected int usedRowsCount = 0;
protected int dataRowCount;

// vector to store how many times each object is covered by rules
protected int[] coverageFreq;

// only select rules that cover at least that many unique objects
// at the time rule is added to the dataset
protected int minCover;

public Coverage(int freeFactorLimitCover) {
	this.minCover = freeFactorLimitCover;
}

public abstract void getCoverage(List<Rule> rSet);

public abstract String getName();

// sets number of data objects
public void setDataRowCount(int dataRowCount) {
	this.dataRowCount = dataRowCount;
	// mark used data rows here, initially all 0
	usedRows = new int[dataRowCount];
}

protected void addToSelection(Rule r) {

	// add rule to final selection
	usefulSet.add(r);
	
	// get rule data
	DataCache d = r.getData();
	
	// get new iterator
	d.setIterator();
	
	DataRow dr;
	while((dr = d.nextLine()) != null){
		// we count only unique rows, so mark only once
		if(usedRows[dr.getRowNumber()] == 0){
			// mark row as used
			usedRows[dr.getRowNumber()] = 1;
			// increment used rows count
			usedRowsCount++;
		}
	}
}

public List<Rule> getCoverageSet() {
	return usefulSet;
}

// returns number of objects that are covered by the rule set
public int getObjectCount(){
	return usedRowsCount;
}

// computes whether algorithm should continue with the ruleset or abort operations
protected boolean validateRuleSet(List<Rule> rSet) {
	if (rSet.size() < 1) {
		return false;
	}
	return true;
}

// not in use:


@SuppressWarnings("unused")
private void removeRedunantRules(ArrayList<Rule> rSet) {
	int b = rSet.size();
	//get list of removable rules
	ArrayList<Rule> removables = fc.removeZeroCount(rSet);
	for(Rule r2 : removables){
		//remove redundant rule from rSet
		rSet.remove(r2);
	}
	
	int a = rSet.size();
	
	if(a < b){
		System.out.println("Midagi eemaldati: " + a + "/" + b);
	}
}


// return 
public int getMinCover() {
	return minCover;
}

}
