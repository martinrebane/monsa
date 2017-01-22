package monsa;

public class VarValFrequency implements Comparable<VarValFrequency>{
	private int variable;
	private int value;
	private int frequency;
	
	public VarValFrequency(int var, int val, int freq){
		variable = var;
		value = val;
		frequency = freq;
	}
	
	public int getVar(){
		return variable;
	}
	
	public int getVal(){
		return value;
	}
	
	public int getFreq(){
		return frequency;
	}

	@Override
	public int compareTo(VarValFrequency o) {
		//order object by:
		//1. frequency
		//2. variable
		//3. value
		
		//if equal frequencies, decide by Variable no
		if(getFreq() == o.getFreq()){
			//if equal variable no, decide by value
			if(getVar() == o.getVar()){
				if(getVal() == o.getVal())
					return 0;
				if(getVal() > o.getVal())
					return -1;
				return 1;
			}
			if(getVar() > o.getVar())
				return -1;
			return 1;
		}
		if(getFreq() > o.getFreq())
			return 1;
		return -1;
	}
}
