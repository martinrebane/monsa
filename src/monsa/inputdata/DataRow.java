package monsa.inputdata;

public class DataRow {
	private int rowNumberInDataset;
	private int[] data;
	
	DataRow(int rowNumber, int[] data){
		this.rowNumberInDataset = rowNumber;
		this.data = data;
	}
	
	//accept integers as string and convert to int
	DataRow(int rowNumber, String[] dataStr, ValueMapper valueMapper){
		this.rowNumberInDataset = rowNumber;
		this.data = new int[dataStr.length];
		
		int i = 0;
		for(String s : dataStr){
			data[i] = valueMapper.getValueAsInt(s);
			//data[i] = Integer.parseInt(s);
			i++;
		}
	}

	public int getRowNumber() {
		return rowNumberInDataset;
	}

	public int[] getData() {
		return data;
	}
	
	//returns whether this row has varNo == val
	public boolean columnMatch(int varNo, int val){
		return data[varNo] == val;
	}
}
