package org.openmrs.module.initializer.api.freq;

import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderFrequenciesLoader extends BaseCsvLoader<OrderFrequenciesCsvParser> {
	
	@Autowired
	public void setParser(OrderFrequenciesCsvParser parser) {
		this.parser = parser;
	}
}
