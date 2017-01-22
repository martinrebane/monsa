package monsa;

import java.util.Random;

public class RandomDataset {
	
	private StringBuilder dataset = new StringBuilder();
	private int rangeStart = 1;
	private int rangeEnd = 5;
	
	RandomDataset(int varCount, int rowCount){
		
		Random rand = new Random();
		
		for(int i=0;i<varCount;i++){
			//header
			dataset.append("Var"+i);
			if(i<(varCount-1)){
				dataset.append(";");
			}
		}
		
		dataset.append("\n");
		
		for(int i=0;i<rowCount;i++){
			for(int j=0;j<varCount;j++){
				
				//generate a random number in range
				dataset.append((rand.nextInt(rangeEnd-rangeStart) + rangeStart));
				
				if(j<(varCount-1)){
					dataset.append(";");
				}
				//add line break for last row
				else {
					dataset.append("\n");
				}
			}
		}
	}
	
	public String toString(){
		return dataset.toString();
	}
}
