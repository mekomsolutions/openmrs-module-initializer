package org.openmrs.module.initializer.api.c;

import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptsCsvParser extends CsvParser<Concept, BaseLineProcessor<Concept>> {
	
	private ConceptService conceptService;
	
	private ConceptNumericLineProcessor numericProcessor;
	
	private ConceptComplexLineProcessor complexProcessor;
	
	private ConceptLineProcessor baseProcessor;
	
	private NestedConceptLineProcessor nestedProcessor;
	
	private MappingsConceptLineProcessor mappingsProcessor;
	
	@Autowired
	public ConceptsCsvParser(@Qualifier("conceptService") ConceptService conceptService,
	    @Qualifier("initializer.conceptLineProcessor") ConceptLineProcessor baseProcessor,
	    @Qualifier("initializer.conceptNumericLineProcessor") ConceptNumericLineProcessor numericProcessor,
	    @Qualifier("initializer.conceptComplexLineProcessor") ConceptComplexLineProcessor complexProcessor,
	    @Qualifier("initializer.nestedConceptLineProcessor") NestedConceptLineProcessor nestedProcessor,
	    @Qualifier("initializer.mappingsConceptLineProcessor") MappingsConceptLineProcessor mappingsProcessor) {
		
		super();
		
		this.conceptService = conceptService;
		
		this.baseProcessor = baseProcessor;
		this.numericProcessor = numericProcessor;
		this.complexProcessor = complexProcessor;
		this.nestedProcessor = nestedProcessor;
		this.mappingsProcessor = mappingsProcessor;
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPTS;
	}
	
	@Override
	protected Concept save(Concept instance) {
		return conceptService.saveConcept(instance);
	}
	
	@Override
	protected void setLineProcessors(String version, String[] headerLine) {
		lineProcessors.clear();
		lineProcessors.add(numericProcessor.setHeaderLine(headerLine));
		lineProcessors.add(complexProcessor.setHeaderLine(headerLine));
		lineProcessors.add(baseProcessor.setHeaderLine(headerLine));
		lineProcessors.add(nestedProcessor.setHeaderLine(headerLine));
		lineProcessors.add(mappingsProcessor.setHeaderLine(headerLine));
	}
}
