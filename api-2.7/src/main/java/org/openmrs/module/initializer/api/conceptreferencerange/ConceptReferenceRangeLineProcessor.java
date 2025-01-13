package org.openmrs.module.initializer.api.conceptreferencerange;

import org.openmrs.ConceptNumeric;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.openmrs.ConceptReferenceRange;

@Component
public class ConceptReferenceRangeLineProcessor extends BaseLineProcessor<ConceptReferenceRange> {
	
	private final String HEADER_CONCEPT_NUMERIC_UUID = "Concept Numeric uuid";
	
	private final String HEADER_ABSOLUTE_LOW = "Absolute Low";
	
	private final String HEADER_ABSOLUTE_HIGH = "Absolute High";
	
	private final String HEADER_CRITICAL_LOW = "Critical Low";
	
	private final String HEADER_CRITICAL_HIGH = "Critical High";
	
	private final String HEADER_NORMAL_LOW = "Normal Low";
	
	private final String HEADER_NORMAL_HIGH = "Normal High";
	
	private final String HEADER_CRITERIA = "Criteria";
	
	private final ConceptService conceptService;
	
	public ConceptReferenceRangeLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	public ConceptReferenceRange fill(ConceptReferenceRange conceptReferenceRange, CsvLine line)
	        throws IllegalArgumentException {
		ConceptNumeric conceptNumeric = conceptService.getConceptNumericByUuid(line.get(HEADER_CONCEPT_NUMERIC_UUID, true));
		
		if (conceptNumeric == null) {
			throw new IllegalArgumentException(HEADER_CONCEPT_NUMERIC_UUID + " not found");
		}
		
		conceptReferenceRange.setConceptNumeric(conceptNumeric);
		conceptReferenceRange.setHiAbsolute(line.getDouble(HEADER_ABSOLUTE_HIGH));
		conceptReferenceRange.setLowAbsolute(line.getDouble(HEADER_ABSOLUTE_LOW));
		conceptReferenceRange.setHiCritical(line.getDouble(HEADER_CRITICAL_HIGH));
		conceptReferenceRange.setLowCritical(line.getDouble(HEADER_CRITICAL_LOW));
		conceptReferenceRange.setHiNormal(line.getDouble(HEADER_NORMAL_HIGH));
		conceptReferenceRange.setLowNormal(line.getDouble(HEADER_NORMAL_LOW));
		conceptReferenceRange.setCriteria(line.getString(HEADER_CRITERIA));
		return conceptReferenceRange;
	}
}
