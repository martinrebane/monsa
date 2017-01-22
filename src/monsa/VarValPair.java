package monsa;

public class VarValPair {
	
	//no of variable
	private int variable;
	//no of value
	private int value;
	
	private int hash;
	
	public VarValPair(int var, int val){
		variable = var;
		value = val;
	}
	
	public int getVariable() {
		return variable;
	}
	
	public int getValue() {
		return value;
	}
	
	public int hashCode(){
		/*hashcode already computed*/
		if(hash > 0){
			return hash;
		}
		//compute hashcode
		hash = 17;
		hash = 31 * hash + variable;
		hash = 31 * hash + value;
		
		return hash;
	}
	
}
