package org.openmrs.module.initializer.api.conceptreferencerange;

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
	
	private final String HEADER_CONCEPT_NUMERIC_UUID = "Concept Numeric Uuid";
	
	private final String HEADER_AH = "Absolute high";
	
	private final String HEADER_CH = "Critical high";
	
	private final String HEADER_NH = "Normal high";
	
	private final String HEADER_AL = "Absolute low";
	
	private final String HEADER_CL = "Critical low";
	
	private final String HEADER_NL = "Normal low";
	
	private final String HEADER_CRITERIA = "Criteria";
	
	private ConceptService conceptService;
	
	private final ConceptReferenceRangeService conceptReferenceRangeService;
	
	@Autowired
	public ConceptReferenceRangeLineProcessor(@Qualifier("conceptService") ConceptService conceptService,
	    ConceptReferenceRangeService conceptReferenceRangeService) {
		this.conceptService = conceptService;
		this.conceptReferenceRangeService = conceptReferenceRangeService;
	}
	
	public ConceptReferenceRange fill(ConceptReferenceRange referenceRange, CsvLine line) throws IllegalArgumentException {
		//		ConceptNumeric conceptNumeric = conceptService.getConceptNumericByUuid(line.get(HEADER_CONCEPT_NUMERIC_UUID));
		ConceptNumeric conceptNumeric = conceptReferenceRangeService
		        .getConceptNumericByUuid(line.get(HEADER_CONCEPT_NUMERIC_UUID));
		
		if (conceptNumeric == null) {
			throw new IllegalArgumentException(
			        "No concept numeric found for '" + line.get(HEADER_CONCEPT_NUMERIC_UUID) + "'");
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
