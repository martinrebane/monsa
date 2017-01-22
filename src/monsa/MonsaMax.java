package monsa;

import java.util.Collections;
import java.util.TreeSet;

public class MonsaMax extends Monsa {

	public MonsaMax(String filename, int classColNumber) throws Exception {
		super(filename, classColNumber);
	}
	
	@Override
	//returns the name of algorithm
	public String getMethodName() {
		return "MONSAMAX";
	}
	
	@Override
	public String getMethodNameShort() {
		return "MMax";
	}
	
	@Override
	protected boolean isRule(Integer count, int freq){
		//if frequency in main frequncy table match
		//frequency in a freq table for given class value
		//then we have found a rule
		if(count == freq)
			return true;
		return false;
	}
	
	//Monsamax scans frequencies in reverse order, from largest to smallest
	@Override
	protected TreeSet<VarValFrequency> createEmptyFrequencySet() {
		// add reverseOrder comparator
		return new TreeSet<VarValFrequency>(Collections.reverseOrder());
	}
	
	@Override
	protected int getObjectCount(Integer count, int freq) {
		return count;
	}
	
}
