package monsa;

import java.util.TreeSet;

public class MonsaMin extends Monsa {

	public MonsaMin(String filename, int classColNumber) throws Exception {
		super(filename, classColNumber);
	}
	
	@Override
	//returns the name of algorithm
	public String getMethodName() {
		return "MONSAMIN";
	}

	@Override
	public String getMethodNameShort() {
		return "MMin";
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
	
	@Override
	protected int getObjectCount(Integer count, int freq) {
		return count;
	}

	@Override
	protected TreeSet<VarValFrequency> createEmptyFrequencySet() {
		return new TreeSet<VarValFrequency>();
	}
}
