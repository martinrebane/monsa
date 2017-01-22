package monsa.inputdata;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;

public class FileCachedReader {
	
private BufferedReader bf;
private String cachedHeader;
private LinkedList<String> lines = new LinkedList<>();
private Iterator<String> it;

/* Constructor for reading in a file */
FileCachedReader(String filename) throws IOException{
	
	Path file = Paths.get(filename);
	 
	//read the file in line-by-line
	if(!Files.isReadable(file)){
		throw new IOException("Fail " + filename + "ei ole loetav!");
	}
	
	try (FileReader fr = new FileReader(file.toString())){
		bf = new BufferedReader(fr);
		cachedHeader = bf.readLine();
		
		String line;
		while((line = bf.readLine()) != null){
			lines.add(line);
		}
	}
}

public String getHeader(){
	//open lines iterator
	it = lines.iterator();
	
	return cachedHeader;
}

//gets next row from the dataset
public String nextLine() throws Exception{
	if(it == null){
		throw new Exception("Access nextLine() only after calling getHeader()");
	}
	if(it.hasNext())
		return it.next();
	return null;
}
	
}
