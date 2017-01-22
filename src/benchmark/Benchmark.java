package benchmark;

import cover.*;
import monsa.Monsa;
import monsa.MonsaBan;
import monsa.MonsaMax;
import monsa.MonsaMin;

public class Benchmark {

	//private static String filename = "C:\\TTU\\mag\\nursery_dataset\\nursery_num.csv";
	//private static String filename = "C:\\Users\\Martin\\Documents\\aandmestik-tunnustearv\\5x_orig";
	//private static String filename = "C:\\Users\\Martin\\Documents\\aandmestik-tunnustearv\\4x_orig_1x_extra";
	//private static String filename = "C:\\Users\\Martin\\Documents\\aandmestik-tunnustearv\\3x_orig_2x_extra";
	//private static String filename = "C:\\Users\\Martin\\Documents\\aandmestik-tunnustearv\\2x_orig_3x_extra";
	private static String filename = "C:\\Users\\Martin\\Documents\\aandmestik-tunnustearv\\1x_orig_5x_extra";
	
	
	private static int classColNumber = 9;
	private static int freeFactorLimitCover = 1;
	
	private static int iterations = 12;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			//running time summing
			double algo = 0;
			double dsr = 0;
			double cover = 0;
			Monsa mmax = null;
			Coverage c = null;
			
			// run once to read into memory
			//mmax = new MonsaMax(filename, classColNumber);
			//mmax = new MonsaMin(filename, classColNumber);
			mmax = new MonsaBan(filename, classColNumber);
			c = new ApproxCoverage(freeFactorLimitCover);
			//c = new GreedyCoverage(freeFactorLimitCover);
			//c = new UniqueGreedyCoverage(freeFactorLimitCover);
			mmax.setCoverage(c);
			mmax.runAlgo();
			
			for (int i=1; i<=iterations; i++) {
				System.gc();
				//mmax = new MonsaMax(filename, classColNumber);
				//mmax = new MonsaMin(filename, classColNumber);
				mmax = new MonsaBan(filename, classColNumber);
				c = new ApproxCoverage(freeFactorLimitCover);
				//c = new GreedyCoverage(freeFactorLimitCover);
				//c = new UniqueGreedyCoverage(freeFactorLimitCover);
				mmax.setCoverage(c);
				
				mmax.runAlgo();
				
				algo += mmax.getAlgoTime();
				dsr += mmax.getDSRTime();
				cover += mmax.getCoverTime();
			}
			
			System.out.println(filename);
			System.out.println("Method: " + mmax.getMethodName());
			System.out.println("Cover algo: " + c.getName());
			System.out.println("Algorithm time: " + algo/(iterations*1.0));
			System.out.println("DSR time: " + dsr/(iterations*1.0));
			System.out.println("Cover time " + cover/(iterations*1.0));
			
			System.out.println("" + algo/(iterations*1.0));
			System.out.println("" + dsr/(iterations*1.0));
			System.out.println("" + cover/(iterations*1.0));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
