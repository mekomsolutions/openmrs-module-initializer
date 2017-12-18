package org.openmrs.module.initializer.api.freq;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.impl.Utils;

/**
 * This is the first level line processor for concepts. It allows to parse and save concepts with
 * the minimal set of required fields.
 */
public class OrderFrequencyLineProcessor extends BaseLineProcessor<OrderFrequency, OrderService> {
	
	protected static String HEADER_FREQ_PER_DAY = "frequency per day";
	
	protected static String HEADER_CONCEPT_FREQ = "concept frequency";
	
	public OrderFrequencyLineProcessor(String[] headerLine, OrderService service) {
		super(headerLine, service);
	}
	
	@Override
	protected OrderFrequency bootstrap(CsvLine line) throws IllegalArgumentException {
		String uuid = getUuid(line.asLine());
		OrderFrequency freq = service.getOrderFrequencyByUuid(uuid);
		if (freq == null) {
			freq = new OrderFrequency();
			if (!StringUtils.isEmpty(uuid)) {
				freq.setUuid(uuid);
			}
		}
		
		freq.setRetired(getVoidOrRetire(line.asLine()));
		
		return freq;
	}
	
	protected OrderFrequency fill(OrderFrequency freq, CsvLine line) throws IllegalArgumentException {
		
		Concept conceptFreq = Utils.fetchConcept(line.get(HEADER_CONCEPT_FREQ), Context.getConceptService());
		freq.setConcept(conceptFreq);
		freq.setFrequencyPerDay(line.getDouble(HEADER_FREQ_PER_DAY));
		
		return freq;
	}
}
