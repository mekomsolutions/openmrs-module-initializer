package org.openmrs.module.initializer.api.c;

import org.apache.commons.lang3.BooleanUtils;
import org.openmrs.ConceptMapType;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@Component
public class ConceptMapTypeLineProcessor extends BaseLineProcessor<ConceptMapType> {

	public static final String HEADER_IS_HIDDEN = "Is hidden";
	
	public ConceptMapType fill(ConceptMapType mapType, CsvLine line) throws IllegalArgumentException {
		mapType.setName(line.get(HEADER_NAME, true));
		mapType.setDescription(line.get(HEADER_DESC));
		mapType.setIsHidden(BooleanUtils.isTrue(line.getBool(HEADER_IS_HIDDEN)));
		return mapType;
	}
}
