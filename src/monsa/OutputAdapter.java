package monsa;

/* Default output adapter that outputs to console
 * 
 * Monsa outputs everything to the selected adapter
 * 
 * Each user can overwrite this default adapter by
 * providing it's own output() method
 * 
 * */

public class OutputAdapter {
	public void outputLine(String text){
		System.out.println(text);
	}
	
	/* method can be used by subclasses */
	public void close(){
	}
}
