package monsa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Rule implements Comparable<Rule>{
	//class variable value that the rule acts upon
	private int classVariableValue;
	
	private Map<Integer, String> colNames = new HashMap<>();
	
	//pairs of Var-Value that will cause the rule to happen
	private Map<Integer, Integer> rule = new HashMap<>();

	// how many objects are covered
	private int frequency;
	
	// how many times the objects inside the rule are covered as average
	// for perfect rule, that does not overlap with anything, this number is 1
	private int overCoverageCoef;
	
	private boolean isDuplicate = false;

	private DataCache data;
	
	private int hash;
	
	// coverage coeficent base value used by coverageByWeight algorithm
	private float coverageCoefBase = 0;

	public Rule(int classVariableValue, int freq) {
		this.classVariableValue = classVariableValue;
		frequency = freq;
	}
	
	public int getClassVarVal(){
		return classVariableValue;
	}
	
	//returns the number of objects that this rule covers
	public int getFrequency(){
		return frequency;
	}

	public void addRulePart(int var, int val, String varLabel) {
		// save data
		rule.put(var, val);
		// save label
		colNames.put(var, varLabel);
	}
	
public String toString() {
		
		StringBuilder str = new StringBuilder();
		
		for(Integer key : rule.keySet()){
			if(str.length() > 0){
				str.append(" & ");
			}
			
			//verbose & 0-indexed
			//str.append("Var " + key +"(" + colNames.get(key) + ")" + "=" + rule.get(key));
			
			//non-verbose & 0-indexed
			//str.append(key + "." + rule.get(key));
			
			//non-verbose & 1-indexed
			str.append((key + 1) + "." + rule.get(key));
		}
		
		str.append(" => class " + classVariableValue);
		
		str.append(" (" + frequency + ")");
		
		return str.toString();
	}

@Override
/* This method compares rules based on:
 * 1. class value that it determines
 * 2. length of the rule if class value is the same 
 * 3. hashCode of rule variable if length is the same, but rules are different
 * */
public int compareTo(Rule o) {

	// if references to the same object, return equals
	if(o == this){
		return 0;
	}
	
	// if found class is different, we can safely return
	if(classVariableValue < o.getClassVarVal())
		return -1;
	else if(classVariableValue > o.getClassVarVal())
		return 1;
	
	// see which is shorter, we need this to compare
	Rule shorter = this;
	Rule longer = o;
	int isLonger = -1;
	if(o.size() < this.size()){
		isLonger = 1;
		shorter = o;
		longer = this;
	}
	// length is equal
	else if(o.size() == this.size()){
		isLonger = 0;
	}
	
	//have to compare rule parts
	for(Entry<Integer, Integer> e : shorter.rule.entrySet()){
		//if longer does not have a key that shorter has
		//it cannot cover same objects (or if it does, both will be
		//wiped out by some other rule anyway)
		//EG: shorter: 1 3;longer: 1 4 5 - longer does not contain 3, so not the same
		//shorter: 1 2 3; longer: 1 2 4 5 - there must be a better rule
		//1 2 which will wipe both away anyway
		if(!longer.containsKey(e.getKey())){
			return compareLenghtAndHash(o, isLonger);
		}
		else{
			//if contains key, check value
			if(longer.getClassValue(e.getKey()) != e.getValue()){
				//for sorting reason we will only consider hashcode when
				//keys or values are not equal
				return compareLenghtAndHash(o, isLonger);
			}
		}
	}
	
	//if we are here, longer rule must be a duplicate of shorter
	//if they are of equal lenght, we do not care which one to keep
	//but one of them must remain unmarked, so we check for that
	if(!shorter.isDuplicate()){
		longer.markDuplicate();
	}
	
	return compareLenghtAndHash(o, isLonger);
}

//compare rules based on length and hashcode of rule variable
private int compareLenghtAndHash(Rule o, int isLonger) {
	if(isLonger != 0){
		return isLonger;
	}
	
	if(ruleHashCode() > o.ruleHashCode())
		return 1;
	if(ruleHashCode() < o.ruleHashCode())
		return -1;
	return 0;
}

//return the value for given variable
private Integer getClassValue(int key) {
	return rule.get(key);
}

//if rule contains given variable
private boolean containsKey(int key) {
	return rule.containsKey(key);
}

public int size(){
	return rule.size();
}

public void markDuplicate(){
	isDuplicate = true;
}

public int ruleHashCode(){
	return rule.hashCode();
}

public boolean isDuplicate() {
	return isDuplicate;
}

public Map<Integer,Integer> getRuleParts(){
	return rule;
}

public void setData(DataCache data) {
	this.data = data;
	
}

public DataCache getData() {
	return data;
}

public int hashCode(){
	if(hash != 0)
		return hash;
	
	hash = (rule.toString() + classVariableValue).hashCode();
	return hash;
}

//compares two rules by coverage, in descending order (bigger is smaller)
public int compareCoverage(Rule other){
	if(this.frequency > other.getFrequency())
		return -1;
	if(this.frequency < other.getFrequency())
		return 1;
	return 0;
}

public int getOverCoverageCoef() {
	return overCoverageCoef;
}

public void setOverCoverageCoef(int overCoverageCoef) {
	this.overCoverageCoef = overCoverageCoef;
}

// returns coverage coefficient used by coveredByWeight algorithm
// it sums up coverage counts for each object that is covered by given rule
// and divides it by frequency
public float getCoverageCoef() {
	return coverageCoefBase/getFrequency();
}

public void incrementCoverageCoefBase(float coverageCoef) {
	this.coverageCoefBase = (float) (this.coverageCoefBase + Math.pow(coverageCoef,2));
}

}
