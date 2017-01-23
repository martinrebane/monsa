package monsa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RuleMap {

private HashMap<Integer,HashMap<Integer,AtomicInteger>> mapTable = new HashMap<>();

// compute how many times each object is covered by rules
RuleMap(Map<Integer,HashMap<Integer,AtomicInteger>> fp, ArrayList<Rule> ruleSet){
	
	// copy over the table to create new freq table with same structure
	// but all frequencies zeroes
	for(int key : fp.keySet()){
		HashMap<Integer,AtomicInteger> valueSet = new HashMap<>();
		mapTable.put(key, valueSet);
		for(int innerKey: fp.get(key).keySet()){
			valueSet.put(innerKey, new AtomicInteger(0));
		}
	}
	System.out.println(mapTable);
	// compute how many rules cover specific values
	for(Rule r : ruleSet){
		Map<Integer, Integer> rp = r.getRuleParts();
		for(int k : rp.keySet()){
			//update frequency by one
			mapTable.get(k).get(rp.get(k)).getAndIncrement();
		}
	}
	
}

}
