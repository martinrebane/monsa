package monsa.inputdata;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import monsa.FrequencyTableParent;

public class DatasetPreprocessor {
	
	
	private String filename;
	private String separator;
	
	public DatasetPreprocessor(String filename, String separator){
		this.filename = filename;
		this.separator = separator;
		
	}
	
	//reads in the data and returns data row count, not counting header row
	@SuppressWarnings("unchecked")
	public int readFileIntoFrequencyTableAndGetNumOfRows(FrequencyTableParent fq) throws IOException {

		//add header to frequency table
		fq.parseHeader(getHeader(), separator);
		
		//add data line by line
		int line_num = 0;
		
		try(Stream<String> lineStream = Files.lines(Paths.get(filename))) {
			for (String line : (Iterable<String>) lineStream.skip(1)::iterator) {
				fq.addLine(new DataRow(line_num, line, separator));
				line_num++;	
			}
		}

		return line_num;
	}
	
	private String getHeader() throws IOException {
		Path file = Paths.get(filename);

		// read the file in line-by-line
		if (!Files.isReadable(file)) {
			throw new IOException("File " + filename + " is not readable!");
		}

		try (FileReader fr = new FileReader(file.toString())) {
			BufferedReader bf = new BufferedReader(fr);
			return bf.readLine();
		}
	}
}
