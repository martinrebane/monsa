package monsa;

public class BannedCell {

private final int var;
private final int val;
private final int depth;

BannedCell(int var, int val, int depth){
	this.var = var;
	this.val = val;
	this.depth = depth;
}

public final int getVar() {
	return var;
}

public final int getVal() {
	return val;
}

public final int getDepth() {
	return depth;
}
	
}
