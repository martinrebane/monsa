package monsa.inputdata;

import monsa.FrequencyTableParent;

public class DatasetPreprocessor {
	
	
	private FileCachedReader fcr;

	private String filename;
	private String separator;
	
	public DatasetPreprocessor(String filename, String separator){
		this.filename = filename;
		this.separator = separator;
		
	}
	
	//reads in the data and returns data row count, not counting header row
	public int readFileIntoFrequencyTableAndGetNumOfRows(FrequencyTableParent fq) throws Exception {
		
		if(fcr == null){
			fcr = new FileCachedReader(filename);
		}
		
		//add header to frequency table
		fq.parseHeader(fcr.getHeader(), separator);
		
		//add data line by line
		String line;
		int line_num = 0;
		
		while((line = fcr.nextLine()) != null){
			fq.addLine(new DataRow(line_num, line, separator));
			line_num++;
		}
		
		return line_num;
	}

}
