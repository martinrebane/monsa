package monsa;

import java.util.HashMap;
import java.util.TreeSet;

import monsa.inputdata.DataRow;

public class MonsaBan extends Monsa {

	public MonsaBan(String filename, int classColNumber) throws Exception {
		super(filename, classColNumber);
	}
	
	@Override
	//returns the name of algorithm
	public String getMethodName() {
		return "MONSABAN";
	}
	
	@Override
	public String getMethodNameShort() {
		return "MBan";
	}
	
	@Override
	protected TreeSet<VarValFrequency> createEmptyFrequencySet() {
		return new TreeSet<VarValFrequency>();
	}
	
	@Override
	protected int getObjectCount(Integer count, int freq) {
		return freq;
	}
	
	@Override
	protected boolean isRule(Integer count, int freq) {
		return count == 0;
	}
	
	@Override
	//returns whether given class column can be added to the DataCache of a rule
	protected boolean columnMatch(int classValue, DataRow dr, int classColNum) {
		return dr.columnMatch((classColNum-1), classValue);
	}

}
