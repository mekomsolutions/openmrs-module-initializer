package org.openmrs.module.initializer.api.freq;

import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.ConceptService;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
@Component
public class OrderFrequencyLineProcessor extends BaseLineProcessor<OrderFrequency> {
	
	protected static String HEADER_FREQ_PER_DAY = "frequency per day";
	
	protected static String HEADER_CONCEPT_FREQ = "concept frequency";
	
	private ConceptService conceptService;
	
	@Autowired
	public OrderFrequencyLineProcessor(@Qualifier("conceptService") ConceptService conceptService) {
		super();
		this.conceptService = conceptService;
	}
	
	@Override
	public OrderFrequency fill(OrderFrequency freq, CsvLine line) throws IllegalArgumentException {
		
		Concept conceptFreq = Utils.fetchConcept(line.get(HEADER_CONCEPT_FREQ), conceptService);
		
		freq.setConcept(conceptFreq);
		freq.setFrequencyPerDay(line.getDouble(HEADER_FREQ_PER_DAY));
		
		return freq;
	}
}
