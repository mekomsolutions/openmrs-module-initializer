package org.openmrs.module.initializer.api.conceptreferencerange;

import org.openmrs.ConceptReferenceRange;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ConceptReferenceRangeParser extends CsvParser<ConceptReferenceRange, BaseLineProcessor<ConceptReferenceRange>> {
	
	private ConceptService conceptService;
	
	public ConceptReferenceRangeParser(@Qualifier("conceptService") ConceptService conceptService,
	    ConceptReferenceRangeLineProcessor lineProcessor) {
		super(lineProcessor);
		this.conceptService = conceptService;
	}
	
	@Override
	public ConceptReferenceRange bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = line.getUuid();
		ConceptReferenceRange conceptReferenceRange = conceptService.getConceptReferenceRangeByUuid(uuid);
		
		if (conceptReferenceRange == null) {
			conceptReferenceRange = new ConceptReferenceRange();
			conceptReferenceRange.setUuid(uuid);
		}
		
		return conceptReferenceRange;
	}
	
	@Override
	public ConceptReferenceRange save(ConceptReferenceRange instance) {
		return conceptService.saveConceptReferenceRange(instance);
	}
	
	@Override
	public Domain getDomain() {
		return Domain.CONCEPT_REFERENCE_RANGE;
	}
}
