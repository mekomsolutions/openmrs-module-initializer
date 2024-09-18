package org.openmrs.module.initializer.api.c;

import org.openmrs.ConceptNumeric;
import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("initializer.conceptReferenceRangeLineProcessor")
public class ConceptReferenceRangeLineProcessor extends BaseLineProcessor<ConceptReferenceRange> {

	
	private final String HEADER_CONCEPT_NUMERIC_UUID = "Concept Numeric uuid";

	private final String HEADER_AH = "Absolute high";

	private final String HEADER_CH = "Critical high";

	private final String HEADER_NH = "Normal high";

	private final String HEADER_AL = "Absolute low";

	private final String HEADER_CL = "Critical low";

	private final String HEADER_NL = "Normal low";

	private final String HEADER_CRITERIA = "Criteria";

	private ConceptService conceptService;

	@Autowired
	public ConceptReferenceRangeLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		this.conceptService = conceptService;
	}
	
	public ConceptReferenceRange fill(ConceptReferenceRange referenceRange, CsvLine line) throws IllegalArgumentException {
		ConceptNumeric conceptNumeric = conceptService.getConceptNumericByUuid(line.get(HEADER_CONCEPT_NUMERIC_UUID));
		
		if (conceptNumeric == null) { // below overrides any other processors work, so this one should be called first
			throw new IllegalArgumentException("No concept numeric found for '" + line.get(HEADER_CONCEPT_NUMERIC_UUID) + "'");
		}
		
		if (referenceRange == null) {
			referenceRange = new ConceptReferenceRange();
		}
		
		referenceRange.setHiAbsolute(line.getDouble(HEADER_AH));
		referenceRange.setHiCritical(line.getDouble(HEADER_CH));
		referenceRange.setHiNormal(line.getDouble(HEADER_NH));
		referenceRange.setLowAbsolute(line.getDouble(HEADER_AL));
		referenceRange.setLowCritical(line.getDouble(HEADER_CL));
		referenceRange.setLowNormal(line.getDouble(HEADER_NL));
		referenceRange.setConceptNumeric(conceptNumeric);
		referenceRange.setCriteria(line.getString(HEADER_CRITERIA));
		
		return referenceRange;
	}
}
