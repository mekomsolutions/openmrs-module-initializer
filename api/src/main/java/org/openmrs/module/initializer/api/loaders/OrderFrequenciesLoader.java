package org.openmrs.module.initializer.api.loaders;

import java.io.IOException;
import java.io.InputStream;

import org.openmrs.api.OrderService;
import org.openmrs.module.initializer.InitializerConstants;
import org.openmrs.module.initializer.api.CsvParser;
import org.openmrs.module.initializer.api.freq.OrderFrequenciesCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class OrderFrequenciesLoader extends BaseCsvLoader {
	
	@Autowired
	@Qualifier("orderService")
	private OrderService service;
	
	@Override
	public String getDomain() {
		return InitializerConstants.DOMAIN_FREQ;
	}
	
	@Override
	public Integer getOrder() {
		return 13;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public CsvParser getParser(InputStream is) throws IOException {
		return new OrderFrequenciesCsvParser(is, service);
	}
	
}
