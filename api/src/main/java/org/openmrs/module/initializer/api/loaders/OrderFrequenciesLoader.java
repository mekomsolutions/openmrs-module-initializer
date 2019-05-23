package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;

import org.openmrs.module.initializer.api.freq.OrderFrequenciesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderFrequenciesLoader extends BaseCsvLoader<OrderFrequenciesCsvParser> {
	
	@Autowired
	public void setParser(OrderFrequenciesCsvParser parser) throws IOException {
		this.parser = parser;
	}
	
}
