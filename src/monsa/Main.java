package monsa;


public class Main {

	/**
	 * @param args
	 */
	
/* for testing in eclipse only */
		
		private static String filename = "C:\\TTU\\mag\\nursery_dataset\\nursery_num.csv";
		private static int classColNumber = 9;
	
	
	public static void main(String[] args) {
		try {
			
			MonsaMax mmax = new MonsaMax(filename, classColNumber);
			mmax.runAlgo();
			mmax.printData(new OutputAdapter(),false,false,true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}