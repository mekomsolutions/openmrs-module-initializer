package org.openmrs.module.initializer.api.display;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DisplaysPreLoader extends BaseCsvLoader<OpenmrsObject, DisplaysCsvParser> {
	
	@Override
	public boolean isPreLoader() {
		return true;
	}
	
	@Override
	public String getDomainName() {
		return "displays";
	}
	
	@Override
	public Integer getOrder() {
		return 0;
	}
	
	@Autowired
	public void setParser(DisplaysCsvParser parser) {
		this.parser = parser;
	}
	
	@Override
	public void setDisplaysPreLoader(DisplaysPreLoader displaysLoader) {
		// this override prevents the parent's method Spring autowiring that would result in a cyclic bean creation error.
	}
	
	public void setBootstrapParser(
	        CsvParser<? extends OpenmrsObject, ? extends BaseLineProcessor<? extends OpenmrsObject>> parser) {
		this.parser.setBootstrapParser(parser);
	}
	
}
