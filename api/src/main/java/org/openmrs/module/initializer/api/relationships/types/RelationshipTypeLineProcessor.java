package org.openmrs.module.initializer.api.relationships.types;

import org.openmrs.RelationshipType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@Component
public class RelationshipTypeLineProcessor extends BaseLineProcessor<RelationshipType> {
	
	final public static String WEIGHT = "weight";
	
	final public static String PREFERRED = "preferred";
	
	final public static String A_IS_TO_B = "a is to b";
	
	final public static String B_IS_TO_A = "b is to a";
	
	@Override
	public RelationshipType fill(RelationshipType instance, CsvLine line) throws IllegalArgumentException {
		instance.setName(line.get(HEADER_NAME));
		instance.setDescription(line.get(HEADER_DESC));
		instance.setaIsToB(line.get(A_IS_TO_B, true));
		instance.setbIsToA(line.get(B_IS_TO_A, true));
		instance.setPreferred(line.getBool(PREFERRED));
		Integer weight = line.getInt(WEIGHT);
		if (weight != null) {
			instance.setWeight(weight);
		}
		return instance;
	}
	
}
