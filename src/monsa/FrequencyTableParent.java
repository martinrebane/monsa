package monsa;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import monsa.inputdata.DataRow;

/*
 * Parent frequency table, stores information about children
 and class column number
 */
public class FrequencyTableParent extends FrequencyTable {

	// sub frequency-tables
	// integer represents value of class column variable
	// value_of_class_variable => frequencies
	protected HashMap<Integer, FrequencyTableChild> children = new HashMap<Integer, FrequencyTableChild>();

	// links _colNames in header to int values
	//_allColNames stores all columns names for reference in toString()
	private HashMap<Integer, String> _colNames, _allColNames;

	//stack for extract conditions
	private ArrayDeque<VarValFrequency> rowSelectors = new ArrayDeque<VarValFrequency>();

	// number of CLASS column, starts from 0
	// internal usage only
	// NB! constructor accepts numbers from 1
	private Integer _classColNumber;

	private DataCache data;

	// accept class column number as an argument
	// this is a number of CLASS variable in a dataset
	// first variable in a row no 1, not 0
	//
	// internal storage of class col number starts from 0
	// userCells - cells that are not to be considered as rule
	// has been already extracted
	public FrequencyTableParent(int classColNumber,
			HashMap<Integer, List<Integer>> _usedCells) {

		// internal storage of class col number starts from 0
		_classColNumber = classColNumber - 1;
		usedCells = _usedCells;

	}

	//accepts header as unparsed string
	public void parseHeader(String header, String _separator){
		parseHeaderArray(header.split(_separator));
	}
	
	//accepts header as String array
	//stores information from the header (column _colNames)
	@SuppressWarnings("unchecked")
	public void parseHeaderArray(String[] h) {

		_colNames = new HashMap<Integer, String>(h.length);

		// initialize table
		_table = new HashMap<Integer, HashMap<Integer, AtomicInteger>>(h.length);

		// store _colNames and create empty structure for _table
		for (int i = 0; i < h.length; i++) {
			_colNames.put(i, h[i]);
			_table.put(i, new HashMap<Integer, AtomicInteger>());
		}
		
		_allColNames = (HashMap<Integer, String>) _colNames.clone();
		
		//allow only non-class and non-extract columns to be indexed
		//remove class var
		_colNames.remove(_classColNumber);
		
		//remove columns that are used as an extract criterion
		Iterator<VarValFrequency> it = rowSelectors.iterator();
		
		while(it.hasNext()){
			VarValFrequency e = it.next();
			//remove column which was used to make an extract
			if(_colNames.containsKey(e.getVar())){
				_colNames.remove(e.getVar());
				continue;
			}
		}
		
		data = new DataCache(h);
	}
	
	public void addLines(DataCache d) throws Exception{
		
		//add data line by line
		DataRow line;
		while((line = d.nextLine()) != null){
			addLine(line);
		}
	}

	// accepts a new line from a dataset and adds it's frequencies
	// to the table
	public void addLine(DataRow dataRow) {

		int[] h = dataRow.getData();
		
		//kontrollime, kas on t2idetud extracti tingimused
		int classValue = h[_classColNumber];
		
		//check if extract condition are met
		//this allows or removes WHOLE ROW of data
		if(!rowSelectors.isEmpty()){
			Iterator<VarValFrequency> i = rowSelectors.iterator();
			while(i.hasNext()){
				VarValFrequency vvf = i.next();
				//extract column value does not match, do not count this row
				if(h[vvf.getVar()] != vvf.getVal()){
					return;
				}
			}
		}
		
		//add to data extract
		data.addRow(dataRow);
		
		// store values. add only those columns which are in the colnames
		// ignore others
		for (Integer colNum : _colNames.keySet()) {
			
			// value of the cell
			Integer colValue = h[colNum];
			
			// adds value to frequency table
			// i'th column, value colValue
			addValue(colNum, colValue);

			// now we are dealing with CLASS VARIABLE
			// and store frequency for given value in the
			// child frequency table. This code creates unique fq table
			// for each unique value of class variable. e.g if class variable
			// has 3 different values then it creates 3 different fq tables
			if (!children.containsKey(classValue)) {
				children.put(classValue, new FrequencyTableChild(_colNames,	usedCells));
			}

			children.get(classValue).addValue(colNum, colValue);
		}
	}

	@Override
	public String toString() {
		String str = super.toString();

		for (Iterator<Entry<Integer, FrequencyTableChild>> iterator = children
				.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, FrequencyTableChild> e = iterator.next();
			str += "\n\rSubtable for " + e.getKey();
			str += "\n\r";
			str += e.getValue().toString();
		}

		return str;
	}

	// maps variable number to it's original name
	public String getVarName(int varNo) {
		return _allColNames.get(varNo);
	}

	// return class variable number
	public int getClassVarNo() {
		return _classColNumber;
	}
	
	// returns treeset with remaining frequencies
	// does not count cells that are marked in usedCells (cells where rules have been found)
	public TreeSet<VarValFrequency> getRemainingFrequenciesInOrder(
		HashMap<Integer, List<Integer>> usedCells,TreeSet<VarValFrequency> retArr) {

		//Integer minValue = null;

		for (Entry<Integer, HashMap<Integer, AtomicInteger>> e : _table
				.entrySet()) {

			// if usedCells has restrictions on that variable, extract those
			for (Entry<Integer, AtomicInteger> e2 : e.getValue().entrySet()) {
				// if that value is not banned for given variable
					if (!keyBanned(usedCells, e.getKey(), e2.getKey()))
					{
							// add to the list of all known remaining pairs
							retArr.add(new VarValFrequency(e.getKey(), e2.getKey(), e2.getValue().intValue()));
					}
			}
		}
		
		return retArr;
	}

	//if key is banned from extract
	//returns true if it is, false if not
	private boolean keyBanned(HashMap<Integer, List<Integer>> usedCells,
			int variableNo,	int varValue) {
		//variable is not in banned list at all
		if(!usedCells.containsKey(variableNo)){
			return false;
		}
		//variable is in banned list, check given value
		if(!usedCells.get(variableNo).contains(varValue)){
			return false;
		}
		
		return true;
	}
	
	public ArrayDeque<VarValFrequency> getExtractConditionStack(){
		return rowSelectors;
	}
	
	public void setExtractConditionStack(ArrayDeque<VarValFrequency> stack) {
		if(!stack.isEmpty()){
			rowSelectors = stack.clone();
		}
	}
	
	//adds extract condition to the stack
	public void addExtractCondition(VarValFrequency smallestFactor){
		rowSelectors.add(smallestFactor);
	}
	
	//returns the frequency of given variable-value combination
	public int varValFrequency(int key, int val){
		return _table.get(key).get(val).intValue();
	}
	
	public String extractFactorsToString(){
		
		Iterator<VarValFrequency> it = rowSelectors.iterator();
		StringBuilder s = new StringBuilder();
		
		while(it.hasNext()){
			VarValFrequency e = it.next();
			s.append((e.getVar()+1) + "." + e.getVal() + " & ");
		}
		
		return s.toString();
	}
	
	public DataCache getData(){
		return data;
	}
	
	public HashMap<Integer,HashMap<Integer,AtomicInteger>> getTable(){
		return _table;
	}
	
	// return the number of data rows that were used to build this frequency table
	public int getDataSize() {
		return data.getDataSize();
	}
}
