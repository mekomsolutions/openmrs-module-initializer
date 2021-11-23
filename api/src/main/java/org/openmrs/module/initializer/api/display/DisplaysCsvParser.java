package org.openmrs.module.initializer.api.display;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
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
	
	@Override
	public OpenmrsObject bootstrap(CsvLine line) throws IllegalArgumentException {
		return bootstrapParser.bootstrap(line);
	}
	
	@Override
	public OpenmrsObject save(OpenmrsObject instance) {
		Context.clearSession();
		return instance;
	}
}
