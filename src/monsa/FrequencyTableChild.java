package monsa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FrequencyTableChild extends FrequencyTable{
	public FrequencyTableChild(HashMap<Integer,String> _colNames, HashMap<Integer,List<Integer>> _usedCells){
		// initialize table
		_table = new HashMap<Integer, HashMap<Integer, AtomicInteger>>(_colNames.size());
		usedCells = _usedCells;
		
		// create empty structure, hashmap for each variable
		Iterator<Integer> i = _colNames.keySet().iterator();
		
		while(i.hasNext()) {
			Integer e = i.next();
			_table.put(e, new HashMap<Integer, AtomicInteger>());
		}
	}
	
	// returns the frequency for given variable and given value of that variable
	public int getFrequency(int variableNo, int varValue){
		
		//table for given variable
		if(_table.get(variableNo).containsKey(varValue)){
			return _table.get(variableNo).get(varValue).intValue();
		}
		return 0;
	}
}
