package monsa;
/*
 * MONSA family of algorithms
 * (C) Martin Rebane 2014
 * 
 * */

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import cover.*;

public abstract class Monsa {

	private String filename;
	private int classColNumber;
	
	private int levelRuleCount;
	
	//recursion depth of the algorithm
	private int algoDepth = 0;
	
	private String _separator = ",";
	
	private FileCachedReader fcr;
	
	private ArrayList<Rule> rawRuleSet = new ArrayList<>();
	private ArrayList<Rule> dsrRuleSet = new ArrayList<>();
	private ArrayList<Rule> coverageRuleSet = new ArrayList<>();
	
	// set that is passed to coverage algorithm
	private ArrayList<Rule> passToCoverageRuleSet = dsrRuleSet;
	
	private int dataRowCount, coverageObjectCount = 0;
	
	private long algoStartTime, algoRuleTime, algoDSRTime, algoCoverageTime;
	
	private int extractCount = 0;
	
	// algorithm finds rules that cover at least that many objects 
	private int freeFactorLimitAlgo = 1;
	
	// cover algorithm selects rules that cover at least that many unique objects that would be left uncovered otherwise
	private int freeFactorLimitCover = 1;
	
	// coverage solver
	Coverage covered_set;
	
	//constructor which outputs to syso by default
	public Monsa(String filename, int classColNumber) throws Exception{
		this.filename = filename;
		this.classColNumber = classColNumber;
	}
	
	// set minimum allowed object frequency for the rules found by algorithm
	public void setFreqLimit(int i) {
		freeFactorLimitAlgo = i;
	}
	
	public void runAlgo() throws Exception {
				
		algoStartTime = System.nanoTime();

		// Initialize frequency table builder
		FrequencyTableParent fq = new FrequencyTableParent(classColNumber, getUsedCells(0, null));

		/*read in the file and record the number of data rows */
		dataRowCount = parseFile(fq);

		//locations of found rules: varNo -> value; this is not an output, it just marks used cells
		//in frequency matrix. this is created only on upper level and passed down separately
		LinkedList<BannedCell> usedCells = new LinkedList<BannedCell>();
		
		//compare frequencies and extract rules based on zero-frequencies
		//on upper level (exceptional because uses the whole dataset)
		//will cause recursion
		findZeroRules(fq, null, algoDepth, usedCells);
		
		// rules are found!
		algoRuleTime = System.nanoTime();
		
		//comparator marks all duplicate rules, sort first
		//for better performance
		Collections.sort(rawRuleSet);
		RuleComparator.markDuplicates(rawRuleSet);
		
		//separate all non-duplicates
		for(Rule r: rawRuleSet){
			if(!r.isDuplicate()){
				r.getFrequency();
				dsrRuleSet.add(r);
			}
		}
		
		algoDSRTime = System.nanoTime();
		
		// compute object coverage, use Approximation by default
		if (covered_set == null) {
			covered_set = new ApproxCoverage(freeFactorLimitCover);
		}

		// set the size of dataset
		covered_set.setDataRowCount(dataRowCount);
		
		covered_set.getCoverage(passToCoverageRuleSet);
		
		//set of rules with unique coverage
		coverageRuleSet = covered_set.getCoverageSet();
		coverageObjectCount = covered_set.getObjectCount();
		
		algoCoverageTime = System.nanoTime();
		
		//sort unique coverage ruleset
		Collections.sort(coverageRuleSet);
		
		//sort DSR ruleset
		Collections.sort(rawRuleSet);
	}
	
	// instead of passing DSR set to coverage algorithm, pass RAW set
	public void passRawToCoverage() {
		passToCoverageRuleSet = rawRuleSet;
	}
	
	// sets coverage algorithm object
	public void setCoverage(Coverage cov) {
		covered_set = cov;
	}

	// outputs information and results
	public void printData(OutputAdapter oa, boolean printRaw, boolean printDsr, boolean printCompressed) {
		
		//print out method name
		oa.outputLine("Rules found (" + getMethodName() + "):");
		oa.outputLine(dataRowCount + " rows in dataset.");
		oa.outputLine("Filename: " + filename);
		
		int threshold = 1; // change to set output threshold
		
		// which rules to output
		if(printRaw) {
			oa.outputLine("RAW rules:");
			printRuleset(rawRuleSet, oa, threshold);
		}
		if(printDsr){
			oa.outputLine("DSR rules:");
			printRuleset(dsrRuleSet, oa, threshold);
		}
		if(printCompressed) {
			oa.outputLine("Coverage rules:");
			printRuleset(coverageRuleSet, oa, threshold);
		}
		
		// statistics
		
		// this object computes how many objects are covered (counts every object once even if covered multiple times)
		UniqueObjectCount uoc = new UniqueObjectCount(dsrRuleSet, dataRowCount);
		
		oa.outputLine("Number of rules:");
		oa.outputLine(rawRuleSet.size() +" uncompressed rules");
		oa.outputLine(dsrRuleSet.size() + " DSR rules");
		oa.outputLine(coverageRuleSet.size() + " optimized coverage rules\n");
		oa.outputLine("Object coverage:");
		oa.outputLine(uoc.getUsedRowsCount() + " with DSR");
		oa.outputLine(coverageObjectCount + " optimized coverage"); 
		oa.outputLine("Objects might be covered multiple times (counted once)\n");
		
		// no of extracts
		oa.outputLine("Total no of extracts: " + extractCount + " extracts");
		oa.outputLine("Frequency limit: " + freeFactorLimitAlgo + " (algorithm); " + covered_set.getMinCover() + " (cover)");
		oa.outputLine("Algorithm: " + getMethodName() + "; Cover algorithm: " + covered_set.getName() + "\n");
		
		//execution time
		oa.outputLine("Algorithm time: " + getAlgoTime() + " sec");
		oa.outputLine("DSR in: " + getDSRTime() + " sec");
		oa.outputLine("Coverage in: " + getCoverTime() + " sec");
		oa.outputLine("Total time: " + getTotalTime() + " sec");
		
		
		//close output adapter / allow it to finalize (flush, write etc)
		oa.close();
	}

	public double getTotalTime() {
		return (algoCoverageTime-algoStartTime)/1_000_000_000.0;
	}

	public double getCoverTime() {
		return (algoCoverageTime-algoDSRTime)/1_000_000_000.0;
	}

	public double getDSRTime() {
		return (algoDSRTime-algoRuleTime)/1_000_000_000.0;
	}

	public double getAlgoTime() {
		return (algoRuleTime-algoStartTime)/1_000_000_000.0;
	}

	private void printRuleset(ArrayList<Rule> ruleSet, OutputAdapter oa, int threshold) {
		int count = 0;
		int c = 0;
		for(Rule r : ruleSet){
			if(r.getFrequency() >= threshold){
				oa.outputLine(r.toString());
				count += r.getFrequency();
				c++;
			}
		}

		oa.outputLine("Over-coverage object count: " + count + "; each object averagely covered " + count*1.00/dataRowCount + " times");
		if (threshold > 1) {
			oa.outputLine("Rules over threshold: " + Integer.toString(c));
		}
	}

	//reads in the data and returns data row count, not counting header row
	private int parseFile(FrequencyTableParent fq) throws Exception {
		
		if(fcr == null){
			fcr = new FileCachedReader(filename);
		}
		
		//add header to frequency table
		fq.parseHeader(fcr.getHeader(), _separator);
		
		//add data line by line
		String line;
		int line_num = 0;
		
		while((line = fcr.nextLine()) != null){
			fq.addLine(new DataRow(line_num, line, _separator));
			line_num++;
		}
		
		return line_num;
	}
	
	private void parseData(FrequencyTableParent fq, DataCache d) throws Exception {
		
		//add header to frequency table
		fq.parseHeaderArray(d.getHeader());
		fq.addLines(d);
	}
	
	// finds complete rules in a given extract
	// parent frequency table is passed to compare backwards
	private void findZeroRules(FrequencyTableParent fq, FrequencyTableParent parentfq, int depth, 
			LinkedList<BannedCell> usedCells) throws Exception {
		
		// set rule count for this level
		levelRuleCount = 0;
		
		// total no of extracts in one run
		extractCount++;
		
		// iterator of main frequency table
		Iterator<Entry<Integer, HashMap<Integer, AtomicInteger>>> it = fq._table.entrySet().iterator();
		
		// iterate through each variable in the dataset
		// to see if frequencies match for some value in class frequency table
		while(it.hasNext()){
			Entry<Integer, HashMap<Integer, AtomicInteger>> e = it.next();
			
			// variable number (0 - n-1)
			int varNo = e.getKey();
			
			// ignore class variable
			if(varNo == fq.getClassVarNo()) continue;
			
			// iterate through each value, check match, find rules
			// e.getValue() contains all the value frequency tables for all the classes
			iterateValues(varNo, e.getValue(), fq, parentfq, depth, usedCells);
		}
		
		// choose next extract if there is more than 1 free factor
		if(fq.getDataSize() >= freeFactorLimitAlgo && fq.getCellCount()-levelRuleCount > 1 && fq.getVariableCount() > 1){
			// get an ordered list of all remaining factors, sorted by minimal/maximal frequency
			// VarValFrequency is an int[] array where int[0] - variable number; int[1] - value
			TreeSet<VarValFrequency> ts = createEmptyFrequencySet();
			ts = fq.getRemainingFrequenciesInOrder(getUsedCells(depth, usedCells), ts);
			
			// remove last as single remaining factor cannot give any rules
			ts.pollLast();
			
			//System.out.println("Number of free factors: " + ts.size() + ";" + (fq.getCellCount()-levelRuleCount));
			
			//MAKE new EXTRACT
			VarValFrequency extractFactor = null;
			
			Iterator<VarValFrequency> iter = ts.iterator();
			
			while(iter.hasNext()){
				extractFactor = iter.next();
				
				// skip this extract if it contains less data than required by the business parameter
				if(extractFactor.getFreq() < freeFactorLimitAlgo){
					continue;
				}
				FrequencyTableParent fq2 = new FrequencyTableParent(classColNumber, getUsedCells(depth, usedCells));
				
				// carry over parent extract conditions
				fq2.setExtractConditionStack(fq.getExtractConditionStack());
				
				// set extract condition
				fq2.addExtractCondition(extractFactor);
				
				parseData(fq2, fq.getData());
				// check variable count. at top level it might include several
				// if 0, continue with next factor
				if(fq2.getVariableCount() == 0){
					continue;
				}
				
				// passing shallow copy of usedCells to keep usedCells only
				// within given path
				findZeroRules(fq2, fq, (depth+1), usedCellsCopy(usedCells));

				// explicitly free up the memory ASAP
				fq2 = null;
				
				// bans var-value combination that was used for the extract
				addIgnoredValue(extractFactor.getVar(), extractFactor.getVal(), depth, usedCells);
			}
		}
	}

	// iterates though all different values for given variable to find rules
	private void iterateValues(int varNo, HashMap<Integer, AtomicInteger> h, FrequencyTableParent fq,
			FrequencyTableParent parentfq, int depth, LinkedList<BannedCell> usedCells) throws Exception {
		
		// for each ***variable-value*** combination, cycle
		// e.g. "VAR1, value 1" in one iteration "VAR1, value 2" on next etc.
		for(Entry<Integer, AtomicInteger> e1 : h.entrySet()){
			
			// value of variable
			int varValue = e1.getKey();

			// frequency of variable
			int freq = e1.getValue().intValue();
			
			
			/*System.out.println("Frequency of variable " + varNo + " on value " + varValue + " is " + freq);*/
			
			// if total frequency is 0, then there is no need to scan children
			// (children == distribution among class variable)
			if(freq == 0) continue;
			
			Integer count = null;
			
			// itereerime l2bi class variable spetsiifilised sagedustabelid, et kontrollida
			// kas sagedused kattuvad yldsagedustabeliga
			for(Entry<Integer, FrequencyTableChild> e2 : fq.children.entrySet()){
				
				// frequency of given var->value pair among objects where classvar=e2.getKey()
				count = e2.getValue().getFrequency(varNo, varValue);
				
				// if rule is found, check if it fits business parameters for data
				if(isRule(count, freq) && getObjectCount(count, freq) >= freeFactorLimitAlgo){
					
					// rule found, there cannot be several rules for one
					// variable here, frequency can only be 0 once
					Rule r = new Rule(e2.getKey(), getObjectCount(count, freq));
					r.addRulePart(varNo, varValue, fq.getVarName(varNo));
					
					// see if we need to add more parts to the rule
					ArrayDeque<VarValFrequency> ec = fq.getExtractConditionStack();
					if(ec.size() > 0){
						for(VarValFrequency v : ec){
							// add extract conditions as part of rule chain
							r.addRulePart(v.getVar(), v.getVal(), fq.getVarName(v.getVar()));
						}
					}
					
					// filter specific rows that match this rule
					r.setData(filterData(fq.getData(), e2.getKey(), r.getRuleParts(), r.getFrequency()));
					
					rawRuleSet.add(r);
					
					// ban the var-value combination where the rule was found
					if(banCell(varNo, varValue, fq, parentfq)){
						addIgnoredValue(varNo, varValue, depth, usedCells);
					}
					levelRuleCount++;
				}	
			}
		}
	}
	
	// by default a cell is banned when rule is found
	// can be overwritten
	protected boolean banCell(int varNo, int varValue, FrequencyTableParent fq,
			FrequencyTableParent parentfq) {
		return true;
	}

	private void addIgnoredValue(int varNo, int varValue,
			int depth, LinkedList<BannedCell> _usedCells) {
		
		_usedCells.add(new BannedCell(varNo, varValue, depth));
	}
	
	// returns the name of algorithm
	public abstract String getMethodName();
	
	// returns abbreviation of method name
	public abstract String getMethodNameShort();

	//returns empty frequency set. this is in separate method
	//so it can be overwritten (different algorithms might want to add different comparator)
	abstract TreeSet<VarValFrequency> createEmptyFrequencySet();

	// object count for rule based on total count (freq) and class count (count)
	// how object coverage is calculated, depends on algorithm version
	abstract int getObjectCount(Integer count, int freq);

	//rule condition is defined here in terms of
	//"count" in child frequency table for given class value
	//and "freq" which is frequency in main freq. table
	//this method must be overridden by other algorithms
	abstract boolean isRule(Integer count, int freq);
	
	// returns used cells according to algorithm depth
	private HashMap<Integer,ArrayList<Integer>> getUsedCells(int depth, LinkedList<BannedCell> _usedCells){
		
		HashMap<Integer,ArrayList<Integer>> uc = new HashMap<Integer,ArrayList<Integer>>();
		
		if(_usedCells == null)
			return uc;
		
		for(BannedCell c : _usedCells){
			// return only those cells that are banned on this level or upper levels
			// and not those which are banned on deeper levels in some other recursion
			if(c.getDepth() <= depth){
				if(!uc.containsKey(c.getVar())){
					// add column
					uc.put(c.getVar(), new ArrayList<Integer>());
				}
				// add value
				uc.get(c.getVar()).add(c.getVal());
				//System.out.println("Banned: " + c.getVar() +"." + c.getVal());
			}
		}
		return uc;
	}
	
	// get shallow copy of usedCells linked list
	private LinkedList<BannedCell> usedCellsCopy(LinkedList<BannedCell> c){
		LinkedList<BannedCell> uc = new LinkedList<>();
		
		for(BannedCell bc : c){
			uc.add(bc);
		}
		
		return uc;
	}
	
	// parses DataCaches and adds rows that match filter
	private DataCache filterData(DataCache d, int classValue, HashMap<Integer,Integer> ruleParts, int objectCount) throws Exception{
		DataCache subd = new DataCache(d.getHeader());
		
		DataRow dr;
		int count = 0;
		addrow: while((dr = d.nextLine()) != null){
			
			// check if class value matches
			if(columnMatch(classValue, dr, classColNumber)){
				continue;
			}
			
			// check if rule parts match this row
			for(Entry<Integer, Integer> e : ruleParts.entrySet()){
				if(!dr.columnMatch(e.getKey(), e.getValue())){
					continue addrow;
				}
			}
			
			subd.addRow(dr);
			
			count++;
			
			// we've found all the rows!
			if(count == objectCount)
				break;
		}
		return subd;
	}

	
	// can be overridden by specific algorithm
	// returns whether given class column can be added to the DataCache of a rule
	protected boolean columnMatch(int classValue, DataRow dr, int classColNum) {
		return !dr.columnMatch((classColNum-1), classValue);
	}
}
