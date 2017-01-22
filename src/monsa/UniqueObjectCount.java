package monsa;

import java.util.List;

import monsa.inputdata.DataRow;

/*
 * computes how many unique objects (data rows) a ruleset covers
 * 
 * */

public class UniqueObjectCount {

int[] usedRows;

// how many rows are covered
private int usedRowsCount;	

public UniqueObjectCount(List<Rule> ruleSet, int dataRowCount) {
	
	// empty integer array for marking objects that appear in rules
	usedRows = new int[dataRowCount];
	
	int i = 0;
	while (i < ruleSet.size()) {
		// get data rows that are covered by this rule
		DataCache d = ruleSet.get(i).getData();
		
		// get new iterator for data
		d.setIterator();
		
		DataRow dr;
		// drawback - this scans all the rows many-many times
		while((dr = d.nextLine()) != null){
			// we count only unique rows, so mark only once
			if(usedRows[dr.getRowNumber()] == 0){
				// mark row as used
				usedRows[dr.getRowNumber()] = 1;
				// increment used rows count
				usedRowsCount++;
			}
		}
		
		i++;
	}
}

public int getUsedRowsCount() {
	return usedRowsCount;
}

}
