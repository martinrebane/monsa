package monsa;

import java.util.Iterator;
import java.util.LinkedList;

public class DataCache {
	private String[] header;
	private LinkedList<DataRow> dataRows = new LinkedList<>();
	private Iterator<DataRow> it;
	
	DataCache(String[] h){
		header = h;
	}
	
	//add one row of data
	public void addRow(DataRow line){
		dataRows.add(line);
	}
	
	public String[] getHeader(){
		//resets lines iterator
		setIterator();
		return header;
	}
	
	public void setIterator(){
		it = dataRows.iterator();
	}

	// gets next row from the dataset
	// be careful to reset the iterator when doing several iterations of some object
	public DataRow nextLine(){
		if(it == null){
			setIterator();
		}
		
		if(it.hasNext()){
			return it.next();
		}
		else {
			setIterator();
			return null;
		}
		
	}
	
	public int getDataSize() {
		return dataRows.size();
	}
}
