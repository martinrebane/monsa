package monsa.inputdata;

import java.util.HashMap;
import java.util.Map;

public class ValueMapper {
	
	private Map<String, Integer> valueMap = new HashMap<>();
	
	int getValueAsInt(String value) {
		if (!valueMap.containsKey(value)) {
			assignIntToStringValue(value); 
		}
		return valueMap.get(value);
	}

	private void assignIntToStringValue(String value) {
		valueMap.put(value, valueMap.size()+1);
	}
}
