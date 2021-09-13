package org.openmrs.module.initializer.api.display;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.Domain;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.openmrs.module.initializer.api.CsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisplaysCsvParser extends CsvParser<OpenmrsObject, BaseLineProcessor<OpenmrsObject>> {
	
	private CsvParser<? extends OpenmrsObject, ? extends BaseLineProcessor<? extends OpenmrsObject>> bootstrapParser;
	
	public void setBootstrapParser(
	        CsvParser<? extends OpenmrsObject, ? extends BaseLineProcessor<? extends OpenmrsObject>> parser) {
		this.bootstrapParser = parser;
	}
	
	@Autowired
	public DisplaysCsvParser(DisplayLineProcessor baseProcessor) {
		super(baseProcessor);
	}
	
	@Override
	public Domain getDomain() {
		return null;
	}
	
	/*
	 * In case no UUID can be read from the CSV line or underlying object is being retired/voided this will 
	 * result in an error this will return null.
	 */
	@Override
	public OpenmrsObject bootstrap(CsvLine line) throws IllegalArgumentException {
		if (BaseLineProcessor.getVoidOrRetire(line)) {
			return null;
		}
		return isBlank(line.getUuid()) ? null : bootstrapParser.bootstrap(line);
	}
	
	@Override
	public OpenmrsObject save(OpenmrsObject instance) {
		return instance;
	}
	
}
