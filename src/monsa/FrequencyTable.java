package monsa;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

abstract class FrequencyTable {
	
	//frequency table: COLNUMBER => SimpleEntry<VALUE, FREQUENCY_OF_VALUE>
	protected Map<Integer,Map<Integer,AtomicInteger>> _table;
	
	protected Map<Integer, List<Integer>> usedCells;
	
	private int cellCount = 0;
	
	public FrequencyTable(){}

	//adds value of one column to frequency table
	//updates fq table if colNum exists
	public void addValue(int colNum, Integer colValue) {
		
		//do not add the value if rule for this column has already
		//been found
		if(usedCells.containsKey(colNum)){
			if(usedCells.get(colNum).contains(colValue)){
				return;
			}
		}
		
		//add new entry
		if(!_table.get(colNum).containsKey(colValue)){
			cellCount++;
			_table.get(colNum).put(colValue, new AtomicInteger(1));
		}
		//update existing entry, increase frequency by one
		else{
			_table.get(colNum).get(colValue).getAndIncrement();
		}
	}
	
	public String toString(){
		return _table.toString();
	}

	// returns number of "cells" (var-value pairs) in this table
	public int getCellCount() {
		return cellCount;
	}
	
	// number of different variables in frequency table
	public int getVariableCount() {
		if(_table != null){
			int retval = 0;
			Iterator<Entry<Integer, Map<Integer, AtomicInteger>>> it = _table.entrySet().iterator();
			while(it.hasNext()){
				Entry<Integer, Map<Integer, AtomicInteger>> e = it.next();
				if(!e.getValue().isEmpty())
					retval ++;
			}
			return retval;
		}
		return 0;
	}
}
